package io.finer.erp.sale.controller;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.entity.BasUnit;
import io.finer.erp.base.service.IBasCustomerService;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.base.service.IBasUnitService;
import io.finer.erp.base.vo.BasMaterialStatisticsVo;
import io.finer.erp.sale.entity.SalShoppingCart;
import io.finer.erp.sale.enums.SalShoppingCartStatusEnum;
import io.finer.erp.sale.service.ISalShoppingCartService;
import io.finer.erp.sale.vo.SalInquiryVo;
import io.finer.erp.sale.vo.SalInquiryWithOtherInfoVo;
import io.finer.erp.sale.vo.SalShoppingCartVo;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.sale.entity.SalInquiry;
import io.finer.erp.sale.service.ISalInquiryService;

import java.util.stream.Collectors;

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
 * @Description: 询盘
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="询盘")
@RestController
@RequestMapping("/sale/salInquiry")
public class SalInquiryController extends JeecgController<SalInquiry, ISalInquiryService> {
	@Autowired
	private ISalInquiryService salInquiryService;
	@Autowired
	private ISalShoppingCartService salShoppingCartService;
	@Autowired
	private IBasCustomerService basCustomerService;
	@Autowired
	private IBasMaterialService basMaterialService;
	@Autowired
	private IBasUnitService basUnitService;
	
	/**
	 * 分页列表查询
	 *
	 * @param salInquiry
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "询盘-分页列表查询")
	@ApiOperation(value="询盘-分页列表查询", notes="询盘-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SalInquiry salInquiry,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SalInquiry> queryWrapper = QueryGenerator.initQueryWrapper(salInquiry, req.getParameterMap());

		// 如果当前用户只是普通用户, 仅显示属于与他关联的询盘信息
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if(oConvertUtils.isNotEmpty(sysUser.getUserIdentity()) && sysUser.getUserIdentity().equals(CommonConstant.USER_IDENTITY_1)) {
			queryWrapper.lambda().eq(SalInquiry::getOperator, sysUser.getUsername());
		}

		Page<SalInquiry> page = new Page<SalInquiry>(pageNo, pageSize);
		IPage<SalInquiry> pageList = salInquiryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "询盘-添加")
	@ApiOperation(value="询盘-添加", notes="询盘-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SalInquiry salInquiry) {
		salInquiryService.save(salInquiry);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "询盘-编辑")
	@ApiOperation(value="询盘-编辑", notes="询盘-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SalInquiry salInquiry) {
		salInquiryService.updateById(salInquiry);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "询盘-通过id删除")
	@ApiOperation(value="询盘-通过id删除", notes="询盘-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		salInquiryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "询盘-批量删除")
	@ApiOperation(value="询盘-批量删除", notes="询盘-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salInquiryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "询盘-通过id查询")
	@ApiOperation(value="询盘-通过id查询", notes="询盘-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SalInquiry salInquiry = salInquiryService.getById(id);
		return Result.OK(salInquiry);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param salInquiry
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SalInquiry salInquiry) {
      return super.exportXls(request, salInquiry, SalInquiry.class, "询盘");
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
      return super.importExcel(request, response, SalInquiry.class);
  }

	 /**
	  * 添加至购物车
	  *
	  * @param salInquiry
	  * @return
	  */
	 @AutoLog(value = "询盘-添加至购物车")
	 @ApiOperation(value="询盘-添加至购物车", notes="询盘-添加至购物车")
	 @PostMapping(value = "/addToShoppingCart")
	 @Transactional
	 public Result<?> addToShoppingCart(@RequestBody SalInquiry salInquiry) {
		 // 如果业务员为空, 将当前用户作为业务员
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if (ObjectUtil.isEmpty(salInquiry.getOperator())) {
			 salInquiry.setOperator(sysUser.getUsername());
		 }

		 // 如果没有输入报价 => 报价填入商品金额
		 if (ObjectUtil.isEmpty(salInquiry.getQuotedAmt())) {
			 salInquiry.setQuotedAmt(salInquiry.getMaterialAmt());
		 }

		 salInquiryService.save(salInquiry);
		 SalShoppingCart salShoppingCart = new SalShoppingCart() {{
			 setCustomerId(salInquiry.getCustomerId());
			 setMaterialId(salInquiry.getMaterialId());
			 setMaterialCount(salInquiry.getMaterialCount());
			 setOperator(salInquiry.getOperator());
			 setQuotedAmt(salInquiry.getQuotedAmt());
			 setMaterialAmt(salInquiry.getMaterialAmt());
			 setInquiryId(salInquiry.getId());
			 setStatus(SalShoppingCartStatusEnum.NORMAL.getStatus());
		 }};
		 salShoppingCartService.save(salShoppingCart);
		 return Result.OK("添加成功！");
	 }

