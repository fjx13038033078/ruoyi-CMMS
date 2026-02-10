package com.ruoyi.campus.match;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.ruoyi.campus.domain.ErrandOrder;
import com.ruoyi.campus.domain.dto.OrderRecommendationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 订单智能匹配算法
 * <p>
 * 综合三因子加权模型：
 *   匹配分 = 距离因子 × 40% + 时间因子 × 30% + 信誉因子 × 30%
 * </p>
 *
 * @author ruoyi
 */
@Component
public class OrderMatchAlgorithm
{
    // ==================== 权重配置 ====================

    /** 距离因子权重 */
    private static final double WEIGHT_DISTANCE = 0.4;
    /** 时间因子权重 */
    private static final double WEIGHT_TIME = 0.3;
    /** 信誉因子权重 */
    private static final double WEIGHT_CREDIT = 0.3;

    // ==================== 归一化基准 ====================

    /** 校园场景最大参考距离(米)，超出此距离得分为0 */
    private static final double MAX_DISTANCE_METERS = 5000.0;
    /** 最大参考时间窗口(分钟)，超过24小时截止的订单紧迫度为0 */
    private static final double MAX_TIME_WINDOW_MINUTES = 1440.0;
    /** 信誉分满分基准 */
    private static final double CREDIT_SCORE_BASE = 100.0;

    // ==================== 常量 ====================

    /** 地球平均半径(米) */
    private static final double EARTH_RADIUS = 6371000.0;

    /**
     * 对待接单订单列表进行智能匹配评分与排序
     *
     * @param pendingOrders  待接单的订单列表
     * @param runnerId       跑腿员用户ID（用于排除自己发布的订单）
     * @param runnerLat      跑腿员当前纬度
     * @param runnerLng      跑腿员当前经度
     * @param runnerCreditScore 跑腿员当前信誉分
     * @return 按匹配分降序排列的推荐订单列表
     */
    public List<OrderRecommendationDTO> calculate(List<ErrandOrder> pendingOrders,
                                                   Long runnerId,
                                                   Double runnerLat,
                                                   Double runnerLng,
                                                   int runnerCreditScore)
    {
        if (pendingOrders == null || pendingOrders.isEmpty())
        {
            return new ArrayList<>();
        }

        // 信誉分归一化到 [0, 1]
        double normalizedCredit = normalizeCredit(runnerCreditScore);

        Date now = new Date();
        List<OrderRecommendationDTO> resultList = new ArrayList<>();

        for (ErrandOrder order : pendingOrders)
        {
            // 排除自己发布的订单
            if (order.getPublisherId() != null && order.getPublisherId().longValue() == runnerId.longValue())
            {
                continue;
            }
            // 排除已过截止时间的订单
            if (order.getDeadlineTime() != null && now.after(order.getDeadlineTime()))
            {
                continue;
            }

            OrderRecommendationDTO dto = new OrderRecommendationDTO();
            BeanUtils.copyProperties(order, dto);

            // ---------- 距离因子 (40%) ----------
            double distScore = calcDistanceScore(runnerLat, runnerLng, order);
            double distMeters = calcDistanceMeters(runnerLat, runnerLng, order);
            dto.setDistanceToPickup(distMeters < 0 ? null : round4(distMeters));
            dto.setDistanceScore(round4(distScore));

            // ---------- 时间因子 (30%) ----------
            double timeScoreVal = calcTimeScore(order, now);
            dto.setTimeScore(round4(timeScoreVal));

            // ---------- 信誉因子 (30%) ----------
            dto.setCreditScore(round4(normalizedCredit));

            // ---------- 综合匹配分 (满分100) ----------
            double matchScore = (distScore * WEIGHT_DISTANCE
                    + timeScoreVal * WEIGHT_TIME
                    + normalizedCredit * WEIGHT_CREDIT) * 100.0;
            dto.setMatchScore(round2(matchScore));

            resultList.add(dto);
        }

        // 按匹配分降序排列
        resultList.sort(Comparator.comparingDouble(OrderRecommendationDTO::getMatchScore).reversed());

        return resultList;
    }

    // ==================== 各因子计算 ====================

    /**
     * 计算距离因子得分 (0~1)
     * 距离越近得分越高，线性归一化
     */
    private double calcDistanceScore(Double runnerLat, Double runnerLng, ErrandOrder order)
    {
        if (runnerLat == null || runnerLng == null
                || order.getPickupLat() == null || order.getPickupLng() == null)
        {
            return 0.0;
        }
        double distMeters = haversineDistance(
                runnerLat, runnerLng,
                order.getPickupLat().doubleValue(),
                order.getPickupLng().doubleValue()
        );
        return Math.max(0.0, 1.0 - distMeters / MAX_DISTANCE_METERS);
    }

    /**
     * 计算跑腿员到取货点的实际距离(米)
     * 无坐标时返回 -1
     */
    private double calcDistanceMeters(Double runnerLat, Double runnerLng, ErrandOrder order)
    {
        if (runnerLat == null || runnerLng == null
                || order.getPickupLat() == null || order.getPickupLng() == null)
        {
            return -1;
        }
        return haversineDistance(
                runnerLat, runnerLng,
                order.getPickupLat().doubleValue(),
                order.getPickupLng().doubleValue()
        );
    }

    /**
     * 计算时间因子得分 (0~1)
     * 剩余时间越少 -> 越紧迫 -> 得分越高
     */
    private double calcTimeScore(ErrandOrder order, Date now)
    {
        if (order.getDeadlineTime() == null)
        {
            return 0.0;
        }
        long remainingMs = order.getDeadlineTime().getTime() - now.getTime();
        double remainingMinutes = remainingMs / 60000.0;
        if (remainingMinutes <= 0)
        {
            return 0.0;
        }
        return Math.max(0.0, 1.0 - remainingMinutes / MAX_TIME_WINDOW_MINUTES);
    }

    /**
     * 信誉分归一化 (0~1)
     */
    private double normalizeCredit(int rawCreditScore)
    {
        double normalized = rawCreditScore / CREDIT_SCORE_BASE;
        return Math.max(0.0, Math.min(normalized, 1.0));
    }

    // ==================== 工具方法 ====================

    /**
     * Haversine 公式计算两个经纬度坐标之间的球面距离(米)
     *
     * @param lat1 纬度1
     * @param lng1 经度1
     * @param lat2 纬度2
     * @param lng2 经度2
     * @return 距离(米)
     */
    private double haversineDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /** 四舍五入保留4位小数 */
    private double round4(double value)
    {
        return Math.round(value * 10000.0) / 10000.0;
    }

    /** 四舍五入保留2位小数 */
    private double round2(double value)
    {
        return Math.round(value * 100.0) / 100.0;
    }
}
