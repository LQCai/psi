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
 * @Description: 购物车
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Data
@TableName("sal_shopping_cart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sal_shopping_cart对象", description="购物车")
public class SalShoppingCart {
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
	private java.lang.String id;
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
	/**询盘id*/
	@Excel(name = "询盘id", width = 15)
    @ApiModelProperty(value = "询盘id")
	private java.lang.String inquiryId;
	/**商品id*/
	@Excel(name = "商品id", width = 15)
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "商品id")
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
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private java.lang.String remark;
	/**状态(1 = 正常, 2 = 失效)*/
	@Excel(name = "状态(1 = 正常, 2 = 失效)", width = 15)
    @ApiModelProperty(value = "状态(1 = 正常, 2 = 失效)")
	private java.lang.Integer status;
	/**创建部门*/
	@Excel(name = "创建部门", width = 15)
    @ApiModelProperty(value = "创建部门")
	private java.lang.String sysOrgCode;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private java.lang.String updateBy;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
}
