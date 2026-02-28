package com.ruoyi.campus.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 首页仪表盘统计数据 DTO
 */
@Data
public class DashboardStatsDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 订单总数 */
    private Integer totalOrders;

    /** 待接单数 */
    private Integer pendingOrders;

    /** 进行中订单(已接单 + 配送中) */
    private Integer activeOrders;

    /** 已完成订单 */
    private Integer completedOrders;

    /** 已取消订单 */
    private Integer cancelledOrders;

    /** 今日新增订单 */
    private Integer todayOrders;

    /** 订单总金额(suggestedPrice之和) */
    private BigDecimal totalAmount;

    /** 认证学生总数 */
    private Integer totalStudents;

    /** 跑腿员总数 */
    private Integer totalRunners;

    /** 平均信誉分 */
    private BigDecimal avgCreditScore;

    /** 近7天每日订单量 */
    private List<Map<String, Object>> dailyOrderTrend;

    /** 订单状态分布 */
    private List<Map<String, Object>> statusDistribution;

    /** 近7天信誉变更统计 */
    private List<Map<String, Object>> dailyCreditTrend;

    /** 今日完成订单数 */
    private Integer todayCompleted;

    /** 完成率(已完成 / (已完成 + 已取消)) */
    private BigDecimal completionRate;
}
