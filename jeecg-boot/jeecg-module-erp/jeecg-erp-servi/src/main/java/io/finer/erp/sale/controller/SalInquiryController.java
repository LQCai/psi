package io.finer.erp.sale.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.sale.entity.SalInquiry;
import io.finer.erp.sale.service.ISalInquiryService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 询盘
 * @Author: jeecg-boot
 * @Date:   2023-01-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="询盘")
@RestController
@RequestMapping("/sale/salInquiry")
public class SalInquiryController extends JeecgController<SalInquiry, ISalInquiryService> {
	@Autowired
	private ISalInquiryService salInquiryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param salInquiry
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "询盘-分页列表查询")
	@ApiOperation(value="询盘-分页列表查询", notes="询盘-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SalInquiry salInquiry,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SalInquiry> queryWrapper = QueryGenerator.initQueryWrapper(salInquiry, req.getParameterMap());
		Page<SalInquiry> page = new Page<SalInquiry>(pageNo, pageSize);
		IPage<SalInquiry> pageList = salInquiryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "询盘-添加")
	@ApiOperation(value="询盘-添加", notes="询盘-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SalInquiry salInquiry) {
		salInquiryService.save(salInquiry);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param salInquiry
	 * @return
	 */
	@AutoLog(value = "询盘-编辑")
	@ApiOperation(value="询盘-编辑", notes="询盘-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SalInquiry salInquiry) {
		salInquiryService.updateById(salInquiry);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "询盘-通过id删除")
	@ApiOperation(value="询盘-通过id删除", notes="询盘-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		salInquiryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "询盘-批量删除")
	@ApiOperation(value="询盘-批量删除", notes="询盘-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.salInquiryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "询盘-通过id查询")
	@ApiOperation(value="询盘-通过id查询", notes="询盘-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SalInquiry salInquiry = salInquiryService.getById(id);
		return Result.OK(salInquiry);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param salInquiry
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SalInquiry salInquiry) {
      return super.exportXls(request, salInquiry, SalInquiry.class, "询盘");
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
      return super.importExcel(request, response, SalInquiry.class);
  }

}
