package com.ruoyi.campus.service;

import com.ruoyi.campus.domain.dto.DashboardStatsDTO;

/**
 * 首页仪表盘统计 服务层
 */
public interface IDashboardService
{
    /**
     * 获取管理员视角的全局统计数据
     */
    public DashboardStatsDTO getAdminStats();

    /**
     * 获取普通用户视角的个人统计数据
     *
     * @param userId 当前用户ID
     */
    public DashboardStatsDTO getUserStats(Long userId);
}
