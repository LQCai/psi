package io.finer.erp.sale.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.sale.entity.SalInquiry;
import io.finer.erp.sale.enums.SalTicketStatusEnum;
import io.finer.erp.sale.param.SalTicketAddParam;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.sale.entity.SalTicket;
import io.finer.erp.sale.service.ISalTicketService;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
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
    @ApiOperation(value = "订单-立即下单", notes = "订单-立即下单")
    @PostMapping(value = "/addImmediately")
    public Result<?> addImmediately(@RequestBody SalTicket salTicket) {
        salTicket.setNo(salTicketService.generalNo());
        salTicket.setSrcNo(salTicketService.generalSrcNo());
        salTicket.setNoDate(DateUtils.getDate());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(salTicket.getOperator())) {
            salTicket.setOperator(sysUser.getUsername());
        }

        // 如果报价金额低于商品金额 => 需要审核
        if (salTicket.getQuotedAmt().compareTo(salTicket.getMaterialAmt()) < 0) {
            salTicket.setStatus(SalTicketStatusEnum.INIT.getStatus());
        } else {
            salTicket.setTotalAmt(salTicket.getMaterialAmt().multiply(new BigDecimal(salTicket.getMaterialCount())));
            salTicket.setStatus(SalTicketStatusEnum.TO_BE_SHIPPED.getStatus());
        }

        salTicketService.save(salTicket);
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
    public Result<?> addFromShoppingCart(@RequestBody SalTicketAddParam salTicket) {
        return salTicketService.addFromShoppingCart(salTicket);
    }

    @AutoLog(value = "订单-审核")
    @ApiOperation(value="订单-审核", notes="订单-审核")
    @PostMapping(value = "/check")
    @Transactional
    public Result<?> check(@RequestParam(name="ids") String ids,
                           @RequestParam(name="approvalResultType") String approvalResultType,
                           @RequestParam(name="approvalRemark") String approvalRemark) {
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
        }
        salTicketService.saveOrUpdateBatch(salTicketList);
        return Result.ok("审核成功!");
    }

}
