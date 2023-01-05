package io.finer.erp.port.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="stk_io对象", description="出入库单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("port_io")
public class PortIo extends Bill {
    /**出入港类型*/
    @Excel(name = "出入港类型", width = 15, dicCode = "x_port_io_type")
    @Dict(dicCode = "x_port_io_type")
    @ApiModelProperty(value = "出入港类型")
    private String portIoType;
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
//    @Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
//    @Dict(dicCode = "x_invoice_type")
//    @ApiModelProperty(value = "发票类型")
//    private String invoiceType;
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
//    @Excel(name = "结算金额", width = 15)
//    @ApiModelProperty(value = "结算金额")
//    private java.math.BigDecimal settleAmt;
//    /**已结算金额*/
//    @Excel(name = "已结算金额", width = 15)
//    @ApiModelProperty(value = "已结算金额")
//    private java.math.BigDecimal settledAmt;
//    /**已开票金额*/
//    @Excel(name = "已开票金额", width = 15)
//    @ApiModelProperty(value = "已开票金额")
//    private java.math.BigDecimal invoicedAmt;
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

    @Override
    public String getBillType() {
        return this.getClass().getSimpleName() + ":" + portIoType;
    }

    /**是否退货*/
    public Integer getIsReturned() {
        //controller中，QueryWrapper也会创建entity；
        //(id == null)，是为了避免作为查询数据库参数。
        if (getId() == null) {
            return null;
        }
        return portIoType == null ? null : (portIoType.equals("1011") || portIoType.equals("2011")) ? 1 : 0;
    }
}
