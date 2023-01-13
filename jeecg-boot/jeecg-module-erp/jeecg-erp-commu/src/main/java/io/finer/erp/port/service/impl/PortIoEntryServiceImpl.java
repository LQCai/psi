package io.finer.erp.port.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.port.mapper.PortIoEntryMapper;
import io.finer.erp.port.service.IPortIoEntryService;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.service.IStkInventoryService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

	@Override
	public void increase(StkIo stkIo, List<StkIoEntry> stkIoEntryList) {
		PortIoEntry portIoEntry = getOne(Wrappers.<PortIoEntry>lambdaQuery()
				.eq(PortIoEntry::getBatchNo, stkIo.getPortBatchNo())
		);
		if (portIoEntry.getQty().compareTo(new BigDecimal(0)) < 0) {
			throw new JeecgBootException("港口库存批次数量不足!");
		}

		for (StkIoEntry stkIoEntry: stkIoEntryList) {
			portIoEntry.setQty(portIoEntry.getQty().subtract(stkIoEntry.getQty()));
			if (portIoEntry.getQty().compareTo(new BigDecimal(0)) < 0) {
				throw new JeecgBootException("入库数量大于港口库存批次数量!");
			}
		}
		saveOrUpdate(portIoEntry);
	}

}
