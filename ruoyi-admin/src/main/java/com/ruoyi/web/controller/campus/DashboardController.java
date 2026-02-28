package com.ruoyi.web.controller.campus;

import com.ruoyi.campus.service.IDashboardService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campus/dashboard")
public class DashboardController extends BaseController
{
    @Autowired
    private IDashboardService dashboardService;

    @GetMapping("/stats")
    public AjaxResult stats()
    {
        Long userId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(userId) || SecurityUtils.hasRole("manager");
        if (isAdmin)
        {
            return AjaxResult.success(dashboardService.getAdminStats());
        }
        else
        {
            return AjaxResult.success(dashboardService.getUserStats(userId));
        }
    }
}
