package io.finer.erp.port.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.vo.BillPage;
import io.finer.erp.port.entity.PortIo;
import io.finer.erp.port.entity.PortIoEntry;
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
 * @Description: 出入港单
 * @Author: luoqc
 * @Date:   2023-01-14
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="port_ioNotify对象", description="出入港单")
public class PortIoNotify extends PortIo {
	/**免柜到期时间*/
	@Excel(
			name = "免柜到期时间",
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
	@ApiModelProperty(value = "免柜到期时间")
	private Date freeDemurrageTime;
	@ApiModelProperty(value = "港口出入实体列表")
	private List<PortIoEntry> portIoEntryList;
}
