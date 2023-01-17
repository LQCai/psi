package io.finer.erp.base.vo;

import io.finer.erp.base.entity.BasMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bas_material统计Vo对象", description="物料")
public class BasMaterialStatisticsVo extends BasMaterial {
    @ApiModelProperty(value = "询盘数量")
    private BigDecimal salInquiryQty;

    @ApiModelProperty(value = "单位名称")
    private String unitName;
}
