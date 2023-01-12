package io.finer.erp.sale.job;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.sale.entity.SalInquiry;
import io.finer.erp.sale.entity.SalShoppingCart;
import io.finer.erp.sale.service.ISalInquiryService;
import io.finer.erp.sale.service.ISalShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 询盘失败任务
 *
 * @author luoqc
 */
@Slf4j
public class SalInquiryFailJob implements Job {

    @Autowired
    private ISalInquiryService salInquiryService;
    @Autowired
    private ISalShoppingCartService salShoppingCartService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" 询盘失败任务 SalInquiryFailJob !  开始时间:" + DateUtils.getTimestamp());

        Date today = DateUtil.yesterday();

        List<SalInquiry> salInquiryList = salInquiryService.list(Wrappers.<SalInquiry>lambdaQuery()
                .between(SalInquiry::getCreateTime, DateUtil.beginOfDay(today), DateUtil.endOfDay(today))
        );
        if (salInquiryList.size() == 0) {
            log.info(" 询盘失败任务 SalInquiryFailJob !  结束时间:" + DateUtils.getTimestamp());
            return;
        }

        List<SalShoppingCart> salShoppingCartList = salShoppingCartService.list(Wrappers.<SalShoppingCart>lambdaQuery()
                .in(SalShoppingCart::getInquiryId, salInquiryList.stream().map(SalInquiry::getId).collect(Collectors.toList()))
        );

        for (SalInquiry salInquiry: salInquiryList) {
            for (SalShoppingCart salShoppingCart: salShoppingCartList) {
                if (salInquiry.getId().equals(salShoppingCart.getInquiryId())) {
                    salInquiry.setStatus(2);
                }
            }
        }
        salInquiryService.saveOrUpdateBatch(salInquiryList);
        salShoppingCartService.removeBatchByIds(salShoppingCartList);

        log.info(" 询盘失败任务 SalInquiryFailJob !  结束时间:" + DateUtils.getTimestamp());
    }
}
