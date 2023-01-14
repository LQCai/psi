package io.finer.erp.port.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.port.entity.PortIo;
import io.finer.erp.port.entity.PortIoEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 出入港单
 * @Author: luoqc
 * @Date:   2023-01-14
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="port_ioNotify对象", description="出入港单")
public class PortIoEntryVo extends PortIoEntry {
	@ApiModelProperty(value = "物料名称")
	private String materialName;
	@ApiModelProperty(value = "单位名称")
	private String unitName;
	@ApiModelProperty(value = "港口名称")
	private String portName;
}
