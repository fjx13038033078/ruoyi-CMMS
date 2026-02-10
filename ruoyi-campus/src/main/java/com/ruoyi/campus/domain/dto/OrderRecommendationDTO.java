package com.ruoyi.campus.domain.dto;

import com.ruoyi.campus.domain.ErrandOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 订单智能推荐 DTO
 * 继承 ErrandOrder，附带匹配度分数用于排序展示
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderRecommendationDTO extends ErrandOrder
{
    private static final long serialVersionUID = 1L;

    /** 综合匹配分 (0~100)，分数越高越推荐 */
    private Double matchScore;

    /** 距离因子得分 (0~1) */
    private Double distanceScore;

    /** 时间因子得分 (0~1) */
    private Double timeScore;

    /** 信誉因子得分 (0~1) */
    private Double creditScore;

    /** 跑腿员与取货点的距离(米) */
    private Double distanceToPickup;
}
