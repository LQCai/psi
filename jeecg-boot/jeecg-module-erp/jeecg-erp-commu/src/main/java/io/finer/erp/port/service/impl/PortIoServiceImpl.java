package io.finer.erp.port.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.common.util.BillUtils;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.service.*;
import io.finer.erp.port.entity.PortIo;
import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.port.mapper.PortIoEntryMapper;
import io.finer.erp.port.mapper.PortIoMapper;
import io.finer.erp.port.service.IPortIoService;
import io.finer.erp.purchase.service.IPurOrderService;
import io.finer.erp.stock.service.IStkInventoryService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version:
 */
@Service
public class PortIoServiceImpl
        extends BillWithEntryServiceImpl<PortIoMapper, PortIo, PortIoEntryMapper, PortIoEntry>
        implements IPortIoService {

    @Lazy
    @Autowired
    private IPurOrderService purOrderService;
    @Lazy
    @Autowired
    private IFinPayableService finPayableService;
    @Lazy
    @Autowired
    private IFinPurInvoiceService finPurInvoiceService;
    @Lazy
    @Autowired
    private IFinPaymentReqService finPaymentReqService;
    @Lazy
    @Autowired
    private IFinPaymentService finPaymentService;
    @Lazy
    @Autowired
    private IFinReceivableService finReceivableService;
    @Lazy
    @Autowired
    private IFinSalInvoiceService finSalInvoiceService;
    @Lazy
    @Autowired
    private IFinReceiptService finReceiptService;

    @Override
    protected  void beforePersistAdd(PortIo bill, List<PortIoEntry> entryList){
        BigDecimal cost = new BigDecimal("0.00");
        BigDecimal settleAmt = new BigDecimal("0.00");
        if(entryList!=null && entryList.size()>0) {
            String supplierId = bill.getSupplierId();
            supplierId = supplierId != null ? supplierId : "";
            for(PortIoEntry entry:entryList) {
                entry.setSupplierId(supplierId);
                if (entry.getCost() != null) {
                    cost = cost.add(entry.getCost());
                }
                if (entry.getSettleAmt() != null) {
                    settleAmt = settleAmt.add(entry.getSettleAmt());
                }
            }
        }
        bill.setCost(cost);
    }

    @Override
    protected void beforePersistEdit(PortIo bill, List<PortIoEntry> entryList) {
        this.beforePersistAdd(bill, entryList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void createSubBill(PortIo bill) throws Exception {

    }

    @Override
    protected boolean hasFinishedExecute(PortIo bill) {
        return super.hasFinishedExecute(bill);
    }

    @Override
    protected void writeBack(PortIo bill, boolean reverse) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void voidBillPreprocess(PortIo bill) throws Exception {
        //后置单据-采购发票、采购付款申请、采购付款
        String billNos = null;
        if (!bill.getPortIoType().startsWith("1")) {
            billNos = finPurInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
            if (StringUtils.isEmpty(billNos)) {
                billNos = finPaymentReqService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
            }
            if (StringUtils.isEmpty(billNos)) {
                billNos = finPaymentService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
            }
        }

        //后置单据-销售发票、销售收款
        if (StringUtils.isEmpty(billNos) && !bill.getPortIoType().startsWith("2")) {
            billNos = finSalInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
            if (StringUtils.isEmpty(billNos)) {
                billNos = finReceiptService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
            }
        }
        if (!StringUtils.isEmpty(billNos)) {
            throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
        }

        //后置单据-应付单、应收单-自动作废：出入库据生效后自动生成的
        if (bill.getPortIoType().startsWith("1")) {
            List<FinPayable> list = finPayableService.listNotVoided(bill.getBillType(), bill.getId());
            if (list != null) {
                for(FinPayable f: list) {
                    finPayableService.voidBill(f.getId());
                }
            }
        } else if (bill.getPortIoType().startsWith("2")) {
            List<FinReceivable> list = finReceivableService.listNotVoided(bill.getBillType(), bill.getId());
            if (list != null) {
                for(FinReceivable f: list) {
                    finReceivableService.voidBill(f.getId());
                }
            }
        }

        //后置单据-应付单、应收单-不自动作废：非出入库据生效后自动生成的；先做自动作废，否则应该自动作废的也会视为“未作废”
        if (!bill.getPortIoType().startsWith("1")) {
            billNos = finPayableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
        }
        if (StringUtils.isEmpty(billNos) && !bill.getPortIoType().startsWith("2")) {
            billNos = finReceivableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
        }
        if (!StringUtils.isEmpty(billNos)) {
            throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse) {
        Map<String, PortIo> billMap = new HashMap<>();
        List<FinPayableCheckEntry> checkEntryList1 = new ArrayList<>();
        for(FinPayableCheckEntry writterEntry: checkEntryList) {
            String srcBillType = writterEntry.getSrcBillType();
            BigDecimal writterEntryAmt = writterEntry.getAmt();
            if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("PortIo")
                    || writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
                continue;
            }

            //前置条件、预处理
            PortIo bill = writtenBackPreprocess(writterEntry, billMap);

            //数据处理
            if (reverse) {
                writterEntryAmt = writterEntryAmt.negate();
            }

            //向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
            if (bill.getIsReturned() == 0) {
                writtenBackForward(bill, writterEntry, checkEntryList1, FinPayableCheckEntry.class);
            }
        }

        //后置状态
        for(PortIo bill: billMap.values()) {
            this.baseMapper.updateById(bill);
            this.refreshExecuteStage(bill);
        }

        //向前回写
        for(FinPayableCheckEntry entry: checkEntryList1) {
            //如果源单不为 PurOrder，不用获取 purOrderService
            if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
                purOrderService.payableCheckWriteBackSettledAmt(checkEntryList1, reverse);
                break;
            }
        }
    }

    private Pair<PortIo, PortIoEntry> invoiceWriteBack(String srcBillId, String srcEntryId, String srcNo,
                                                     String unitId, BigDecimal qty, BigDecimal amt, boolean reverse,
                                                     Map<String, PortIo> billMap, Map<String, PortIoEntry> entryMap) {
        //前置状态
        PortIo bill = billMap.get(srcBillId);
        if (bill == null) {
            bill = this.baseMapper.selectById(srcBillId);
            if (bill == null) {
                throw new JeecgBootException(srcNo.split(":")[0] + "：单据未找到，可能被其他用户删除了！");
            }

            if (bill.getIsEffective() == 0 || bill.getIsVoided() == 1) {
                throw new JeecgBootException(bill.getBillNo() + "：单据未生效或被作废，不能被回写！");
            }
            billMap.put(srcBillId, bill);
        }

        PortIoEntry entry = entryMap.get(srcEntryId);
        if (entry == null) {
            entry = this.entryMapper.selectById(srcEntryId);
            if (entry == null){
                throw new JeecgBootException(srcNo + "：出入库分录不存在！");
            }
            entryMap.put(srcEntryId, entry);
        }

        //数据处理
        if (!unitId.equals(entry.getUnitId())) {
            qty = BillUtils.convertUnit(qty, unitId, entry.getUnitId());
        }
        if (reverse){
            qty = qty.negate();
            amt = amt.negate();
        }

        entry.setInvoicedQty(entry.getInvoicedQty().add(qty));
        int i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
        if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
            throw new JeecgBootException(srcNo + "：开票数量不能超出未开票数量！");
        }

        entry.setInvoicedAmt(entry.getInvoicedAmt().add(amt));
        i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
        if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
            throw new JeecgBootException(srcNo + "：开票金额不能超出未开票金额！");
        }

        return Pair.of(bill, entry);
    }

}
