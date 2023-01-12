package io.finer.erp.sale.vo;

import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.sale.entity.SalShoppingCart;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SalShoppingCartVo extends BasCustomer {
    @ApiModelProperty(value = "客户关联购物车列表")
    private List<SalShoppingCart> salShoppingCartList;
}
