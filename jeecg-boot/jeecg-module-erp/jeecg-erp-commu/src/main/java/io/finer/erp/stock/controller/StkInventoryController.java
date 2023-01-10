package io.finer.erp.stock.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.entity.BasMaterialCategory;
import io.finer.erp.base.service.IBasMaterialCategoryService;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.service.IStkInventoryService;
import io.finer.erp.stock.vo.BasMaterialCategoryVo;
import io.finer.erp.stock.vo.BasMaterialVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 库存
 * @Author:
 * @Date:
 * @Version:
 */
@Api(tags="库存")
@RestController
@RequestMapping("/stock/stkInventory")
@Slf4j
public class StkInventoryController extends JeecgController<StkInventory, IStkInventoryService> {

	@Autowired
	private IStkInventoryService stkInventoryService;
	@Autowired
	private IBasMaterialService basMaterialService;
	@Autowired
	private IBasMaterialCategoryService basMaterialCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param stkInventory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "库存-分页列表查询")
	@ApiOperation(value="库存-分页列表查询", notes="库存-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StkInventory stkInventory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StkInventory> queryWrapper = QueryGenerator.initQueryWrapper(stkInventory, req.getParameterMap());
		Page<StkInventory> page = new Page<StkInventory>(pageNo, pageSize);
		IPage<StkInventory> pageList = stkInventoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "库存-通过id查询")
	 @ApiOperation(value="库存-通过id查询", notes="库存-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 StkInventory stkInventory = stkInventoryService.getById(id);
		 if(stkInventory==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(stkInventory);
	 }


	 /**
	 *  编辑
	 *
	 * @param stkInventory
	 * @return
	 */
	@AutoLog(value = "库存-编辑")
	@ApiOperation(value="库存-编辑", notes="库存-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody StkInventory stkInventory) {
		stkInventoryService.updateById(stkInventory);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "库存-通过id删除")
	@ApiOperation(value="库存-通过id删除", notes="库存-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		stkInventoryService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "库存-批量删除")
	@ApiOperation(value="库存-批量删除", notes="库存-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stkInventoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stkInventory
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StkInventory stkInventory) {
        return super.exportXls(request, stkInventory, StkInventory.class, "详细即时库存");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@AutoLog(value = "通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, StkInventory.class);
    }

	@ApiOperation(value = "库存 - 物料分组列表", notes = "库存 - 物料分组列表")
	@GetMapping(value = "/materialList")
	public Result<?> materialList() {
		List<BasMaterialCategory> basMaterialCategorieList = basMaterialCategoryService.list(Wrappers.<BasMaterialCategory>lambdaQuery()
				.eq(BasMaterialCategory::getIsEnabled, 1)
		);
		List<BasMaterial> list = basMaterialService.list(Wrappers.<BasMaterial>lambdaQuery()
				.eq(BasMaterial::getIsEnabled, 1)
		);
		List<Map<String, Object>> stkInventoryList = stkInventoryService.summaryList();

		List<BasMaterialVo> basMaterialVoList = new ArrayList<>();
		list.forEach(item -> {
			BasMaterialVo basMaterialVo = BeanUtil.copyProperties(item, BasMaterialVo.class);
			for (Map<String, Object> stkInventory: stkInventoryList) {
				if (basMaterialVo.getId().equals(stkInventory.get("material_id"))) {
					basMaterialVo.setStkInventory(stkInventory);
					break;
				}
			}
			basMaterialVoList.add(basMaterialVo);
		});

		List<BasMaterialCategoryVo> dataList = new ArrayList<>();
		for (BasMaterialCategory basMaterialCategory : basMaterialCategorieList) {
			BasMaterialCategoryVo basMaterialCategoryVo = BeanUtil.copyProperties(basMaterialCategory, BasMaterialCategoryVo.class);
			List<BasMaterialVo> infoList = new ArrayList<>();
			for (BasMaterialVo basMaterial : basMaterialVoList) {
				if (basMaterial.getCategoryId().equals(basMaterialCategory.getId())) {
					infoList.add(basMaterial);
				}
			}
			basMaterialCategoryVo.setBasMaterialList(infoList);
			dataList.add(basMaterialCategoryVo);
		}
		return Result.OK(dataList);
	}

}
