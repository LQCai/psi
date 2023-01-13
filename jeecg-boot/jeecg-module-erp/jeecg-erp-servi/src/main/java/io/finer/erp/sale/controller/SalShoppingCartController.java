package io.finer.erp.sale.controller;

import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.service.IBasCustomerService;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.sale.enums.SalShoppingCartStatusEnum;
import io.finer.erp.sale.vo.SalShoppingCartVo;
import io.finer.erp.sale.vo.SalShoppingCartWithStkInventoryVo;
import io.finer.erp.stock.service.IStkInventoryService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import io.finer.erp.sale.entity.SalShoppingCart;
import io.finer.erp.sale.service.ISalShoppingCartService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 购物车
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="购物车")
@RestController
@RequestMapping("/sale/salShoppingCart")
public class SalShoppingCartController extends JeecgController<SalShoppingCart, ISalShoppingCartService> {
	@Autowired
	private ISalShoppingCartService salShoppingCartService;
	@Autowired
	private IBasMaterialService basMaterialService;
	@Autowired
	private IStkInventoryService stkInventoryService;
	@Autowired
	private IBasCustomerService basCustomerService;
	
	/**
	 * 分页列表查询
	 *
	 * @param salShoppingCart
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "购物车-分页列表查询")
	@ApiOperation(value="购物车-分页列表查询", notes="购物车-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SalShoppingCart salShoppingCart,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SalShoppingCart> queryWrapper = QueryGenerator.initQueryWrapper(salShoppingCart, req.getParameterMap());

		// 近显示当前用户关联的数据
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		queryWrapper.lambda().eq(SalShoppingCart::getOperator, sysUser.getUsername());

		Page<SalShoppingCart> page = new Page<>(pageNo, pageSize);
		IPage<SalShoppingCart> pageList = salShoppingCartService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param salShoppingCart
	 * @return
	 */
	@AutoLog(value = "购物车-添加")
	@ApiOperation(value="购物车-添加", notes="购物车-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SalShoppingCart salShoppingCart) {
		// 如果业务员为空, 将当前用户作为业务员
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (ObjectUtil.isEmpty(salShoppingCart.getOperator())) {
			salShoppingCart.setOperator(sysUser.getUsername());
		}

		salShoppingCart.setStatus(SalShoppingCartStatusEnum.NORMAL.getStatus());
		// 如果没有输入报价 => 报价填入商品销售价格
		if (ObjectUtil.isEmpty(salShoppingCart.getQuotedAmt())) {
			BasMaterial material = basMaterialService.getById(salShoppingCart.getMaterialId());
			if (ObjectUtil.isEmpty(material)) {
				Result.error("商品不存在!");
			}
			if (ObjectUtil.isEmpty(material.getSalePrice())) {
				Result.error("商品销售价格未配置!");
			}
			salShoppingCart.setQuotedAmt(material.getSalePrice());
		}
		salShoppingCartService.save(salShoppingCart);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param salShoppingCart
	 * @return
	 */
	@AutoLog(value = "购物车-编辑")
	@ApiOperation(value="购物车-编辑", notes="购物车-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SalShoppingCart salShoppingCart) {
		salShoppingCartService.updateById(salShoppingCart);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "购物车-通过id删除")
	@ApiOperation(value="购物车-通过id删除", notes="购物车-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		salShoppingCartService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "购物车-批量删除")
	@ApiOperation(value="购物车-批量删除", notes="购物车-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salShoppingCartService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "购物车-通过id查询")
	@ApiOperation(value="购物车-通过id查询", notes="购物车-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SalShoppingCart salShoppingCart = salShoppingCartService.getById(id);
		return Result.OK(salShoppingCart);
	}

	 /**
	  * 分页列表根据客户分组
	  *
	  * @param salShoppingCart
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "购物车-分页列表根据客户分组")
	 @ApiOperation(value="购物车-分页列表根据客户分组", notes="购物车-分页列表根据客户分组")
	 @GetMapping(value = "/listGroupByCustomer")
	 public Result<?> queryPageListGroupByCustomer(SalShoppingCart salShoppingCart,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<SalShoppingCart> queryWrapper = QueryGenerator.initQueryWrapper(salShoppingCart, req.getParameterMap());

		 // 仅显示当前用户关联的数据
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 queryWrapper.lambda().eq(SalShoppingCart::getOperator, sysUser.getUsername());

		 Page<SalShoppingCart> page = new Page<>(pageNo, pageSize);
		 IPage<SalShoppingCart> pageList = salShoppingCartService.page(page, queryWrapper);

		 List<SalShoppingCart> salShoppingCartList = pageList.getRecords();
		 List<String> customerIdList = salShoppingCartList.stream().map(SalShoppingCart::getCustomerId).distinct().collect(Collectors.toList());
		 if (customerIdList.size() == 0) {
			 return Result.ok(page);
		 }

		 List<Map<String, Object>> stkInventoryList = stkInventoryService.summaryListInMaterialIds(salShoppingCartList.stream().map(SalShoppingCart::getMaterialId).collect(Collectors.toList()));
		 List<SalShoppingCartWithStkInventoryVo> salShoppingCartWithStkInventoryVoList = salShoppingCartList.stream().map(item -> BeanUtil.copyProperties(item, SalShoppingCartWithStkInventoryVo.class)).collect(Collectors.toList());
		 salShoppingCartWithStkInventoryVoList.forEach(item -> {
			 for (Map<String, Object> stkInventory: stkInventoryList) {
				 if (item.getMaterialId().equals(stkInventory.get("material_id"))) {
					 item.setStkInventory(stkInventory);
					 break;
				 }
			 }
		 });

		 List<BasCustomer> customerList = basCustomerService.list(Wrappers.<BasCustomer>lambdaQuery()
				 .in(BasCustomer::getId, customerIdList)
		 );
		 List<SalShoppingCartVo> salShoppingCartVoList = new ArrayList<>();
		 salShoppingCartWithStkInventoryVoList.forEach(item -> {
			 for (BasCustomer customer : customerList) {
				 if (customer.getId().equals(item.getCustomerId())) {
					 if (salShoppingCartVoList.size() == 0) {
						 SalShoppingCartVo salShoppingCartVo = BeanUtil.copyProperties(customer, SalShoppingCartVo.class);
						 salShoppingCartVo.setSalShoppingCartList(new ArrayList<SalShoppingCart>() {{
							 add(item);
						 }});
						 salShoppingCartVoList.add(salShoppingCartVo);
						 break;
					 }

					 if (!salShoppingCartVoList.stream().map(SalShoppingCartVo::getId).collect(Collectors.toList()).contains(customer.getId())) {
						 SalShoppingCartVo salShoppingCartVo = BeanUtil.copyProperties(customer, SalShoppingCartVo.class);
						 salShoppingCartVo.setSalShoppingCartList(new ArrayList<SalShoppingCart>() {{
							 add(item);
						 }});
						 salShoppingCartVoList.add(salShoppingCartVo);
						 break;
					 }
					 for (SalShoppingCartVo salShoppingCartVo: salShoppingCartVoList) {
						 if (salShoppingCartVo.getId().equals(customer.getId())) {
							 salShoppingCartVo.getSalShoppingCartList().add(item);
							 break;
						 }
					 }
				 }
			 }
		 });
		 IPage<SalShoppingCartVo> list = new Page(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
		 list.setRecords(salShoppingCartVoList);

		 return Result.OK(list);
	 }

}
