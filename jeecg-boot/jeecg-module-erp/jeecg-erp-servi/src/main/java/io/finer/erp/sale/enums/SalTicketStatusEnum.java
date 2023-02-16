package io.finer.erp.sale.enums;

import org.jeecg.common.system.annotation.EnumDict;

/**
 * 订单状态
 * @author luoqc
 */
@EnumDict("salShoppingCartStatus")
public enum SalTicketStatusEnum {

    INIT(0,  "待审核"),
    TO_BE_SHIPPED(1,  "待发货"),
    SHIPPED(2,  "已发货"),
    CLOSE(3,  "关闭"),
    TO_BE_PAYMENT(4,  "待付款"),
    COMPLETED(5,  "完成"),
    ;

    Integer status;

    String name;

    SalTicketStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
