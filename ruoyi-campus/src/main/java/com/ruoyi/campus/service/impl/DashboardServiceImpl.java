package com.ruoyi.campus.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.ruoyi.campus.domain.StudentProfile;
import com.ruoyi.campus.domain.dto.DashboardStatsDTO;
import com.ruoyi.campus.mapper.CreditLogMapper;
import com.ruoyi.campus.mapper.ErrandOrderMapper;
import com.ruoyi.campus.mapper.StudentProfileMapper;
import com.ruoyi.campus.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements IDashboardService
{
    @Autowired
    private ErrandOrderMapper errandOrderMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private CreditLogMapper creditLogMapper;

    @Override
    public DashboardStatsDTO getAdminStats()
    {
        DashboardStatsDTO dto = new DashboardStatsDTO();

        List<Map<String, Object>> statusList = errandOrderMapper.countByStatus();
        fillStatusCounts(dto, statusList);

        dto.setTodayOrders(errandOrderMapper.countTodayOrders());
        dto.setTodayCompleted(errandOrderMapper.countTodayCompleted());

        Map<String, Object> amountMap = errandOrderMapper.sumAmount();
        dto.setTotalAmount(new BigDecimal(amountMap.get("totalAmount").toString()));

        calcCompletionRate(dto);

        Map<String, Object> profileStats = studentProfileMapper.countProfileStats();
        dto.setTotalStudents(((Number) profileStats.get("totalStudents")).intValue());
        dto.setTotalRunners(((Number) profileStats.get("totalRunners")).intValue());

        Map<String, Object> avgMap = studentProfileMapper.avgCreditScore();
        dto.setAvgCreditScore(new BigDecimal(avgMap.get("avgScore").toString()));

        dto.setDailyOrderTrend(errandOrderMapper.dailyOrderTrend());
        dto.setStatusDistribution(statusList);
        dto.setDailyCreditTrend(creditLogMapper.dailyCreditTrend());

        return dto;
    }

    @Override
    public DashboardStatsDTO getUserStats(Long userId)
    {
        DashboardStatsDTO dto = new DashboardStatsDTO();

        List<Map<String, Object>> statusList = errandOrderMapper.countByStatusForUser(userId);
        fillStatusCounts(dto, statusList);

        dto.setTodayOrders(errandOrderMapper.countTodayOrdersForUser(userId));
        dto.setTodayCompleted(errandOrderMapper.countTodayCompletedForUser(userId));

        Map<String, Object> amountMap = errandOrderMapper.sumAmountForUser(userId);
        dto.setTotalAmount(new BigDecimal(amountMap.get("totalAmount").toString()));

        calcCompletionRate(dto);

        StudentProfile profile = studentProfileMapper.selectStudentProfileByUserId(userId);
        if (profile != null)
        {
            dto.setAvgCreditScore(profile.getCreditScore() != null
                    ? new BigDecimal(profile.getCreditScore()) : BigDecimal.ZERO);
        }
        else
        {
            dto.setAvgCreditScore(BigDecimal.ZERO);
        }
        dto.setTotalStudents(null);
        dto.setTotalRunners(null);

        dto.setDailyOrderTrend(errandOrderMapper.dailyOrderTrendForUser(userId));
        dto.setStatusDistribution(statusList);
        dto.setDailyCreditTrend(creditLogMapper.dailyCreditTrendForUser(userId));

        return dto;
    }

    private void fillStatusCounts(DashboardStatsDTO dto, List<Map<String, Object>> statusList)
    {
        int total = 0, pending = 0, active = 0, completed = 0, cancelled = 0;
        for (Map<String, Object> item : statusList)
        {
            String status = item.get("status").toString();
            int cnt = ((Number) item.get("count")).intValue();
            total += cnt;
            switch (status)
            {
                case "0":
                    pending = cnt;
                    break;
                case "1":
                case "2":
                    active += cnt;
                    break;
                case "3":
                    completed = cnt;
                    break;
                case "4":
                    cancelled = cnt;
                    break;
            }
        }
        dto.setTotalOrders(total);
        dto.setPendingOrders(pending);
        dto.setActiveOrders(active);
        dto.setCompletedOrders(completed);
        dto.setCancelledOrders(cancelled);
    }

    private void calcCompletionRate(DashboardStatsDTO dto)
    {
        int finished = dto.getCompletedOrders() + dto.getCancelledOrders();
        if (finished > 0)
        {
            BigDecimal rate = new BigDecimal(dto.getCompletedOrders())
                    .divide(new BigDecimal(finished), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(1, RoundingMode.HALF_UP);
            dto.setCompletionRate(rate);
        }
        else
        {
            dto.setCompletionRate(BigDecimal.ZERO);
        }
    }
}
