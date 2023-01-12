package io.finer.erp.sale.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalTicketDeliveryParam {
    @ApiModelProperty(value = "驾驶人姓名")
    private String driverName;
    @ApiModelProperty(value = "驾驶人电话")
    private String driverTel;
    @ApiModelProperty(value = "驾驶人车牌号")
    private String driverCarNumber;
    @ApiModelProperty(value = "驾驶人身份证")
    private String driverIdCard;

    private String ticketId;
    private String materialId;
    private java.math.BigDecimal materialAmt;
    private Integer stockIoDirection;
    private java.math.BigDecimal qty;
    private java.math.BigDecimal settleQty;
    private java.math.BigDecimal cost;
    private java.math.BigDecimal settleAmt;
    private String warehouseId;
    private String batchNo;
    private String inventoryUnitId;
    private java.math.BigDecimal inventoryQty;
    private java.math.BigDecimal inventoryCost;
    private java.math.BigDecimal taxRate;
    private java.math.BigDecimal price;
    private String materialModel;
    private String unitId;
}
