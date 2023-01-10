package io.finer.erp.stock.vo;

import io.finer.erp.base.entity.BasMaterialCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: 物料分类
 * @Author: luoqc
 * @Date:   2023-01-05
 * @Version: V1.0
 */
@Data
@ApiModel(value="bas_material_categoryPage对象", description="物料分类")
public class BasMaterialCategoryVo extends BasMaterialCategory {
	@ExcelCollection(name="物料")
	@ApiModelProperty(value = "物料")
	private List<BasMaterialVo> basMaterialList;

}
