package io.finer.erp.port.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.finer.erp.port.entity.PortIo;
import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.stock.entity.StkCheck;
import io.finer.erp.stock.entity.StkCheckEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version:
 */
public interface IPortIoService extends IBillWithEntryService<PortIo, PortIoEntry> {

    /**
     * 后置回写更新——已结算金额
     * @param checkEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse);


}
