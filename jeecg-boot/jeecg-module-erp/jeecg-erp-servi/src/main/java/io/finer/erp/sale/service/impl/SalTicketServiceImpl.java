package io.finer.erp.sale.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.finance.service.IFinReceivableService;
import io.finer.erp.sale.entity.SalShoppingCart;
import io.finer.erp.sale.entity.SalTicket;
import io.finer.erp.sale.enums.SalShoppingCartStatusEnum;
import io.finer.erp.sale.enums.SalTicketStatusEnum;
import io.finer.erp.sale.mapper.SalTicketMapper;
import io.finer.erp.sale.param.SalTicketAddParam;
import io.finer.erp.sale.service.ISalShoppingCartService;
import io.finer.erp.sale.service.ISalTicketService;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.service.IStkInventoryService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Service
public class SalTicketServiceImpl extends ServiceImpl<SalTicketMapper, SalTicket> implements ISalTicketService {

    @Autowired
    private ISalShoppingCartService salShoppingCartService;
    @Autowired
    private IFinReceivableService finReceivableService;

    @Override
    public String generalNo() {
        return "no-" + UUID.randomUUID();
    }

    @Override
    public String generalSrcNo() {
        return "src-no-" + UUID.randomUUID();
    }

    @Override
    @Transactional
    public Result<?> addFromShoppingCart(SalTicketAddParam salTicketAddParam) throws Exception {
        List<String> cartIdList = Arrays.asList(salTicketAddParam.getShoppingCartIds().split(","));
        if (cartIdList.size() == 0) {
            return Result.error("请至少选择一个商品!");
        }

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String srcNo = generalSrcNo();
        List<SalShoppingCart> salShoppingCartList = salShoppingCartService.list(Wrappers.<SalShoppingCart>lambdaQuery()
                .in(SalShoppingCart::getId, cartIdList)
        );

        List<SalShoppingCart> removeCartList = new ArrayList<>();
        List<SalTicket> salTicketList = new ArrayList<>();
        for (SalShoppingCart salShoppingCart: salShoppingCartList) {
            if (salShoppingCart.getStatus().equals(SalShoppingCartStatusEnum.EXPIRE.getStatus())) {
                return Result.error("请勿对战败商品操作!");
            }

            Object noObj = FillRuleUtil.executeRule("stk_xsck_custom_bill_no", new JSONObject());
            if (ObjectUtil.isEmpty(noObj)) {
                return Result.error("未配置订单号规则，请联系管理员!");
            }

            SalTicket salTicket = new SalTicket() {{
                assert noObj != null;
                setNo(noObj.toString());
                setNoDate(DateUtils.getDate());
                setSrcNo(srcNo);
                setCustomerId(salShoppingCart.getCustomerId());
                setMaterialId(salShoppingCart.getMaterialId());
                setMaterialAmt(salShoppingCart.getMaterialAmt());
                setMaterialCount(salShoppingCart.getMaterialCount());
                setQuotedAmt(salShoppingCart.getQuotedAmt());
                setBillingMethod(salTicketAddParam.getBillingMethod());
                setInvoiceType(salTicketAddParam.getInvoiceType());
                setPaymentsMethod(salTicketAddParam.getPaymentsMethod());
                setRemark(salShoppingCart.getRemark());
            }};

            // 设置业务员
            if (ObjectUtil.isEmpty(salShoppingCart.getOperator())) {
                salTicket.setOperator(sysUser.getUsername());
            } else {
                salTicket.setOperator(salShoppingCart.getOperator());
            }

            // 如果报价金额低于商品金额 => 需要审核
            if (salTicket.getQuotedAmt().compareTo(salTicket.getMaterialAmt()) < 0) {
                salTicket.setStatus(SalTicketStatusEnum.INIT.getStatus());
            } else {
                salTicket.setTotalAmt(salTicket.getQuotedAmt().multiply(new BigDecimal(salTicket.getMaterialCount())));

                // 付款方式: 货到付款 => 状态变为【待发货】
                if (salTicket.getPaymentsMethod().equals(1)) {
                    salTicket.setStatus(SalTicketStatusEnum.TO_BE_SHIPPED.getStatus());
                } else {
                    // 款到发货 => 订单状态为【待付款】
                    salTicket.setStatus(SalTicketStatusEnum.TO_BE_PAYMENT.getStatus());

                    // 创建应收记录
                    createReceivableBill(salTicket);
                }
            }
            salTicketList.add(salTicket);
            removeCartList.add(salShoppingCart);
        }

        // 批量创建工单
        saveBatch(salTicketList);
        // 移除购物车
        salShoppingCartService.removeBatchByIds(removeCartList);

        return Result.ok("下单成功！");
    }

    @Override
    public void createReceivableBill(SalTicket salTicket) throws Exception {
        StkIo stkIo = new StkIo() {{
            setCustomerId(salTicket.getCustomerId());
            setBillNo(salTicket.getNo());
            setBillDate(salTicket.getNoDate());
            setId(salTicket.getId());
            setHasRp(1);
            setHasSwell(0);
            setStockIoType("203");
            setSettleAmt(salTicket.getTotalAmt());
            setInvoiceType(String.valueOf(salTicket.getInvoiceType()));
            setIsAuto(0);
            setIsRubric(0);
        }};
        finReceivableService.createBill(stkIo, "102");
    }
}
