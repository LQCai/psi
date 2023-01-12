package io.finer.erp.sale.service;

import io.finer.erp.sale.entity.SalTicket;
import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.sale.param.SalTicketAddParam;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date:   2023-01-11
 * @Version: V1.0
 */
public interface ISalTicketService extends IService<SalTicket> {

    String generalNo();

    String generalSrcNo();

    Result<?> addFromShoppingCart(SalTicketAddParam salTicketAddParam);
}
