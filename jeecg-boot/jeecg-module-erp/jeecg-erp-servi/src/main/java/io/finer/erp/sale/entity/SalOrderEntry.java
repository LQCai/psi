package io.finer.erp.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="sal_order_entry对象", description="明细")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_order_entry")
public class SalOrderEntry extends Entry {
	/**物料*/
    @Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "物料")
    private String materialId;
	/**计量单位*/
    @Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "计量单位")
    private String unitId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**税率%*/
    @Excel(name = "税率%", width = 15)
    @ApiModelProperty(value = "税率%")
    private java.math.BigDecimal taxRate;
	/**含税单价*/
	@Excel(name = "含税单价", width = 15)
    @ApiModelProperty(value = "含税单价")
    private java.math.BigDecimal price;
	/**折扣率%*/
	@Excel(name = "折扣率%", width = 15)
    @ApiModelProperty(value = "折扣率%")
    private java.math.BigDecimal discountRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @ApiModelProperty(value = "税额")
    private java.math.BigDecimal tax;
	/**含税金额*/
	@Excel(name = "含税金额", width = 15)
    @ApiModelProperty(value = "含税金额")
    private java.math.BigDecimal amt;
    /**已出库数量*/
    @Excel(name = "已出库数量", width = 15)
    @ApiModelProperty(value = "已出库数量")
    private java.math.BigDecimal outQty;
    /**已出库成本*/
    @Excel(name = "已出库成本", width = 15)
    @ApiModelProperty(value = "已出库成本")
    private java.math.BigDecimal outCost;
    /**结算数量*/
    @Excel(name = "结算数量", width = 15)
    @ApiModelProperty(value = "结算数量")
    private java.math.BigDecimal settleQty;
    /**结算金额*/
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settleAmt;
 	/**已开票数量*/
	@Excel(name = "已开票数量", width = 15)
    @ApiModelProperty(value = "已开票数量")
    private java.math.BigDecimal invoicedQty;
	/**已开票金额*/
	@Excel(name = "已开票金额", width = 15)
    @ApiModelProperty(value = "已开票金额")
    private java.math.BigDecimal invoicedAmt;
    /**不含税结算金额*/
    @Excel(name = "不含税结算金额", width = 15)
    @ApiModelProperty(value = "不含税结算金额")
    private java.math.BigDecimal exTaxSettleAmt;
    /**毛利润*/
    @Excel(name = "毛利润", width = 15)
    @ApiModelProperty(value = "毛利润")
    private java.math.BigDecimal grossProfit;
}
