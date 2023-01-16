package io.finer.erp.sale.vo;

import io.finer.erp.sale.entity.SalShoppingCart;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SalShoppingCartWithOtherInfoVo extends SalShoppingCart {
    @ApiModelProperty(value = "库存信息")
    private Map<String, Object> stkInventory;
    @ApiModelProperty(value = "物料logo")
    private String materialLogo;
}
