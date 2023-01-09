package io.finer.erp.sale.enums;

import org.jeecg.common.system.annotation.EnumDict;

/**
 * 购物车装填
 * @author luoqc
 */
@EnumDict("salShoppingCartStatus")
public enum SalShoppingCartStatusEnum {

    NORMAL(1,  "正常"),
    EXPIRE(2,  "失效")
    ;

    Integer status;

    String name;

    SalShoppingCartStatusEnum(Integer status, String name) {
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
