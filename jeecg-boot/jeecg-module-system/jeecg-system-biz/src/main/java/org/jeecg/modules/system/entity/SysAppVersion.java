package org.jeecg.modules.system.entity;

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
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: app版本
 * @Author: jeecg-boot
 * @Date:   2023-02-09
 * @Version: V1.0
 */
@Data
@TableName("sys_app_version")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_app_version对象", description="app版本")
public class SysAppVersion {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private java.lang.String id;
	/**创建人登录名称*/
	@Excel(name = "创建人登录名称", width = 15)
    @ApiModelProperty(value = "创建人登录名称")
	private java.lang.String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
	private java.util.Date createTime;
	/**更新人登录名称*/
	@Excel(name = "更新人登录名称", width = 15)
    @ApiModelProperty(value = "更新人登录名称")
	private java.lang.String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
	private java.util.Date updateTime;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
	private java.lang.String name;
	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
	private java.lang.String versionCode;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
	private java.lang.String description;
	/**下载链接*/
	@Excel(name = "下载链接", width = 15)
    @ApiModelProperty(value = "下载链接")
	private java.lang.String link;
}
