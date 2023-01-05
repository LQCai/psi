package io.finer.erp.port.service.impl;

import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.port.mapper.PortIoEntryMapper;
import io.finer.erp.port.service.IPortIoEntryService;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.service.IStkInventoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class PortIoEntryServiceImpl
		extends ServiceImpl<PortIoEntryMapper, PortIoEntry>
		implements IPortIoEntryService {
	@Autowired
	private IStkInventoryService stkInventoryService;

	@Override
	public List<PortIoEntry> selectByMainId(String mainId) {
		return this.baseMapper.selectByMainId(mainId);
	}

	@Override
	public List<PortIoEntry> selectEditingByMainId(String mainId) {
		List<PortIoEntry> list = selectByMainId(mainId);
		return list;
	}

}
