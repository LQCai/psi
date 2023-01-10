package io.finer.erp.stock.vo;

import io.finer.erp.base.entity.BasMaterial;
import lombok.Data;

import java.util.Map;

@Data
public class BasMaterialVo extends BasMaterial {
    private Map<String, Object> stkInventory;
}
