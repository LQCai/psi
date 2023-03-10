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
 * @Description: ??????
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="??????")
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
	 * ??????????????????
	 *
	 * @param salInquiry
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "??????-??????????????????")
	@ApiOperation(value="??????-??????????????????", notes="??????-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SalInquiry salInquiry,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SalInquiry> queryWrapper = QueryGenerator.initQueryWrapper(salInquiry, req.getParameterMap());

		// ????????????????????????????????????, ??????????????????????????????????????????
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if(oConvertUtils.isNotEmpty(sysUser.getUserIdentity()) && sysUser.getUserIdentity().equals(CommonConstant.USER_IDENTITY_1)) {
			queryWrapper.lambda().eq(SalInquiry::getOperator, sysUser.getUsername());
		}

		Page<SalInquiry> page = new Page<SalInquiry>(pageNo, pageSize);
		IPage<SalInquiry> pageList = salInquiryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * ??????
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "??????-??????")
	@ApiOperation(value="??????-??????", notes="??????-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SalInquiry salInquiry) {
		salInquiryService.save(salInquiry);
		return Result.OK("???????????????");
	}
	
	/**
	 * ??????
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "??????-??????")
	@ApiOperation(value="??????-??????", notes="??????-??????")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SalInquiry salInquiry) {
		salInquiryService.updateById(salInquiry);
		return Result.OK("????????????!");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????-??????id??????")
	@ApiOperation(value="??????-??????id??????", notes="??????-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		salInquiryService.removeById(id);
		return Result.OK("????????????!");
	}
	
	/**
	 * ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "??????-????????????")
	@ApiOperation(value="??????-????????????", notes="??????-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salInquiryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("?????????????????????");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????-??????id??????")
	@ApiOperation(value="??????-??????id??????", notes="??????-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SalInquiry salInquiry = salInquiryService.getById(id);
		return Result.OK(salInquiry);
	}

  /**
   * ??????excel
   *
   * @param request
   * @param salInquiry
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SalInquiry salInquiry) {
      return super.exportXls(request, salInquiry, SalInquiry.class, "??????");
  }

  /**
   * ??????excel????????????
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
	  * ??????????????????
	  *
	  * @param salInquiry
	  * @return
	  */
	 @AutoLog(value = "??????-??????????????????")
	 @ApiOperation(value="??????-??????????????????", notes="??????-??????????????????")
	 @PostMapping(value = "/addToShoppingCart")
	 @Transactional
	 public Result<?> addToShoppingCart(@RequestBody SalInquiry salInquiry) {
		 // ?????????????????????, ??????????????????????????????
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if (ObjectUtil.isEmpty(salInquiry.getOperator())) {
			 salInquiry.setOperator(sysUser.getUsername());
		 }

		 // ???????????????????????? => ????????????????????????
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
		 return Result.OK("???????????????");
	 }

	 @AutoLog(value = "??????-??????")
	 @ApiOperation(value="??????-??????", notes="??????-??????")
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
				 return Result.error("????????????????????????, ?????????????????????!");
			 }
			 salInquiry.setApprovalResultType(approvalResultType);
			 salInquiry.setApprovalRemark(approvalRemark);
			 salInquiry.setApprover(sysUser.getUsername());
		 }
		 salInquiryService.saveOrUpdateBatch(salInquiryList);
		 return Result.ok("??????????????????!");
	 }

	 /**
	  * ????????????
	  *
	  * @param salInquiry
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "?????? - ????????????")
	 @ApiOperation(value="?????? - ????????????", notes="?????? - ????????????")
	 @GetMapping(value = "/listFail")
	 public Result<?> queryListFail(SalInquiry salInquiry,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		 QueryWrapper<SalInquiry> queryWrapper = QueryGenerator.initQueryWrapper(salInquiry, req.getParameterMap());

		 // ????????????????????????????????????
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 queryWrapper.lambda().eq(SalInquiry::getOperator, sysUser.getUsername());

		 // ?????????????????????
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

		 // ????????????????????????logo
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
	  * ?????? - ????????????
	  *
	  * @return
	  */
	 @ApiOperation(value="?????? - ????????????", notes="?????? - ????????????")
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
