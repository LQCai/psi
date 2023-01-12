package io.finer.erp.sale.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@TableName("sal_ticket")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sal_ticket对象", description="订单")
public class SalTicket {
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
	private java.lang.String id;
	/**订单号*/
	@Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
	private java.lang.String no;
	/**订单日期*/
	@Excel(name = "订单日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "订单日期")
	private java.util.Date noDate;
	/**源单号*/
	@Excel(name = "源单号", width = 15)
    @ApiModelProperty(value = "源单号")
	private java.lang.String srcNo;
	/**业务员*/
	@Excel(name = "业务员", width = 15)
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "业务员")
	private java.lang.String operator;
	/**客户*/
	@Excel(name = "客户", width = 15)
	@Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
	private java.lang.String customerId;
	/**商品id*/
	@Excel(name = "商品", width = 15)
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "商品id")
	private java.lang.String materialId;
	/**商品金额*/
	@Excel(name = "商品金额", width = 15)
    @ApiModelProperty(value = "商品金额")
	private java.math.BigDecimal materialAmt;
	/**报价金额*/
	@Excel(name = "报价金额", width = 15)
    @ApiModelProperty(value = "报价金额")
	private java.math.BigDecimal quotedAmt;
	/**商品数量*/
	@Excel(name = "商品数量", width = 15)
    @ApiModelProperty(value = "商品数量")
	private java.lang.Integer materialCount;
	/**总金额*/
	@Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
	private java.math.BigDecimal totalAmt;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String remark;
	/**状态(0 = 待审核, 1 = 待发货, 2 = 已发货)*/
	@Excel(name = "状态(0 = 待审核, 1 = 待发货, 2 = 已发货)", width = 15)
	@Dict(
			dicCode = "x_ticket_status"
	)
    @ApiModelProperty(value = "状态(0 = 待审核, 1 = 待发货, 2 = 已发货)")
	private java.lang.Integer status;
	/**结算方式*/
	@Excel(name = "结算方式", width = 15)
	@Dict(
			dicCode = "x_settle_method"
	)
    @ApiModelProperty(value = "结算方式")
	private java.lang.Integer billingMethod;
	/**付款方式*/
	@Excel(name = "付款方式", width = 15)
	@Dict(
			dicCode = "x_payment_method"
	)
    @ApiModelProperty(value = "付款方式")
	private java.lang.Integer paymentsMethod;
	/**开票方式*/
	@Excel(name = "开票方式", width = 15)
	@Dict(
			dicCode = "x_invoice_type"
	)
    @ApiModelProperty(value = "开票方式")
	private java.lang.Integer invoiceType;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
	@Dict(
			dictTable = "sys_user",
			dicText = "realname",
			dicCode = "username"
	)
    @ApiModelProperty(value = "审核人")
	private java.lang.String approver;
	/**审核结果*/
	@Excel(name = "审核结果", width = 15)
	@Dict(
			dicCode = "x_approval_result_type"
	)
    @ApiModelProperty(value = "审核结果")
	private java.lang.String approvalResultType;
	/**审核意见*/
	@Excel(name = "审核意见", width = 15)
    @ApiModelProperty(value = "审核意见")
	private java.lang.String approvalRemark;

	@Excel(name = "驾驶人姓名", width = 15)
	@ApiModelProperty(value = "驾驶人姓名")
	private String driverName;
	@Excel(name = "驾驶人电话", width = 15)
	@ApiModelProperty(value = "驾驶人电话")
	private String driverTel;
	@Excel(name = "驾驶人车牌号", width = 15)
	@ApiModelProperty(value = "驾驶人车牌号")
	private String driverCarNumber;
	@Excel(name = "驾驶人身份证", width = 15)
	@ApiModelProperty(value = "驾驶人身份证")
	private String driverIdCard;

	/**创建部门*/
	@Excel(
			name = "创建部门",
			width = 15.0D,
			dictTable = "sys_depart",
			dicText = "depart_name",
			dicCode = "org_code"
	)
	@Dict(
			dictTable = "sys_depart",
			dicText = "depart_name",
			dicCode = "org_code"
	)
	@ApiModelProperty(value = "创建部门")
	private java.lang.String sysOrgCode;
	/**创建人*/
	@Excel(
			name = "创建人",
			width = 15.0D,
			dictTable = "sys_user",
			dicText = "realname",
			dicCode = "username"
	)
	@Dict(
			dictTable = "sys_user",
			dicText = "realname",
			dicCode = "username"
	)
	@ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人*/
	@Excel(
			name = "修改人",
			width = 15.0D,
			dictTable = "sys_user",
			dicText = "realname",
			dicCode = "username"
	)
	@Dict(
			dictTable = "sys_user",
			dicText = "realname",
			dicCode = "username"
	)
	@ApiModelProperty(value = "修改人")
	private java.lang.String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
}
