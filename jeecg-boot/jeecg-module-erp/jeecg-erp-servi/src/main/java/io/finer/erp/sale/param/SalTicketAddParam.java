package io.finer.erp.sale.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalTicketAddParam {
    /**结算方式*/
    @ApiModelProperty(value = "结算方式")
    private java.lang.Integer billingMethod;
    /**付款方式*/
    @ApiModelProperty(value = "付款方式")
    private java.lang.Integer paymentsMethod;
    /**开票方式*/
    @ApiModelProperty(value = "开票方式")
    private java.lang.Integer invoiceType;
    /**购物车ids*/
    @ApiModelProperty(value = "购物车ids(逗号分隔的字符串)")
    private java.lang.String shoppingCartIds;
}
