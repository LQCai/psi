package io.finer.erp.port.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.vo.BillPage;
import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.stock.entity.StkIoEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 出入库单
 * @Author: jeecg-boot
 * @Date:   2020-03-31
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="stk_ioPage对象", description="出入库单")
public class PortIoPage extends BillPage {
	/**出入港类型*/
	@Excel(name = "出入港类型", width = 15, dicCode = "x_port_io_type")
	@Dict(dicCode = "x_port_io_type")
	@ApiModelProperty(value = "出入港类型")
	private String portIoType;
	/**是否有往来*/
//	@Excel(name = "是否有往来", width = 15, dicCode = "yn")
//	@Dict(dicCode = "yn")
//	@ApiModelProperty(value = "是否有往来")
//	private Integer hasRp;
//	/**是否有涨吨*/
//	@Excel(name = "是否有涨吨", width = 15, dicCode = "yn")
//	@Dict(dicCode = "yn")
//	@ApiModelProperty(value = "是否有涨吨")
//	private Integer hasSwell;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "供应商")
	private String supplierId;
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "客户")
	private String customerId;
	/**发票类型*/
//	@Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
//	@Dict(dicCode = "x_invoice_type")
//	@ApiModelProperty(value = "发票类型")
//	private String invoiceType;
	/**业务部门*/
	@Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@ApiModelProperty(value = "业务部门")
	private String opDept;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "业务员")
	private String operator;
	/**出入库经办*/
	@Excel(name = "出入库经办", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "出入库经办")
	private String handler;
	/**成本*/
	@Excel(name = "成本", width = 15)
	@ApiModelProperty(value = "成本")
	private java.math.BigDecimal cost;
	/**结算金额*/
//	@Excel(name = "结算金额", width = 15)
//	@ApiModelProperty(value = "结算金额")
//	private java.math.BigDecimal settleAmt;
//	/**已结算金额*/
//	@Excel(name = "已结算金额", width = 15)
//	@ApiModelProperty(value = "已结算金额")
//	private java.math.BigDecimal settledAmt;
//	/**已开票金额*/
//	@Excel(name = "已开票金额", width = 15)
//	@ApiModelProperty(value = "已开票金额")
//	private java.math.BigDecimal invoicedAmt;

	/**免柜时间（天数）*/
	@Excel(name = "免柜时间（天数）", width = 15, dicCode = "free_demurrage")
	@ApiModelProperty(value = "免柜时间（天数）")
	private Integer freeDemurrage;
	/**入港时间*/
	@Excel(
			name = "入港时间",
			width = 15.0D,
			format = "yyyy-MM-dd HH:mm:ss"
	)
	@JsonFormat(
			timezone = "GMT+8",
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@DateTimeFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty(value = "入港时间")
	private Date inPortTime;
	/**提前提醒天数*/
	@Excel(name = "提前提醒天数", width = 15, dicCode = "notify_pre_days")
	@ApiModelProperty(value = "提前提醒天数")
	private Integer notifyPreDays;

	@ExcelCollection(name="明细")
	@ApiModelProperty(value = "明细")
	private List<PortIoEntry> portIoEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + portIoType;
	}
}
