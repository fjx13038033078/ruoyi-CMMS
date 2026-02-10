package com.ruoyi.campus.service;

import java.util.List;
import com.ruoyi.campus.domain.CreditLog;

/**
 * 用户信誉变更记录 服务层
 * 
 * @author ruoyi
 */
public interface ICreditLogService
{
    /**
     * 查询信誉变更记录列表
     * 
     * @param creditLog 信誉变更记录
     * @return 信誉变更记录集合
     */
    public List<CreditLog> selectCreditLogList(CreditLog creditLog);

    /**
     * 通过日志ID查询信誉变更记录
     * 
     * @param logId 日志ID
     * @return 信誉变更记录
     */
    public CreditLog selectCreditLogById(Long logId);

    /**
     * 查询用户的信誉变更记录
     * 
     * @param userId 用户ID
     * @return 信誉变更记录集合
     */
    public List<CreditLog> selectCreditLogByUserId(Long userId);

    /**
     * 新增信誉变更记录
     * 
     * @param creditLog 信誉变更记录
     * @return 结果
     */
    public int insertCreditLog(CreditLog creditLog);

    /**
     * 删除信誉变更记录
     * 
     * @param logId 日志ID
     * @return 结果
     */
    public int deleteCreditLogById(Long logId);

    /**
     * 批量删除信誉变更记录
     * 
     * @param logIds 需要删除的日志ID
     * @return 结果
     */
    public int deleteCreditLogByIds(Long[] logIds);
}
