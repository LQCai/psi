package io.finer.erp.base.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.finer.erp.base.entity.BasPort;
import io.finer.erp.base.service.IBasPortService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
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
 * @Description: 港口
 * @Author: jeecg-boot
 * @Date:   2022-12-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags="港口")
@RestController
@RequestMapping("/base/basPort")
public class BasPortController extends JeecgController<BasPort, IBasPortService> {
	 @Autowired
	 private IBasPortService basPortService;

	 /**
	  * 分页列表查询
	  *
	  * @param basPort
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "港口-分页列表查询")
	 @ApiOperation(value="港口-分页列表查询", notes="港口-分页列表查询")
	 @GetMapping(value = "/rootList")
	 public Result<IPage<BasPort>> queryPageList(BasPort basPort,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		 String hasQuery = req.getParameter("hasQuery");
		 if(hasQuery != null && "true".equals(hasQuery)){
			 QueryWrapper<BasPort> queryWrapper =  QueryGenerator.initQueryWrapper(basPort, req.getParameterMap());
			 List<BasPort> list = basPortService.queryTreeListNoPage(queryWrapper);
			 IPage<BasPort> pageList = new Page<>(1, 10, list.size());
			 pageList.setRecords(list);
			 return Result.OK(pageList);
		 }else{
			 String parentId = basPort.getPid();
			 if (oConvertUtils.isEmpty(parentId)) {
				 parentId = "0";
			 }
			 basPort.setPid(null);
			 QueryWrapper<BasPort> queryWrapper = QueryGenerator.initQueryWrapper(basPort, req.getParameterMap());
			 // 使用 eq 防止模糊查询
			 queryWrapper.eq("pid", parentId);
			 Page<BasPort> page = new Page<BasPort>(pageNo, pageSize);
			 IPage<BasPort> pageList = basPortService.page(page, queryWrapper);
			 return Result.OK(pageList);
		 }
	 }

	 /**
	  * 获取子数据
	  * @param basPort
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "港口-获取子数据")
	 @ApiOperation(value="港口-获取子数据", notes="港口-获取子数据")
	 @GetMapping(value = "/childList")
	 public Result<IPage<BasPort>> queryPageList(BasPort basPort,HttpServletRequest req) {
		 QueryWrapper<BasPort> queryWrapper = QueryGenerator.initQueryWrapper(basPort, req.getParameterMap());
		 List<BasPort> list = basPortService.list(queryWrapper);
		 IPage<BasPort> pageList = new Page<>(1, 10, list.size());
		 pageList.setRecords(list);
		 return Result.OK(pageList);
	 }

	 /**
	  * 批量查询子节点
	  * @param parentIds 父ID（多个采用半角逗号分割）
	  * @return 返回 IPage
	  * @param parentIds
	  * @return
	  */
	 //@AutoLog(value = "港口-批量获取子数据")
	 @ApiOperation(value="港口-批量获取子数据", notes="港口-批量获取子数据")
	 @GetMapping("/getChildListBatch")
	 public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
		 try {
			 QueryWrapper<BasPort> queryWrapper = new QueryWrapper<>();
			 List<String> parentIdList = Arrays.asList(parentIds.split(","));
			 queryWrapper.in("pid", parentIdList);
			 List<BasPort> list = basPortService.list(queryWrapper);
			 IPage<BasPort> pageList = new Page<>(1, 10, list.size());
			 pageList.setRecords(list);
			 return Result.OK(pageList);
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 return Result.error("批量查询子节点失败：" + e.getMessage());
		 }
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "港口-通过id查询")
	 @ApiOperation(value="港口-通过id查询", notes="港口-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<BasPort> queryById(@RequestParam(name="id",required=true) String id) {
		 BasPort basPort = basPortService.getById(id);
		 if(basPort==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(basPort);
	 }

	 /**
	  *   添加
	  *
	  * @param basPort
	  * @return
	  */
	 @AutoLog(value = "港口-添加")
	 @ApiOperation(value="港口-添加", notes="港口-添加")
	 @PostMapping(value = "/add")
	 public Result<String> add(@RequestBody BasPort basPort) {
		 basPortService.addBasPort(basPort);
		 return Result.OK("添加成功！");
	 }

	 /**
	  *  编辑
	  *
	  * @param basPort
	  * @return
	  */
	 @AutoLog(value = "港口-编辑")
	 @ApiOperation(value="港口-编辑", notes="港口-编辑")
	 @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> edit(@RequestBody BasPort basPort) {
		 basPortService.updateBasPort(basPort);
		 return Result.OK("编辑成功!");
	 }

	 /**
	  *   通过id删除
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "港口-通过id删除")
	 @ApiOperation(value="港口-通过id删除", notes="港口-通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		 basPortService.deleteBasPort(id);
		 return Result.OK("删除成功!");
	 }

	 /**
	  *  批量删除
	  *
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "港口-批量删除")
	 @ApiOperation(value="港口-批量删除", notes="港口-批量删除")
	 @DeleteMapping(value = "/deleteBatch")
	 public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		 this.basPortService.removeByIds(Arrays.asList(ids.split(",")));
		 return Result.OK("批量删除成功！");
	 }

	 /**
	  * 导出excel
	  *
	  * @param request
	  * @param basPort
	  */
	 @AutoLog(value = "导出为excel")
	 @RequestMapping(value = "/exportXls")
	 public ModelAndView exportXls(HttpServletRequest request, BasPort basPort) {
		 return super.exportXls(request, basPort, BasPort.class, "港口");
	 }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @AutoLog(value = "通过excel导入数据")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		 return super.importExcel(request, response, BasPort.class);
	 }

}
