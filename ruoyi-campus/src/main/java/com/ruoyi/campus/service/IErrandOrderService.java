package com.ruoyi.campus.service;

import java.util.List;
import com.ruoyi.campus.domain.ErrandOrder;
import com.ruoyi.campus.domain.dto.OrderRecommendationDTO;

/**
 * 校园跑腿订单 服务层
 * 
 * @author ruoyi
 */
public interface IErrandOrderService
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
     * 发布跑腿订单
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
     * 接单
     * 
     * @param orderId 订单ID
     * @param runnerId 跑腿员ID
     * @return 结果
     */
    public int acceptOrder(Long orderId, Long runnerId);

    /**
     * 开始配送
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int startDelivery(Long orderId);

    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int completeOrder(Long orderId);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int cancelOrder(Long orderId);

    /**
     * 智能推荐订单列表
     * 根据跑腿员位置、订单紧迫度、跑腿员信誉分综合计算匹配度，按匹配度降序排列
     *
     * @param runnerId  跑腿员用户ID
     * @param runnerLat 跑腿员当前纬度
     * @param runnerLng 跑腿员当前经度
     * @return 推荐订单列表(按匹配分降序)
     */
    public List<OrderRecommendationDTO> selectRecommendedOrders(Long runnerId, Double runnerLat, Double runnerLng);
}
