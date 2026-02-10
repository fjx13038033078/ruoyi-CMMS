package com.ruoyi.campus.mapper;

import java.util.List;
import com.ruoyi.campus.domain.StudentProfile;

/**
 * 校园用户信息扩展 数据层
 * 
 * @author ruoyi
 */
public interface StudentProfileMapper
{
    /**
     * 查询校园用户信息列表
     * 
     * @param studentProfile 校园用户信息
     * @return 校园用户信息集合
     */
    public List<StudentProfile> selectStudentProfileList(StudentProfile studentProfile);

    /**
     * 通过主键查询校园用户信息
     * 
     * @param profileId 主键ID
     * @return 校园用户信息
     */
    public StudentProfile selectStudentProfileById(Long profileId);

    /**
     * 通过用户ID查询校园用户信息
     * 
     * @param userId 用户ID
     * @return 校园用户信息
     */
    public StudentProfile selectStudentProfileByUserId(Long userId);

    /**
     * 新增校园用户信息
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    public int insertStudentProfile(StudentProfile studentProfile);

    /**
     * 修改校园用户信息
     * 
     * @param studentProfile 校园用户信息
     * @return 结果
     */
    public int updateStudentProfile(StudentProfile studentProfile);

    /**
     * 删除校园用户信息
     * 
     * @param profileId 主键ID
     * @return 结果
     */
    public int deleteStudentProfileById(Long profileId);

    /**
     * 批量删除校园用户信息
     * 
     * @param profileIds 需要删除的主键ID
     * @return 结果
     */
    public int deleteStudentProfileByIds(Long[] profileIds);

    /**
     * 校验学号是否唯一
     * 
     * @param studentNo 学号
     * @return 结果
     */
    public StudentProfile checkStudentNoUnique(String studentNo);

    /**
     * 校验用户ID是否已存在
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public StudentProfile checkUserIdUnique(Long userId);
}
