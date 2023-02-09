package org.jeecg.modules.system.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.system.entity.SysAppVersion;
import org.jeecg.modules.system.service.ISysAppVersionService;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: app版本
 * @Author: jeecg-boot
 * @Date: 2023-02-09
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "app版本")
@RestController
@RequestMapping("/system/sysAppVersion")
public class SysAppVersionController extends JeecgController<SysAppVersion, ISysAppVersionService> {
    @Autowired
    private ISysAppVersionService sysAppVersionService;

    /**
     * 分页列表查询
     *
     * @param sysAppVersion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "app版本-分页列表查询")
    @ApiOperation(value = "app版本-分页列表查询", notes = "app版本-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysAppVersion sysAppVersion,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysAppVersion> queryWrapper = QueryGenerator.initQueryWrapper(sysAppVersion, req.getParameterMap());
        Page<SysAppVersion> page = new Page<SysAppVersion>(pageNo, pageSize);
        IPage<SysAppVersion> pageList = sysAppVersionService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param sysAppVersion
     * @return
     */
    @AutoLog(value = "app版本-添加")
    @ApiOperation(value = "app版本-添加", notes = "app版本-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysAppVersion sysAppVersion) {
        sysAppVersionService.save(sysAppVersion);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysAppVersion
     * @return
     */
    @AutoLog(value = "app版本-编辑")
    @ApiOperation(value = "app版本-编辑", notes = "app版本-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody SysAppVersion sysAppVersion) {
        sysAppVersionService.updateById(sysAppVersion);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "app版本-通过id删除")
    @ApiOperation(value = "app版本-通过id删除", notes = "app版本-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysAppVersionService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "app版本-批量删除")
    @ApiOperation(value = "app版本-批量删除", notes = "app版本-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysAppVersionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "app版本-通过id查询")
    @ApiOperation(value = "app版本-通过id查询", notes = "app版本-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysAppVersion sysAppVersion = sysAppVersionService.getById(id);
        return Result.OK(sysAppVersion);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysAppVersion
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysAppVersion sysAppVersion) {
        return super.exportXls(request, sysAppVersion, SysAppVersion.class, "app版本");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysAppVersion.class);
    }

    /**
     * 最新版本
     *
     * @return
     */
    @AutoLog(value = "app版本-最新版本")
    @ApiOperation(value = "app版本-最新版本", notes = "app版本-最新版本")
    @GetMapping(value = "/lastVersion")
    public Result<?> lastVersion() {
        List<SysAppVersion> sysAppVersions = sysAppVersionService.list(Wrappers.<SysAppVersion>lambdaQuery()
                .orderByDesc(SysAppVersion::getVersionCode)
                .last("limit 1")
        );
        if (sysAppVersions.size() == 0) {
            return Result.error("没有数据!");
        }
        return Result.OK(sysAppVersions.get(0));
    }

}
