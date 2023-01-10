package io.finer.erp.stock.mapper;

import io.finer.erp.stock.entity.StkInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @Description: 库存
 * @Author: jeecg-boot
 * @Date:   2020-04-11
 * @Version: V1.0
 */
public interface StkInventoryMapper extends BaseMapper<StkInventory> {

    List<Map<String, Object>> summaryList();

    List<Map<String, Object>> summaryListInMaterialIds(List<String> materialIds);
}
