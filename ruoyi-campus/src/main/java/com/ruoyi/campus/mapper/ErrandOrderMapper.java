package com.ruoyi.campus.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.campus.domain.ErrandOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 校园跑腿订单 数据层
 * 
 * @author ruoyi
 */
public interface ErrandOrderMapper
{
    /**
     * 查询跑腿订单列表
     * 
     * @param errandOrder 跑腿订单
     * @return 跑腿订单集合
     */
    public List<ErrandOrder> selectErrandOrderList(ErrandOrder errandOrder);

    /**
     * 通过订单ID查询跑腿订单
     * 
     * @param orderId 订单ID
     * @return 跑腿订单
     */
    public ErrandOrder selectErrandOrderById(Long orderId);

    /**
     * 通过订单编号查询跑腿订单
     * 
     * @param orderNo 订单编号
     * @return 跑腿订单
     */
    public ErrandOrder selectErrandOrderByOrderNo(String orderNo);

    /**
     * 新增跑腿订单
     * 
     * @param errandOrder 跑腿订单
     * @return 结果
     */
    public int insertErrandOrder(ErrandOrder errandOrder);

    /**
     * 修改跑腿订单
     * 
     * @param errandOrder 跑腿订单
     * @return 结果
     */
    public int updateErrandOrder(ErrandOrder errandOrder);

    /**
     * 删除跑腿订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteErrandOrderById(Long orderId);

    /**
     * 批量删除跑腿订单
     * 
     * @param orderIds 需要删除的订单ID
     * @return 结果
     */
    public int deleteErrandOrderByIds(Long[] orderIds);

    /**
     * 校验订单编号是否唯一
     * 
     * @param orderNo 订单编号
     * @return 结果
     */
    public ErrandOrder checkOrderNoUnique(String orderNo);

    /** 按状态统计订单数量(全局) */
    public List<Map<String, Object>> countByStatus();

    /** 按状态统计指定用户相关的订单数量(发布者或跑腿员) */
    public List<Map<String, Object>> countByStatusForUser(@Param("userId") Long userId);

    /** 今日新增订单数(全局) */
    public Integer countTodayOrders();

    /** 今日新增订单数(指定用户) */
    public Integer countTodayOrdersForUser(@Param("userId") Long userId);

    /** 今日完成订单数(全局) */
    public Integer countTodayCompleted();

    /** 今日完成订单数(指定用户) */
    public Integer countTodayCompletedForUser(@Param("userId") Long userId);

    /** 订单总金额(全局) */
    public Map<String, Object> sumAmount();

    /** 订单总金额(指定用户) */
    public Map<String, Object> sumAmountForUser(@Param("userId") Long userId);

    /** 近7天每日订单量(全局) */
    public List<Map<String, Object>> dailyOrderTrend();

    /** 近7天每日订单量(指定用户) */
    public List<Map<String, Object>> dailyOrderTrendForUser(@Param("userId") Long userId);
}
