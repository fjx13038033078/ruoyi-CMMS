package com.ruoyi.campus.match;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * 跑腿订单智能定价算法
 * <p>
 * 定价公式：suggestedPrice = (baseFee + distanceFee + weightSurcharge) × urgentMultiplier
 * <ul>
 *   <li>baseFee: 基础服务费(起步价，含首1公里)</li>
 *   <li>distanceFee: 超出1公里部分按阶梯费率计算</li>
 *   <li>weightSurcharge: 物品重量附加费</li>
 *   <li>urgentMultiplier: 加急倍率</li>
 * </ul>
 * </p>
 */
@Component
public class PricingAlgorithm
{
    /** 基础服务费(元)，含首1公里 */
    private static final BigDecimal BASE_FEE = new BigDecimal("3.00");

    /** 1~3公里部分单价(元/公里) */
    private static final BigDecimal RATE_TIER1 = new BigDecimal("1.50");

    /** 3公里以上部分单价(元/公里) */
    private static final BigDecimal RATE_TIER2 = new BigDecimal("2.00");

    /** 小件附加费 */
    private static final BigDecimal WEIGHT_SMALL = BigDecimal.ZERO;

    /** 中件附加费 */
    private static final BigDecimal WEIGHT_MEDIUM = new BigDecimal("2.00");

    /** 大件附加费 */
    private static final BigDecimal WEIGHT_LARGE = new BigDecimal("5.00");

    /** 加急倍率 */
    private static final BigDecimal URGENT_MULTIPLIER = new BigDecimal("1.30");

    /** 最低价格 */
    private static final BigDecimal MIN_PRICE = new BigDecimal("3.00");

    /**
     * 计算建议跑腿费用
     *
     * @param distanceMeters  路径规划距离(米)
     * @param weightCategory  物品重量类型: SMALL / MEDIUM / LARGE
     * @param isUrgent        是否加急: "1"=加急, "0"=普通
     * @return 建议价格(元)，保留2位小数
     */
    public BigDecimal calculate(double distanceMeters, String weightCategory, String isUrgent)
    {
        double distanceKm = distanceMeters / 1000.0;

        // 1. 距离费用(阶梯计算)
        BigDecimal distanceFee = BigDecimal.ZERO;
        if (distanceKm > 1.0)
        {
            double overFirst = distanceKm - 1.0;
            if (overFirst <= 2.0)
            {
                distanceFee = RATE_TIER1.multiply(BigDecimal.valueOf(overFirst));
            }
            else
            {
                distanceFee = RATE_TIER1.multiply(new BigDecimal("2.0"))
                        .add(RATE_TIER2.multiply(BigDecimal.valueOf(overFirst - 2.0)));
            }
        }

        // 2. 重量附加费
        BigDecimal weightFee;
        if ("LARGE".equals(weightCategory))
        {
            weightFee = WEIGHT_LARGE;
        }
        else if ("MEDIUM".equals(weightCategory))
        {
            weightFee = WEIGHT_MEDIUM;
        }
        else
        {
            weightFee = WEIGHT_SMALL;
        }

        // 3. 小计 = 基础费 + 距离费 + 重量费
        BigDecimal subtotal = BASE_FEE.add(distanceFee).add(weightFee);

        // 4. 加急倍率
        if ("1".equals(isUrgent))
        {
            subtotal = subtotal.multiply(URGENT_MULTIPLIER);
        }

        // 5. 不低于最低价
        subtotal = subtotal.max(MIN_PRICE);

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
}
