package io.finer.erp.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @Description: 询盘
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Data
@TableName("sal_inquiry")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sal_inquiry对象", description="询盘")
public class SalInquiry {
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
	private java.lang.String id;
	/**单据编号*/
	@Excel(name = "单据编号", width = 15)
    @ApiModelProperty(value = "单据编号")
	private java.lang.String billNo;
	/**单据日期*/
	@Excel(name = "单据日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "单据日期")
	private java.util.Date billDate;
	/**subject*/
	@Excel(name = "subject", width = 15)
    @ApiModelProperty(value = "subject")
	private java.lang.String subject;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "业务员")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	private java.lang.String operator;
	/**客户*/
	@Excel(name = "客户", width = 15)
    @ApiModelProperty(value = "客户")
	@Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
	private java.lang.String customerId;
	/**商品id*/
	@Excel(name = "商品", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "商品")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	private java.lang.String materialId;
	/**报价金额*/
	@Excel(name = "报价金额", width = 15)
    @ApiModelProperty(value = "报价金额")
	private java.math.BigDecimal quotedAmt;
	/**商品金额*/
	@Excel(name = "商品金额", width = 15)
    @ApiModelProperty(value = "商品金额")
	private java.math.BigDecimal materialAmt;
	/**商品数量*/
	@Excel(name = "商品数量", width = 15)
    @ApiModelProperty(value = "商品数量")
	private java.lang.Integer materialCount;
	/**附件*/
	@Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
	private java.lang.String attachment;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String remark;
	/**单据阶段*/
	@Excel(name = "单据阶段", width = 15)
    @ApiModelProperty(value = "单据阶段")
	private java.lang.String billStage;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
	private java.lang.String approver;
	/**下单意向*/
	@Excel(name = "下单意向", width = 15)
    @ApiModelProperty(value = "下单意向")
	private java.lang.String intention;
	/**核批结果类型*/
	@Excel(
			name = "核批结果类型",
			width = 15.0D,
			dicCode = "x_approval_result_type"
	)
	@Dict(
			dicCode = "x_approval_result_type"
	)
    @ApiModelProperty(value = "核批结果类型")
	private java.lang.String approvalResultType;
	/**核批意见*/
	@Excel(name = "核批意见", width = 15)
    @ApiModelProperty(value = "核批意见")
	private java.lang.String approvalRemark;
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
