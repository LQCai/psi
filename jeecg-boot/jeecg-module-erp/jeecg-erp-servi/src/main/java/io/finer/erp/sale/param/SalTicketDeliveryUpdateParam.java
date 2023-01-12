package io.finer.erp.sale.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalTicketDeliveryUpdateParam {
    @ApiModelProperty(value = "驾驶人姓名")
    private String driverName;
    @ApiModelProperty(value = "驾驶人电话")
    private String driverTel;
    @ApiModelProperty(value = "驾驶人车牌号")
    private String driverCarNumber;
    @ApiModelProperty(value = "驾驶人身份证")
    private String driverIdCard;

    private String id;
}
