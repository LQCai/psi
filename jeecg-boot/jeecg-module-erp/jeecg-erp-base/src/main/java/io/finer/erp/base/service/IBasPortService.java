package io.finer.erp.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.finer.erp.base.entity.BasPort;
import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.base.entity.BasPort;
import io.finer.erp.base.entity.BasPort;
import org.jeecg.common.exception.JeecgBootException;

import java.util.List;

/**
 * @Description: 港口
 * @Author: jeecg-boot
 * @Date:   2022-12-20
 * @Version: V1.0
 */
public interface IBasPortService extends IService<BasPort> {

    /**根节点父ID的值*/
    public static final String ROOT_PID_VALUE = "0";

    /**树节点有子节点状态值*/
    public static final String HASCHILD = "1";

    /**树节点无子节点状态值*/
    public static final String NOCHILD = "0";

    /**新增节点*/
    void addBasPort(BasPort basPort);

    /**修改节点*/
    void updateBasPort(BasPort basPort) throws JeecgBootException;

    /**删除节点*/
    void deleteBasPort(String id) throws JeecgBootException;

    /**查询所有数据，无分页*/
    List<BasPort> queryTreeListNoPage(QueryWrapper<BasPort> queryWrapper);
}
