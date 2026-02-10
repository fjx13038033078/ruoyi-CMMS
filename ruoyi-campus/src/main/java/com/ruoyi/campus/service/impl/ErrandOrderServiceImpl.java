package com.ruoyi.campus.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.campus.domain.CreditLog;
import com.ruoyi.campus.domain.ErrandOrder;
import com.ruoyi.campus.domain.StudentProfile;
import com.ruoyi.campus.domain.dto.OrderRecommendationDTO;
import com.ruoyi.campus.mapper.CreditLogMapper;
import com.ruoyi.campus.mapper.ErrandOrderMapper;
import com.ruoyi.campus.mapper.StudentProfileMapper;
import com.ruoyi.campus.match.OrderMatchAlgorithm;
import com.ruoyi.campus.service.IErrandOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 校园跑腿订单 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class ErrandOrderServiceImpl implements IErrandOrderService
{
    @Autowired
    private ErrandOrderMapper errandOrderMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private CreditLogMapper creditLogMapper;

    @Autowired
    private OrderMatchAlgorithm orderMatchAlgorithm;

    /**
     * 查询跑腿订单列表
     * 
     * @param errandOrder 跑腿订单
     * @return 跑腿订单集合
     */
    @Override
    public List<ErrandOrder> selectErrandOrderList(ErrandOrder errandOrder)
    {
        return errandOrderMapper.selectErrandOrderList(errandOrder);
    }

    /**
     * 通过订单ID查询跑腿订单
     * 
     * @param orderId 订单ID
     * @return 跑腿订单
     */
    @Override
    public ErrandOrder selectErrandOrderById(Long orderId)
    {
        return errandOrderMapper.selectErrandOrderById(orderId);
    }

    /**
     * 通过订单编号查询跑腿订单
     * 
     * @param orderNo 订单编号
     * @return 跑腿订单
     */
    @Override
    public ErrandOrder selectErrandOrderByOrderNo(String orderNo)
    {
        return errandOrderMapper.selectErrandOrderByOrderNo(orderNo);
    }

    /**
     * 发布跑腿订单
     * 
     * @param errandOrder 跑腿订单
     * @return 结果
     */
    @Override
    public int insertErrandOrder(ErrandOrder errandOrder)
    {
        // 自动生成唯一订单编号: ER + UUID(去横线,取前20位保证不超过32字符)
        if (StringUtils.isEmpty(errandOrder.getOrderNo()))
        {
            errandOrder.setOrderNo("ER" + IdUtils.simpleUUID().substring(0, 20));
        }
        errandOrder.setOrderStatus("0"); // 待接单
        return errandOrderMapper.insertErrandOrder(errandOrder);
    }

    /**
     * 修改跑腿订单
     * 
     * @param errandOrder 跑腿订单
     * @return 结果
     */
    @Override
    public int updateErrandOrder(ErrandOrder errandOrder)
    {
        return errandOrderMapper.updateErrandOrder(errandOrder);
    }

    /**
     * 删除跑腿订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int deleteErrandOrderById(Long orderId)
    {
        return errandOrderMapper.deleteErrandOrderById(orderId);
    }

    /**
     * 批量删除跑腿订单
     * 
     * @param orderIds 需要删除的订单ID
     * @return 结果
     */
    @Override
    public int deleteErrandOrderByIds(Long[] orderIds)
    {
        return errandOrderMapper.deleteErrandOrderByIds(orderIds);
    }

    /**
     * 接单
     * 
     * @param orderId 订单ID
     * @param runnerId 跑腿员ID
     * @return 结果
     */
    @Override
    @Transactional
    public int acceptOrder(Long orderId, Long runnerId)
    {
        ErrandOrder order = errandOrderMapper.selectErrandOrderById(orderId);
        if (StringUtils.isNull(order))
        {
            throw new ServiceException("订单不存在");
        }
        if (!"0".equals(order.getOrderStatus()))
        {
            throw new ServiceException("该订单当前状态无法接单");
        }
        // 校验跑腿员是否有接单权限
        StudentProfile runner = studentProfileMapper.selectStudentProfileByUserId(runnerId);
        if (StringUtils.isNull(runner) || !"1".equals(runner.getIsRunner()))
        {
            throw new ServiceException("您没有接单权限，请先完成学生认证");
        }
        // 不能接自己发布的订单
        if (order.getPublisherId().longValue() == runnerId.longValue())
        {
            throw new ServiceException("不能接自己发布的订单");
        }

        ErrandOrder update = new ErrandOrder();
        update.setOrderId(orderId);
        update.setRunnerId(runnerId);
        update.setOrderStatus("1"); // 已接单
        return errandOrderMapper.updateErrandOrder(update);
    }

    /**
     * 开始配送
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int startDelivery(Long orderId)
    {
        ErrandOrder order = errandOrderMapper.selectErrandOrderById(orderId);
        if (StringUtils.isNull(order))
        {
            throw new ServiceException("订单不存在");
        }
        if (!"1".equals(order.getOrderStatus()))
        {
            throw new ServiceException("该订单当前状态无法开始配送");
        }

        ErrandOrder update = new ErrandOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("2"); // 配送中
        return errandOrderMapper.updateErrandOrder(update);
    }

    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int completeOrder(Long orderId)
    {
        ErrandOrder order = errandOrderMapper.selectErrandOrderById(orderId);
        if (StringUtils.isNull(order))
        {
            throw new ServiceException("订单不存在");
        }
        if (!"2".equals(order.getOrderStatus()))
        {
            throw new ServiceException("该订单当前状态无法完成");
        }

        ErrandOrder update = new ErrandOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("3"); // 已完成
        update.setFinishTime(new Date());
        int rows = errandOrderMapper.updateErrandOrder(update);

        // 记录信誉分变更 - 跑腿员完成订单加分
        if (rows > 0 && order.getRunnerId() != null)
        {
            StudentProfile runner = studentProfileMapper.selectStudentProfileByUserId(order.getRunnerId());
            if (StringUtils.isNotNull(runner))
            {
                // 判断是否超时
                boolean isLate = order.getDeadlineTime() != null && new Date().after(order.getDeadlineTime());
                int scoreChange = isLate ? -3 : 2;
                String changeType = isLate ? "LATE" : "COMPLETE";
                int newScore = runner.getCreditScore() + scoreChange;

                // 更新信誉分
                StudentProfile profileUpdate = new StudentProfile();
                profileUpdate.setProfileId(runner.getProfileId());
                profileUpdate.setCreditScore(newScore);
                studentProfileMapper.updateStudentProfile(profileUpdate);

                // 记录信誉日志
                CreditLog creditLog = new CreditLog();
                creditLog.setUserId(order.getRunnerId());
                creditLog.setChangeType(changeType);
                creditLog.setScoreChange(scoreChange);
                creditLog.setCurrentScore(newScore);
                creditLog.setRefOrderId(orderId);
                creditLogMapper.insertCreditLog(creditLog);
            }
        }
        return rows;
    }

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelOrder(Long orderId)
    {
        ErrandOrder order = errandOrderMapper.selectErrandOrderById(orderId);
        if (StringUtils.isNull(order))
        {
            throw new ServiceException("订单不存在");
        }
        if ("3".equals(order.getOrderStatus()) || "4".equals(order.getOrderStatus()))
        {
            throw new ServiceException("该订单已完成或已取消，无法操作");
        }

        ErrandOrder update = new ErrandOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("4"); // 已取消
        int rows = errandOrderMapper.updateErrandOrder(update);

        // 如果已经接单后取消，对跑腿员进行信誉扣分
        if (rows > 0 && order.getRunnerId() != null && "1".equals(order.getOrderStatus()))
        {
            StudentProfile runner = studentProfileMapper.selectStudentProfileByUserId(order.getRunnerId());
            if (StringUtils.isNotNull(runner))
            {
                int scoreChange = -5;
                int newScore = runner.getCreditScore() + scoreChange;

                StudentProfile profileUpdate = new StudentProfile();
                profileUpdate.setProfileId(runner.getProfileId());
                profileUpdate.setCreditScore(newScore);
                studentProfileMapper.updateStudentProfile(profileUpdate);

                CreditLog creditLog = new CreditLog();
                creditLog.setUserId(order.getRunnerId());
                creditLog.setChangeType("VIOLATION");
                creditLog.setScoreChange(scoreChange);
                creditLog.setCurrentScore(newScore);
                creditLog.setRefOrderId(orderId);
                creditLogMapper.insertCreditLog(creditLog);
            }
        }
        return rows;
    }

    // ==================== 智能推荐 ====================

    /**
     * 智能推荐订单列表
     * 委托 OrderMatchAlgorithm 进行匹配度计算与排序
     *
     * @param runnerId  跑腿员用户ID
     * @param runnerLat 跑腿员当前纬度
     * @param runnerLng 跑腿员当前经度
     * @return 推荐订单列表(按匹配分降序)
     */
    @Override
    public List<OrderRecommendationDTO> selectRecommendedOrders(Long runnerId, Double runnerLat, Double runnerLng)
    {
        // 1. 校验跑腿员身份
        StudentProfile runner = studentProfileMapper.selectStudentProfileByUserId(runnerId);
        if (StringUtils.isNull(runner) || !"1".equals(runner.getIsRunner()))
        {
            throw new ServiceException("您没有接单权限，请先完成学生认证");
        }

        // 2. 查询所有"待接单"状态的订单
        ErrandOrder query = new ErrandOrder();
        query.setOrderStatus("0");
        List<ErrandOrder> pendingOrders = errandOrderMapper.selectErrandOrderList(query);

        // 3. 获取跑腿员信誉分
        int creditScore = runner.getCreditScore() != null ? runner.getCreditScore() : 0;

        // 4. 调用匹配算法计算并排序
        return orderMatchAlgorithm.calculate(pendingOrders, runnerId, runnerLat, runnerLng, creditScore);
    }

}
