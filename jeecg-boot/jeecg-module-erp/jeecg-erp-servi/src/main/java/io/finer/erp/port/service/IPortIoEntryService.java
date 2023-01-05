package io.finer.erp.port.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.port.entity.PortIoEntry;

import java.util.List;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IPortIoEntryService extends IService<PortIoEntry> {
	List<PortIoEntry> selectByMainId(String mainId);
	List<PortIoEntry> selectEditingByMainId(String mainId);
}
