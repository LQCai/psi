package io.finer.erp.base.mapper;


import org.apache.ibatis.annotations.Param;
import io.finer.erp.base.entity.BasPort;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 港口
 * @Author: jeecg-boot
 * @Date:   2022-12-20
 * @Version: V1.0
 */
public interface BasPortMapper extends BaseMapper<BasPort> {

    /**
     * 编辑节点状态
     * @param id
     * @param status
     */
    void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);
}
