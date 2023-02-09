package io.finer.erp.port.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.entity.BasPort;
import io.finer.erp.base.entity.BasUnit;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.base.service.IBasPortService;
import io.finer.erp.base.service.IBasUnitService;
import io.finer.erp.port.entity.PortIo;
import io.finer.erp.port.entity.PortIoEntry;
import io.finer.erp.port.service.IPortIoEntryService;
import io.finer.erp.port.service.IPortIoService;
import io.finer.erp.port.vo.PortIoEntryVo;
import io.finer.erp.port.vo.PortIoNotify;
import io.finer.erp.port.vo.PortIoPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 出入库单
 * @Author: jeecg-boot
 * @Date:   2020-03-31
 * @Version: V1.0
 */
@Api(tags="出入库单")
@RestController
@RequestMapping("/port/portIo")
@Slf4j
public class PortIoController {
	@Autowired
	private IPortIoService portIoService;
	@Autowired
	private IPortIoEntryService portIoEntryService;
	@Autowired
	private IBasMaterialService basMaterialService;
	@Autowired
	private IBasPortService basPortService;
	@Autowired
	private IBasUnitService basUnitService;

	/**
	 * 分页列表查询
	 *
	 * @param stkIo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "出入库单-分页列表查询")
	@ApiOperation(value="出入库单-分页列表查询", notes="出入库单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{stockIoType}", "/list/{stockIoType}/{isRubric}"})  //stockIoType、isRubric会传至stkIo.stockIoType
	public Result<?> queryPageList(PortIo stkIo,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		QueryWrapper<PortIo> queryWrapper = QueryGenerator.initQueryWrapper(stkIo, req.getParameterMap());
		Page<PortIo> page = new Page<PortIo>(pageNo, pageSize);
		IPage<PortIo> pageList = portIoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出入库单-通过id查询")
	@ApiOperation(value="出入库单-通过id查询", notes="出入库单-通过id查询")
	@GetMapping(value = {"/queryById", "/queryById/dictText"})
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
	 	PortIo stkIo = portIoService.getById(id);
	 	if(stkIo ==null) {
			return Result.error("未找到对应数据");
		 }
		 return Result.OK(stkIo);
	}

	 /**
	  * 通过ids列表查询
	  *
	  */
	 //@AutoLog(value = "出入库单-通过ids列表查询")
	 @ApiOperation(value="出入库单-通过ids列表查询", notes="出入库单-通过ids列表查询")
	 @GetMapping(value = "/listByIds")
	 public Result<List<PortIo>> queryListByIds(@RequestParam(name="ids",required=true) String ids) {
		 List<PortIo> list = portIoService.listByIds(Arrays.asList(ids.split(",")));
		 return Result.OK(list);
	 }

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "明细集合-通过id查询")
	@ApiOperation(value="明细集合-通过id查询", notes="明细-通过id查询")
	@GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		List<PortIoEntry> stkIoEntryList = portIoEntryService.selectByMainId(id);
		return Result.OK(stkIoEntryList);
	}

	//@AutoLog(value = "明细集合-通过ids查询")
	@ApiOperation(value="明细集合-通过ids查询", notes="明细-通过ids查询")
	@GetMapping(value = "/queryEntryByMainIds")
	public Result<?> queryEntryListByMainIds(@NotNull @RequestParam(name="ids",required=true) String ids) {
	 	List<PortIoEntry> stkIoEntryList = null;
		for(String id: ids.split(",")) {
			List<PortIoEntry> list = portIoEntryService.selectByMainId(id);
			if (stkIoEntryList == null) {
				stkIoEntryList = list;
			} else {
				stkIoEntryList.addAll(list);
			}
		}
		return Result.OK(stkIoEntryList);
	}

	//@AutoLog(value = "明细集合-通过id查询")
	@ApiOperation(value="明细集合-通过id查询", notes="明细-通过id查询")
	@GetMapping(value = "/queryEditingEntryByMainId")
	public Result<?> queryEditingEntryListByMainId(@RequestParam(name="id",required=true) String id) {
	 List<PortIoEntry> stkIoEntryList = portIoEntryService.selectEditingByMainId(id);
	 return Result.OK(stkIoEntryList);
	}

	/**
	*   新增
	*
	* @param stkIoPage
	* @return
	*/
	@AutoLog(value = "出入库单-新增")
	@ApiOperation(value="出入库单-新增", notes="出入库单-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody PortIoPage stkIoPage, @PathVariable String action) {
		PortIo bill = new PortIo();
		BeanUtils.copyProperties(stkIoPage, bill);
		try {
			if (action.equals("submit")) {
				portIoService.submitAdd(bill, stkIoPage.getPortIoEntryList());
				return Result.OK("新增提交成功！");
			} else {
				portIoService.saveAdd(bill, stkIoPage.getPortIoEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param stkIoPage
	 * @return
	 */
	@AutoLog(value = "出入库单-编辑")
	@ApiOperation(value="出入库单-编辑", notes="出入库单-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody PortIoPage stkIoPage, @PathVariable String action) {
		PortIo bill = new PortIo();
		BeanUtils.copyProperties(stkIoPage, bill);
		try {
			if (action.equals("submit")) {
				portIoService.submitEdit(bill, stkIoPage.getPortIoEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				portIoService.saveEdit(bill, stkIoPage.getPortIoEntryList());
				return Result.OK("编辑保存成功!");
			}

		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "出入库单-通过id删除")
	@ApiOperation(value="出入库单-通过id删除", notes="出入库单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			portIoService.delete(id);
			return Result.OK("删除成功!");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "出入库单-批量删除")
	@ApiOperation(value="出入库单-批量删除", notes="出入库单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.portIoService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}


	 @AutoLog(value = "出入库单-执行")
	 @ApiOperation(value="出入库单-执行", notes="出入库单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 portIoService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-关闭")
	 @ApiOperation(value="出入库单-关闭", notes="出入库单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 portIoService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单--反关闭")
	 @ApiOperation(value="出入库单--反关闭", notes="出入库单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 portIoService.unclose(json.getString("id"));
			 return Result.OK("反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "出入库单-批量关闭")
	 @ApiOperation(value="出入库单-批量关闭", notes="出入库单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 portIoService.close(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量反关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "出入库单-批量反关闭")
	 @ApiOperation(value="出入库单-批量反关闭", notes="出入库单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 portIoService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-作废")
	 @ApiOperation(value="出入库单-作废", notes="出入库单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 portIoService.voidBill(json.getString("id"));
			 return Result.OK("作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  * 导出excel
	  *
	  * @param request
	  * @param stkIo
	  */
	 @AutoLog(value = "导出为excel")
	 @RequestMapping(value = {"/exportXls", "/exportXls/{stockIoType}", "/exportXls/{stockIoType}/{isRubric}"})  //stockIoType会传至stkIo.stockIoType
	 public ModelAndView exportXls(HttpServletRequest request, PortIo stkIo) {
		 // Step.1 组装查询条件查询数据
		 QueryWrapper<PortIo> queryWrapper = QueryGenerator.initQueryWrapper(stkIo, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 //Step.2 获取导出数据
		 List<PortIo> queryList = portIoService.list(queryWrapper);
		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 List<PortIo> stkIoList = new ArrayList<PortIo>();
		 if(oConvertUtils.isEmpty(selections)) {
			 stkIoList = queryList;
		 }else {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 stkIoList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 }

		 // Step.3 组装pageList
		 List<PortIoPage> pageList = new ArrayList<PortIoPage>();
		 for (PortIo main : stkIoList) {
			 PortIoPage vo = new PortIoPage();
			 BeanUtils.copyProperties(main, vo);
			 List<PortIoEntry> stkIoEntryList = portIoEntryService.selectByMainId(main.getId());
			 vo.setPortIoEntryList(stkIoEntryList);
			 pageList.add(vo);
		 }

		 // Step.4 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "出入库单列表");
		 mv.addObject(NormalExcelConstants.CLASS, PortIoPage.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出入库单数据", "导出人:"+sysUser.getRealname(), "出入库单"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		 return mv;
	 }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @return
	  */
	 @AutoLog(value = "通过excel导入数据")
	 @RequestMapping(value = {"/importExcel", "/importExcel/{stockIoType}", "/importExcel/{stockIoType}/{isRubric}"}, method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request,
								  @PathVariable(required = false) String stockIoType,
								  @PathVariable(required = false) Integer isRubric) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<PortIoPage> list = ExcelImportUtil.importExcel(file.getInputStream(), PortIoPage.class, params);

				 Map<PortIo, List<PortIoEntry>> billMap = new HashMap<>();
				 for (PortIoPage page : list) {
					 PortIo bill = new PortIo();
					 BeanUtils.copyProperties(page, bill);
					 if (stockIoType != null && !bill.getPortIoType().equals(stockIoType) ||
							 isRubric != null && !bill.getIsRubric().equals(isRubric)) {
						 throw new JeecgBootException(bill.getBillNo() + "：“出入库类型”或“是否红字”不符！");
					 }
					 billMap.put(bill, page.getPortIoEntryList());
				 }
				 portIoService.saveAddBatch(billMap);//20211204 cfm：事务化

				 return Result.OK("文件导入成功！数据行数:" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(),e);
				 return Result.error("文件导入失败:"+e.getMessage());
			 }
			 finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.OK("文件导入失败！");
	 }

	 /**
	  * 明细 - 通过batchNos列表查询
	  *
	  */
	 //@AutoLog(value = "出入库单-明细 - 通过batchNos列表查询")
	 @ApiOperation(value="出入港单-明细 - 通过batchNos列表查询", notes="出入港单-明细 - 通过batchNos列表查询")
	 @GetMapping(value = "/entryListByBatchNos")
	 public Result<List<PortIoEntry>> listByBatchNos(@RequestParam(name="batchNos",required=true) String batchNos) {
		 List<PortIoEntry> list = portIoEntryService.list(Wrappers.<PortIoEntry>lambdaQuery()
				 .in(PortIoEntry::getBatchNo, Arrays.asList(batchNos.split(",")))
		 );
		 return Result.OK(list);
	 }

	 /**
	  * 即将过期列表
	  *
	  */
	 @ApiOperation(value="出入港单-即将过期列表", notes="出入库单-即将过期列表")
	 @GetMapping(value = "/queryListExpireSoon")
	 public Result<List<PortIoNotify>> queryListExpireSoon() {
		 List<PortIoEntry> portIoEntryList = portIoEntryService.list(Wrappers.<PortIoEntry>lambdaQuery()
				 .gt(PortIoEntry::getQty, 0)
		 );
		 List<PortIoEntryVo> portIoEntryVoList = getPortIoEntryVoList(portIoEntryList);

		 List<String> midList = portIoEntryList.stream().map(PortIoEntry::getMid).collect(Collectors.toList());
		 if (midList.size() <= 0) {
			 return Result.OK(new ArrayList<>());
		 }
		 List<PortIo> portIoList = portIoService.listByIds(midList);

		 List<PortIoNotify> expireSoonList = new ArrayList<>();
		 Date today = new Date();
		 for (PortIo portIo: portIoList) {
			 // 免柜到期日期
			 Date freeDemurrageTime = DateUtil.offsetDay(portIo.getInPortTime(), portIo.getFreeDemurrage());
			 // 免柜到期预先提醒日期
			 Date expireNotifyTime = DateUtil.offsetDay(freeDemurrageTime, - portIo.getNotifyPreDays());
			 if (today.after(expireNotifyTime) && today.before(freeDemurrageTime)) {
				 PortIoNotify portIoNotify = BeanUtil.copyProperties(portIo, PortIoNotify.class);
				 portIoNotify.setFreeDemurrageTime(freeDemurrageTime);
				 portIoNotify.setPortIoEntryList(new ArrayList<>());
				 expireSoonList.add(portIoNotify);
			 }
		 }

		 expireSoonList = ListUtil.sortByProperty(expireSoonList, "freeDemurrageTime");
		 for (PortIoNotify portIoNotify: expireSoonList) {
			 for (PortIoEntryVo portIoEntry: portIoEntryVoList) {
				 if (portIoEntry.getMid().equals(portIoNotify.getId())) {
					 portIoNotify.getPortIoEntryList().add(portIoEntry);
				 }
			 }
		 }
		 return Result.OK(expireSoonList);
	 }

	 private List<PortIoEntryVo> getPortIoEntryVoList(List<PortIoEntry> portIoEntryList) {
		 portIoEntryList.forEach(portIoEntry -> BeanUtil.copyProperties(portIoEntry, PortIoEntryVo.class));

		 List<BasUnit> basUnitList = basUnitService.list();
		 List<BasMaterial> basMaterialList = basMaterialService.list();
		 List<BasPort> basPortList = basPortService.list();

		 return portIoEntryList.stream().map(portIoEntry ->  {
			 PortIoEntryVo portIoEntryVo = BeanUtil.copyProperties(portIoEntry, PortIoEntryVo.class);

			 basUnitList.forEach(item -> {
				 if (item.getId().equals(portIoEntryVo.getUnitId())) {
					 portIoEntryVo.setUnitName(item.getName());
				 }
			 });

			 basMaterialList.forEach(item -> {
				 if (item.getId().equals(portIoEntryVo.getMaterialId())) {
					 portIoEntryVo.setMaterialName(item.getAuxName());
				 }
			 });

			 basPortList.forEach(item -> {
				 if (item.getId().equals(portIoEntryVo.getPortId())) {
					 portIoEntryVo.setPortName(item.getAuxName());
				 }
			 });

			 return portIoEntryVo;
		 }).collect(Collectors.toList());
	 }
 }
