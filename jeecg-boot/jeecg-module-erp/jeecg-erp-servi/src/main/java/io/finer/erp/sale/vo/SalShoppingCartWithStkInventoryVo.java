package io.finer.erp.sale.vo;

import io.finer.erp.sale.entity.SalShoppingCart;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SalShoppingCartWithStkInventoryVo extends SalShoppingCart {
    @ApiModelProperty(value = "库存信息")
    private Map<String, Object> stkInventory;
}