	 @AutoLog(value = "询盘-审核")
	 @ApiOperation(value="询盘-审核", notes="询盘-审核")
	 @PostMapping(value = "/check")
	 @Transactional
	 public Result<?> check(@RequestParam(name="ids") String ids,
							@RequestParam(name="approvalResultType") String approvalResultType,
							@RequestParam(name="approvalRemark") String approvalRemark) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 List<SalInquiry> salInquiryList = salInquiryService.list(Wrappers.<SalInquiry>lambdaQuery()
				 .in(SalInquiry::getId, Arrays.asList(ids.split(",")))
		 );
		 for (SalInquiry salInquiry: salInquiryList) {
			 if (ObjectUtil.isNotEmpty(salInquiry.getApprovalResultType())) {
				 return Result.error("存在已审核的数据, 请检查勾选数据!");
			 }
			 salInquiry.setApprovalResultType(approvalResultType);
			 salInquiry.setApprovalRemark(approvalRemark);
			 salInquiry.setApprover(sysUser.getUsername());
		 }
		 salInquiryService.saveOrUpdateBatch(salInquiryList);
		 return Result.ok("审核提交成功!");
	 }

	 /**
	  * 战败记录
	  *
	  * @param salInquiry
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "询盘 - 战败记录")
	 @ApiOperation(value="询盘 - 战败记录", notes="询盘 - 战败记录")
	 @GetMapping(value = "/listFail")
	 public Result<?> queryListFail(SalInquiry salInquiry,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		 QueryWrapper<SalInquiry> queryWrapper = QueryGenerator.initQueryWrapper(salInquiry, req.getParameterMap());

		 // 仅显示当前用户关联的数据
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 queryWrapper.lambda().eq(SalInquiry::getOperator, sysUser.getUsername());

		 // 仅获取失败记录
		 queryWrapper.lambda().eq(SalInquiry::getStatus, 2);

		 Page<SalInquiry> page = new Page<>(pageNo, pageSize);
		 IPage<SalInquiry> pageList = salInquiryService.page(page, queryWrapper);

		 List<SalInquiry> salInquiryList = pageList.getRecords();
		 List<String> customerIdList = salInquiryList.stream().map(SalInquiry::getCustomerId).distinct().collect(Collectors.toList());
		 if (customerIdList.size() == 0) {
			 return Result.ok(page);
		 }

		 List<String> materialIdList = salInquiryList.stream().map(SalInquiry::getMaterialId).distinct().collect(Collectors.toList());
		 List<BasMaterial> basMaterialList = basMaterialService.list(Wrappers.<BasMaterial>lambdaQuery()
				 .in(BasMaterial::getId, materialIdList)
		 );

		 // 填充询盘物料对应logo
		 List<SalInquiryWithOtherInfoVo> salInquiryWithOtherInfoVoList = salInquiryList.stream().map(item -> {
			 SalInquiryWithOtherInfoVo salInquiryVo = BeanUtil.copyProperties(item, SalInquiryWithOtherInfoVo.class);

			 basMaterialList.forEach(material -> {
				 if (material.getId().equals(item.getMaterialId())) {
					 salInquiryVo.setMaterialLogo(material.getLogo());
				 }
			 });

			 return salInquiryVo;
		 }).collect(Collectors.toList());

		 List<BasCustomer> customerList = basCustomerService.list(Wrappers.<BasCustomer>lambdaQuery()
				 .in(BasCustomer::getId, customerIdList)
		 );
		 List<SalInquiryVo> salInquiryVoList = new ArrayList<>();
		 salInquiryWithOtherInfoVoList.forEach(item -> {
			 for (BasCustomer customer : customerList) {
				 if (customer.getId().equals(item.getCustomerId())) {
					 if (salInquiryVoList.size() == 0) {
						 SalInquiryVo salInquiryVo = BeanUtil.copyProperties(customer, SalInquiryVo.class);
						 salInquiryVo.setSalInquiryList(new ArrayList<SalInquiry>() {{
							 add(item);
						 }});
						 salInquiryVoList.add(salInquiryVo);
						 break;
					 }

					 if (!salInquiryVoList.stream().map(SalInquiryVo::getId).collect(Collectors.toList()).contains(customer.getId())) {
						 SalInquiryVo salInquiryVo = BeanUtil.copyProperties(customer, SalInquiryVo.class);
						 salInquiryVo.setSalInquiryList(new ArrayList<SalInquiry>() {{
							 add(item);
						 }});
						 salInquiryVoList.add(salInquiryVo);
						 break;
					 }
					 for (SalInquiryVo salInquiryVo: salInquiryVoList) {
						 if (salInquiryVo.getId().equals(customer.getId())) {
							 salInquiryVo.getSalInquiryList().add(item);
							 break;
						 }
					 }
				 }
			 }
		 });
		 IPage<SalInquiryVo> list = new Page(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
		 list.setRecords(salInquiryVoList);

		 return Result.OK(list);
	 }

	 /**
	  * 询盘 - 统计物料
	  *
	  * @return
	  */
	 @ApiOperation(value="询盘 - 统计物料", notes="询盘 - 统计物料")
	 @GetMapping(value = "/statisticsMaterial")
	 public Result<List<BasMaterialStatisticsVo>> queryStatisticsMaterial() {
		 Date now = new Date();
		 List<SalInquiry> salInquiryList = salInquiryService.list(Wrappers.<SalInquiry>lambdaQuery()
				 .between(SalInquiry::getCreateTime, DateUtil.beginOfDay(DateUtil.offsetDay(now, -7)), DateUtil.endOfDay(now))
				 .orderByDesc(SalInquiry::getCreateTime)
		 );

		 if (salInquiryList.size() == 0) {
			 return Result.ok(new ArrayList<>());
		 }

		 List<BasUnit> basUnitList = basUnitService.list();

		 List<BasMaterial> basMaterialList = basMaterialService.list(Wrappers.<BasMaterial>lambdaQuery()
				 .in(BasMaterial::getId, salInquiryList.stream().map(SalInquiry::getMaterialId).collect(Collectors.toList()))
		 );
		 List<BasMaterialStatisticsVo> basMaterialStatisticsVoList = new ArrayList<>();
		 for (BasMaterial basMaterial: basMaterialList) {
			 BasMaterialStatisticsVo basMaterialStatisticsVo = BeanUtil.copyProperties(basMaterial, BasMaterialStatisticsVo.class);
			 basMaterialStatisticsVo.setSalInquiryQty(new BigDecimal(0));
			 salInquiryList.forEach(salInquiry -> {
				 if (salInquiry.getMaterialId().equals(basMaterial.getId())) {
					 basMaterialStatisticsVo.setSalInquiryQty(basMaterialStatisticsVo.getSalInquiryQty().add(new BigDecimal(salInquiry.getMaterialCount())));
				 }
			 });

			 basUnitList.forEach(basUnit -> {
				 if (basUnit.getId().equals(basMaterial.getUnitId())) {
					 basMaterialStatisticsVo.setUnitName(basUnit.getName());
				 }
			 });
			 basMaterialStatisticsVoList.add(basMaterialStatisticsVo);
		 };

		 basMaterialStatisticsVoList.sort(Comparator.comparing(BasMaterialStatisticsVo::getSalInquiryQty).reversed());

		 return Result.ok(basMaterialStatisticsVoList);
	 }

}
