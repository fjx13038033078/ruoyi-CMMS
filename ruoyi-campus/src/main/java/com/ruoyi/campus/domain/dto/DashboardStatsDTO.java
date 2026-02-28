package com.ruoyi.campus.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 首页仪表盘统计数据 DTO
 */
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

    /** 近7天每日订单量 [{date:'2026-02-04', count:5}, ...] */
    private List<Map<String, Object>> dailyOrderTrend;

    /** 订单状态分布 [{status:'0', count:10}, ...] */
    private List<Map<String, Object>> statusDistribution;

    /** 近7天信誉变更统计 [{date:'2026-02-04', complete:3, late:1, violation:0}, ...] */
    private List<Map<String, Object>> dailyCreditTrend;

    /** 今日完成订单数 */
    private Integer todayCompleted;

    /** 完成率(已完成 / (已完成 + 已取消)) */
    private BigDecimal completionRate;

    public Integer getTotalOrders()
    {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders)
    {
        this.totalOrders = totalOrders;
    }

    public Integer getPendingOrders()
    {
        return pendingOrders;
    }

    public void setPendingOrders(Integer pendingOrders)
    {
        this.pendingOrders = pendingOrders;
    }

    public Integer getActiveOrders()
    {
        return activeOrders;
    }

    public void setActiveOrders(Integer activeOrders)
    {
        this.activeOrders = activeOrders;
    }

    public Integer getCompletedOrders()
    {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders)
    {
        this.completedOrders = completedOrders;
    }

    public Integer getCancelledOrders()
    {
        return cancelledOrders;
    }

    public void setCancelledOrders(Integer cancelledOrders)
    {
        this.cancelledOrders = cancelledOrders;
    }

    public Integer getTodayOrders()
    {
        return todayOrders;
    }

    public void setTodayOrders(Integer todayOrders)
    {
        this.todayOrders = todayOrders;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalStudents()
    {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents)
    {
        this.totalStudents = totalStudents;
    }

    public Integer getTotalRunners()
    {
        return totalRunners;
    }

    public void setTotalRunners(Integer totalRunners)
    {
        this.totalRunners = totalRunners;
    }

    public BigDecimal getAvgCreditScore()
    {
        return avgCreditScore;
    }

    public void setAvgCreditScore(BigDecimal avgCreditScore)
    {
        this.avgCreditScore = avgCreditScore;
    }

    public List<Map<String, Object>> getDailyOrderTrend()
    {
        return dailyOrderTrend;
    }

    public void setDailyOrderTrend(List<Map<String, Object>> dailyOrderTrend)
    {
        this.dailyOrderTrend = dailyOrderTrend;
    }

    public List<Map<String, Object>> getStatusDistribution()
    {
        return statusDistribution;
    }

    public void setStatusDistribution(List<Map<String, Object>> statusDistribution)
    {
        this.statusDistribution = statusDistribution;
    }

    public List<Map<String, Object>> getDailyCreditTrend()
    {
        return dailyCreditTrend;
    }

    public void setDailyCreditTrend(List<Map<String, Object>> dailyCreditTrend)
    {
        this.dailyCreditTrend = dailyCreditTrend;
    }

    public Integer getTodayCompleted()
    {
        return todayCompleted;
    }

    public void setTodayCompleted(Integer todayCompleted)
    {
        this.todayCompleted = todayCompleted;
    }

    public BigDecimal getCompletionRate()
    {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate)
    {
        this.completionRate = completionRate;
    }
}
