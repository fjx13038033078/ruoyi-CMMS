package com.ruoyi.campus.service.impl;

import java.util.List;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.campus.domain.StudentProfile;
import com.ruoyi.campus.mapper.StudentProfileMapper;
import com.ruoyi.campus.service.IStudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 校园用户信息扩展 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class StudentProfileServiceImpl implements IStudentProfileService
{
    @Autowired
    private StudentProfileMapper studentProfileMapper;

    /**
     * 查询校园用户信息列表
     * 
     * @param studentProfile 校园用户信息
     * @return 校园用户信息集合
     */
    @Override
    public List<StudentProfile> selectStudentProfileList(StudentProfile studentProfile)
    {
        return studentProfileMapper.selectStudentProfileList(studentProfile);
    }

    /**
     * 通过主键查询校园用户信息
     * 
     * @param profileId 主键ID
     * @return 校园用户信息
     */
    @Override
    public StudentProfile selectStudentProfileById(Long profileId)
    {
        return studentProfileMapper.selectStudentProfileById(profileId);
    }

    /**
     * 通过用户ID查询校园用户信息
     * 
     * @param userId 用户ID
     * @return 校园用户信息
     */
    @Override
    public StudentProfile selectStudentProfileByUserId(Long userId)
    {
        return studentProfileMapper.selectStudentProfileByUserId(userId);
    }

    /**
     * 新增校园用户信息
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    @Override
    public int insertStudentProfile(StudentProfile studentProfile)
    {
        return studentProfileMapper.insertStudentProfile(studentProfile);
    }

    /**
     * 修改校园用户信息
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    @Override
    public int updateStudentProfile(StudentProfile studentProfile)
    {
        return studentProfileMapper.updateStudentProfile(studentProfile);
    }

    /**
     * 删除校园用户信息
     * 
     * @param profileId 主键ID
     * @return 结果
     */
    @Override
    public int deleteStudentProfileById(Long profileId)
    {
        return studentProfileMapper.deleteStudentProfileById(profileId);
    }

    /**
     * 批量删除校园用户信息
     * 
     * @param profileIds 需要删除的主键ID
     * @return 结果
     */
    @Override
    public int deleteStudentProfileByIds(Long[] profileIds)
    {
        return studentProfileMapper.deleteStudentProfileByIds(profileIds);
    }

    /**
     * 校验学号是否唯一
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    @Override
    public boolean checkStudentNoUnique(StudentProfile studentProfile)
    {
        Long profileId = StringUtils.isNull(studentProfile.getProfileId()) ? -1L : studentProfile.getProfileId();
        StudentProfile info = studentProfileMapper.checkStudentNoUnique(studentProfile.getStudentNo());
        if (StringUtils.isNotNull(info) && info.getProfileId().longValue() != profileId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户ID是否已注册
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserIdUnique(StudentProfile studentProfile)
    {
        Long profileId = StringUtils.isNull(studentProfile.getProfileId()) ? -1L : studentProfile.getProfileId();
        StudentProfile info = studentProfileMapper.checkUserIdUnique(studentProfile.getUserId());
        if (StringUtils.isNotNull(info) && info.getProfileId().longValue() != profileId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 审核校园用户认证
     * 
     * @param studentProfile 校园用户信息(包含authStatus和auditRemark)
     * @return 结果
     */
    @Override
    public int auditStudentProfile(StudentProfile studentProfile)
    {
        StudentProfile update = new StudentProfile();
        update.setProfileId(studentProfile.getProfileId());
        update.setAuthStatus(studentProfile.getAuthStatus());
        update.setAuditRemark(studentProfile.getAuditRemark());
        // 审核通过则自动授予接单权限
        if ("1".equals(studentProfile.getAuthStatus()))
        {
            update.setIsRunner("1");
        }
        return studentProfileMapper.updateStudentProfile(update);
    }

    /**
     * 根据用户ID查询学生信息(便捷方法,用于查询当前登录用户)
     * 
     * @param userId 用户ID
     * @return 校园用户信息
     */
    @Override
    public StudentProfile selectProfileByUserId(Long userId)
    {
        return studentProfileMapper.selectStudentProfileByUserId(userId);
    }
}
