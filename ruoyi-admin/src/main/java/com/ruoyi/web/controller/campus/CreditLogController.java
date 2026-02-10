package com.ruoyi.web.controller.campus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.campus.domain.CreditLog;
import com.ruoyi.campus.service.ICreditLogService;

/**
 * 用户信誉变更记录 操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/campus/credit")
public class CreditLogController extends BaseController
{
    @Autowired
    private ICreditLogService creditLogService;

    /**
     * 查询信誉变更记录列表
     */
    @PreAuthorize("@ss.hasPermi('campus:credit:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreditLog creditLog)
    {
        startPage();
        List<CreditLog> list = creditLogService.selectCreditLogList(creditLog);
        return getDataTable(list);
    }

    /**
     * 查询我的信誉变更记录
     */
    @GetMapping("/mine")
    public AjaxResult myLogs()
    {
        Long userId = SecurityUtils.getUserId();
        return success(creditLogService.selectCreditLogByUserId(userId));
    }

    /**
     * 导出信誉变更记录列表
     */
    @PreAuthorize("@ss.hasPermi('campus:credit:export')")
    @Log(title = "信誉变更记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CreditLog creditLog)
    {
        List<CreditLog> list = creditLogService.selectCreditLogList(creditLog);
        ExcelUtil<CreditLog> util = new ExcelUtil<CreditLog>(CreditLog.class);
        util.exportExcel(response, list, "信誉变更记录数据");
    }

    /**
     * 根据日志ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:credit:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable Long logId)
    {
        return success(creditLogService.selectCreditLogById(logId));
    }

    /**
     * 查询指定用户的信誉变更记录
     */
    @PreAuthorize("@ss.hasPermi('campus:credit:query')")
    @GetMapping(value = "/user/{userId}")
    public AjaxResult getLogsByUserId(@PathVariable Long userId)
    {
        return success(creditLogService.selectCreditLogByUserId(userId));
    }

    /**
     * 删除信誉变更记录
     */
    @PreAuthorize("@ss.hasPermi('campus:credit:remove')")
    @Log(title = "信誉变更记录", businessType = BusinessType.DELETE)
    @PostMapping("/remove/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(creditLogService.deleteCreditLogByIds(logIds));
    }
}
