package io.finer.erp.sale.controller;

import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.finance.entity.FinReceipt;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.finer.erp.finance.entity.FinReceivable;
import io.finer.erp.finance.service.IFinReceiptService;
import io.finer.erp.finance.service.IFinReceivableService;
import io.finer.erp.sale.enums.SalTicketStatusEnum;
import io.finer.erp.sale.param.SalTicketAddParam;
import io.finer.erp.sale.param.SalTicketDeliveryParam;
import io.finer.erp.sale.param.SalTicketDeliveryUpdateParam;
import io.finer.erp.sale.param.SalTicketReceiptsParam;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.FillRuleUtil;
import io.finer.erp.sale.entity.SalTicket;
import io.finer.erp.sale.service.ISalTicketService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "订单")
@RestController
@RequestMapping("/sale/salTicket")
public class SalTicketController extends JeecgController<SalTicket, ISalTicketService> {
    @Autowired
    private ISalTicketService salTicketService;
    @Autowired
    private IStkIoService stkIoService;
    @Autowired
    private IFinReceivableService finReceivableService;
    @Autowired
    private IFinReceiptService finReceiptService;

    /**
     * 分页列表查询
     *
     * @param salTicket
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "订单-分页列表查询")
    @ApiOperation(value = "订单-分页列表查询", notes = "订单-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SalTicket salTicket,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SalTicket> queryWrapper = QueryGenerator.initQueryWrapper(salTicket, req.getParameterMap());
        Page<SalTicket> page = new Page<SalTicket>(pageNo, pageSize);
        IPage<SalTicket> pageList = salTicketService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param salTicket
     * @return
     */
    @AutoLog(value = "订单-添加")
    @ApiOperation(value = "订单-添加", notes = "订单-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SalTicket salTicket) {
        salTicketService.save(salTicket);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param salTicket
     * @return
     */
    @AutoLog(value = "订单-编辑")
    @ApiOperation(value = "订单-编辑", notes = "订单-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody SalTicket salTicket) {
        salTicketService.updateById(salTicket);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单-通过id删除")
    @ApiOperation(value = "订单-通过id删除", notes = "订单-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        salTicketService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "订单-批量删除")
    @ApiOperation(value = "订单-批量删除", notes = "订单-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.salTicketService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单-通过id查询")
    @ApiOperation(value = "订单-通过id查询", notes = "订单-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SalTicket salTicket = salTicketService.getById(id);
        return Result.OK(salTicket);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param salTicket
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SalTicket salTicket) {
        return super.exportXls(request, salTicket, SalTicket.class, "订单");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SalTicket.class);
    }

    /**
     * 立即下单
     *
     * @param salTicket 订单实体
     * @return
     */
    @AutoLog(value = "订单-立即下单")
    @Transactional
    @ApiOperation(value = "订单-立即下单", notes = "订单-立即下单")
    @PostMapping(value = "/addImmediately")
    public Result<?> addImmediately(@RequestBody SalTicket salTicket) throws Exception {
        Object noObj = FillRuleUtil.executeRule("stk_xsck_custom_bill_no", new JSONObject());
        if (ObjectUtil.isEmpty(noObj)) {
            return Result.error("未配置订单号规则，请联系管理员!");
        }

        assert noObj != null;
        salTicket.setNo(noObj.toString());
        salTicket.setSrcNo(salTicketService.generalSrcNo());
        salTicket.setNoDate(DateUtils.getDate());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(salTicket.getOperator())) {
            salTicket.setOperator(sysUser.getUsername());
        }

        // 如果报价金额低于商品金额 => 需要审核
        if (salTicket.getQuotedAmt().compareTo(salTicket.getMaterialAmt()) < 0) {
            salTicket.setStatus(SalTicketStatusEnum.INIT.getStatus());
            salTicketService.save(salTicket);
        } else {
            salTicket.setTotalAmt(salTicket.getQuotedAmt().multiply(new BigDecimal(salTicket.getMaterialCount())));
            // 付款方式: 货到付款 => 状态变为【待发货】
            if (salTicket.getPaymentsMethod().equals(1)) {
                salTicket.setStatus(SalTicketStatusEnum.TO_BE_SHIPPED.getStatus());
                salTicketService.save(salTicket);
            } else {
                // 款到发货 => 订单状态为【待付款】
                salTicket.setStatus(SalTicketStatusEnum.TO_BE_PAYMENT.getStatus());
                salTicketService.save(salTicket);

                // 创建应收记录
                salTicketService.createReceivableBill(salTicket);
            }
        }

        return Result.OK("下单成功！");
    }

    /**
     * 购物车下单
     *
     * @param salTicket 订单实体
     * @return
     */
    @AutoLog(value = "订单-购物车下单")
    @ApiOperation(value = "订单-购物车下单", notes = "订单-购物车下单")
    @PostMapping(value = "/addFromShoppingCart")
    public Result<?> addFromShoppingCart(@RequestBody SalTicketAddParam salTicket) throws Exception {
        return salTicketService.addFromShoppingCart(salTicket);
    }

    @AutoLog(value = "订单-审核")
    @ApiOperation(value="订单-审核", notes="订单-审核")
    @PostMapping(value = "/check")
    @Transactional
    public Result<?> check(@RequestParam(name="ids") String ids,
                           @RequestParam(name="approvalResultType") String approvalResultType,
                           @RequestParam(name="approvalRemark") String approvalRemark) throws Exception {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<SalTicket> salTicketList = salTicketService.list(Wrappers.<SalTicket>lambdaQuery()
                .in(SalTicket::getId, Arrays.asList(ids.split(",")))
        );
        for (SalTicket salTicket: salTicketList) {
            if (salTicket.getQuotedAmt().compareTo(salTicket.getMaterialAmt()) >= 0) {
                return Result.error("存在不需要审核的订单, 请检查提交数据!");
            }

            if (ObjectUtil.isNotEmpty(salTicket.getApprovalResultType())) {
                return Result.error("存在已审核的订单, 请检查提交数据!");
            }
            salTicket.setApprovalResultType(approvalResultType);
            salTicket.setApprovalRemark(approvalRemark);
            salTicket.setApprover(sysUser.getUsername());
            if (salTicket.getApprovalResultType().equals("1")) {
                salTicket.setTotalAmt(salTicket.getQuotedAmt().multiply(new BigDecimal(salTicket.getMaterialCount())));
                // 付款方式: 货到付款 => 状态变为【待发货】
                if (salTicket.getPaymentsMethod().equals(1)) {
                    salTicket.setStatus(SalTicketStatusEnum.TO_BE_SHIPPED.getStatus());
                } else {
                    // 款到发货 => 订单状态为【待付款】
                    salTicket.setStatus(SalTicketStatusEnum.TO_BE_PAYMENT.getStatus());
                    // 创建应收记录
                    salTicketService.createReceivableBill(salTicket);
                }
            } else {
                salTicket.setStatus(SalTicketStatusEnum.CLOSE.getStatus());
            }
        }
        salTicketService.saveOrUpdateBatch(salTicketList);
        return Result.ok("审核成功!");
    }

    @AutoLog(value = "订单-发货")
    @ApiOperation(value = "订单-发货", notes = "订单-发货")
    @Transactional
    @PostMapping(value = "/delivery")
    public Result<?> delivery(@RequestBody SalTicketDeliveryParam salTicketDelivery) throws Exception {
        SalTicket salTicket = salTicketService.getById(salTicketDelivery.getTicketId());
        if (ObjectUtil.isEmpty(salTicket)) {
            return Result.error("订单不存在!");
        }

        if (salTicket.getStatus().equals(SalTicketStatusEnum.INIT.getStatus())) {
            return Result.error("订单尚未审核通过!");
        }
        if (salTicket.getStatus().equals(SalTicketStatusEnum.SHIPPED.getStatus())) {
            return Result.error("订单已发货!");
        }

        salTicket.setDriverName(salTicketDelivery.getDriverName());
        salTicket.setDriverTel(salTicketDelivery.getDriverTel());
        salTicket.setDriverCarNumber(salTicketDelivery.getDriverCarNumber());
        salTicket.setDriverIdCard(salTicketDelivery.getDriverIdCard());
        salTicket.setStatus(SalTicketStatusEnum.SHIPPED.getStatus());
        salTicketService.saveOrUpdate(salTicket);

        StkIo stkIo = new StkIo() {{
            setCustomerId(salTicket.getCustomerId());
            setBillNo(salTicket.getNo());
            setBillDate(salTicket.getNoDate());
            setHasRp(1);
            setHasSwell(0);
            setStockIoType("203");
            setInvoiceType(String.valueOf(salTicket.getInvoiceType()));
            setIsAuto(0);
            setIsRubric(0);
        }};
        StkIoEntry stkIoEntry = BeanUtil.copyProperties(salTicketDelivery, StkIoEntry.class);
        stkIoEntry.setEntryNo(10);
        stkIoService.submitAdd(stkIo, new ArrayList<StkIoEntry>() {{add(stkIoEntry);}});

        // 如果付款方式是货到付款 => 发货后创建应收记录
        if (salTicket.getPaymentsMethod().equals(1)) {
            salTicketService.createReceivableBill(salTicket);
        }

        return Result.OK("发货成功！");
    }

    @AutoLog(value = "订单-修改发货信息")
    @ApiOperation(value = "订单-修改发货信息", notes = "订单-修改发货信息")
    @Transactional
    @PostMapping(value = "/deliveryUpdate")
    public Result<?> deliveryUpdate(@RequestBody SalTicketDeliveryUpdateParam salTicketDelivery) {
        SalTicket salTicket = salTicketService.getById(salTicketDelivery.getId());
        if (ObjectUtil.isEmpty(salTicket)) {
            return Result.error("订单不存在!");
        }

        if (!salTicket.getStatus().equals(SalTicketStatusEnum.SHIPPED.getStatus())) {
            return Result.error("订单尚未发货!");
        }

        salTicket.setDriverName(salTicketDelivery.getDriverName());
        salTicket.setDriverTel(salTicketDelivery.getDriverTel());
        salTicket.setDriverCarNumber(salTicketDelivery.getDriverCarNumber());
        salTicket.setDriverIdCard(salTicketDelivery.getDriverIdCard());
        salTicketService.saveOrUpdate(salTicket);
        return Result.ok("修改成功!");
    }

    @AutoLog(value = "订单-确认收款")
    @ApiOperation(value = "订单-确认收款", notes = "订单-确认收款")
    @Transactional(rollbackFor = {Exception.class})
    @PostMapping(value = "/confirmReceivable")
    public Result<?> confirmReceivable(@RequestBody SalTicketReceiptsParam salTicketReceiptsParam) throws Exception {
        SalTicket salTicket = salTicketService.getById(salTicketReceiptsParam.getId());
        if (ObjectUtil.isEmpty(salTicket)) {
            return Result.error("订单不存在!");
        }

        FinReceivable finReceivable = finReceivableService.getOne(Wrappers.<FinReceivable>lambdaQuery()
                .eq(FinReceivable::getSrcBillId, salTicket.getId())
        );
        if (ObjectUtil.isEmpty(finReceivable)) {
            return Result.error("订单无需收款!");
        }
        if (finReceivable.getIsClosed().equals(1)) {
            return Result.error("订单收款已结束, 无需收款!");
        }

        if (!salTicket.getStatus().equals(SalTicketStatusEnum.TO_BE_PAYMENT.getStatus())) {
            return Result.error("订单状态错误!");
        }

        BigDecimal receivableAmt = finReceivable.getAmt();

        String billNo = (String)FillRuleUtil.executeRule("fin_xssk_bill_no", null);
        FinReceipt bill = new FinReceipt();
        // 收款类型: 订单收款
        bill.setReceiptType("100");
        bill.setAmt(salTicketReceiptsParam.getReceiptAmt());
        bill.setCustomerId(salTicket.getCustomerId());
        bill.setSrcBillId(salTicket.getId());
        bill.setBillNo(billNo);
        bill.setBillDate(new Date());

        FinReceiptEntry finReceiptEntry = new FinReceiptEntry();
        finReceiptEntry.setAmt(salTicketReceiptsParam.getReceiptAmt());
        finReceiptEntry.setBillNo(billNo);
        finReceiptEntry.setSrcBillId(salTicket.getId());
        finReceiptEntry.setSettleMethod(salTicketReceiptsParam.getSettleMethod());
        finReceiptEntry.setBankAccountId(salTicketReceiptsParam.getBankAccountId());
        finReceiptEntry.setEntryNo(10);
        // 创建收款记录
        finReceiptService.submitAdd(bill, new ArrayList<FinReceiptEntry>() {{
            add(finReceiptEntry);
        }});

        List<FinReceipt> receiptList = finReceiptService.list(Wrappers.<FinReceipt>lambdaQuery()
                .eq(FinReceipt::getSrcBillId, salTicket.getId())
        );
        // 获取已经收款的金额
        BigDecimal receiptedAmt = new BigDecimal(0);
        for (FinReceipt receipt: receiptList) {
            receiptedAmt = receiptedAmt.add(receipt.getAmt());
        }

        if (salTicket.getTotalAmt().compareTo(receiptedAmt) < 0) {
            throw new Exception("收款总金额超过订单金额!");
        } else if (salTicket.getTotalAmt().compareTo(receiptedAmt) == 0) {
            // 付款方式: 货到付款 => 状态变为【完成】
            if (salTicket.getPaymentsMethod().equals(1)) {
                salTicket.setStatus(SalTicketStatusEnum.COMPLETED.getStatus());
            } else {
                // 款到发货 => 订单状态为【待发货】
                salTicket.setStatus(SalTicketStatusEnum.TO_BE_SHIPPED.getStatus());
            }
        }
        salTicketService.saveOrUpdate(salTicket);
        return Result.ok("修改成功!");
    }

    @ApiOperation(value = "订单-收款记录", notes = "订单-收款记录")
    @GetMapping(value = "/receiptList")
    public Result<List<FinReceipt>> receiptList(@RequestParam(name = "id", required = true) String id) {
        SalTicket salTicket = salTicketService.getById(id);
        if (ObjectUtil.isEmpty(salTicket)) {
            return Result.error("订单不存在!");
        }

        List<FinReceipt> receiptList = finReceiptService.list(Wrappers.<FinReceipt>lambdaQuery()
                .eq(FinReceipt::getSrcBillId, salTicket.getId())
        );
        return Result.OK(receiptList);
    }
}
