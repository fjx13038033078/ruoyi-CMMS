package com.ruoyi.campus.service.impl;

import java.util.List;

import com.ruoyi.campus.domain.CreditLog;
import com.ruoyi.campus.mapper.CreditLogMapper;
import com.ruoyi.campus.service.ICreditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户信誉变更记录 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class CreditLogServiceImpl implements ICreditLogService
{
    @Autowired
    private CreditLogMapper creditLogMapper;

    /**
     * 查询信誉变更记录列表
     * 
     * @param creditLog 信誉变更记录
     * @return 信誉变更记录集合
     */
    @Override
    public List<CreditLog> selectCreditLogList(CreditLog creditLog)
    {
        return creditLogMapper.selectCreditLogList(creditLog);
    }

    /**
     * 通过日志ID查询信誉变更记录
     * 
     * @param logId 日志ID
     * @return 信誉变更记录
     */
    @Override
    public CreditLog selectCreditLogById(Long logId)
    {
        return creditLogMapper.selectCreditLogById(logId);
    }

    /**
     * 查询用户的信誉变更记录
     * 
     * @param userId 用户ID
     * @return 信誉变更记录集合
     */
    @Override
    public List<CreditLog> selectCreditLogByUserId(Long userId)
    {
        return creditLogMapper.selectCreditLogByUserId(userId);
    }

    /**
     * 新增信誉变更记录
     * 
     * @param creditLog 信誉变更记录
     * @return 结果
     */
    @Override
    public int insertCreditLog(CreditLog creditLog)
    {
        return creditLogMapper.insertCreditLog(creditLog);
    }

    /**
     * 删除信誉变更记录
     * 
     * @param logId 日志ID
     * @return 结果
     */
    @Override
    public int deleteCreditLogById(Long logId)
    {
        return creditLogMapper.deleteCreditLogById(logId);
    }

    /**
     * 批量删除信誉变更记录
     * 
     * @param logIds 需要删除的日志ID
     * @return 结果
     */
    @Override
    public int deleteCreditLogByIds(Long[] logIds)
    {
        return creditLogMapper.deleteCreditLogByIds(logIds);
    }
}
