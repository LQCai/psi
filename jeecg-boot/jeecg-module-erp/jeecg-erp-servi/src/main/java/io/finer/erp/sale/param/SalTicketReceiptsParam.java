package io.finer.erp.sale.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalTicketReceiptsParam {
    @ApiModelProperty(value = "收款金额")
    private BigDecimal receiptAmt;
    @ApiModelProperty(value = "结算方式")
    private String settleMethod;
    @ApiModelProperty(value = "资金账户")
    private String bankAccountId;

    private String id;
}
