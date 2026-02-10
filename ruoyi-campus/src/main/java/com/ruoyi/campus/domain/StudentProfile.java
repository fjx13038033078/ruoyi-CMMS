package com.ruoyi.campus.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 校园用户信息扩展对象 campus_student_profile
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long profileId;

    /** 关联sys_user的用户ID */
    @Excel(name = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 学号 */
    @Excel(name = "学号")
    @Size(min = 0, max = 30, message = "学号长度不能超过30个字符")
    private String studentNo;

    /** 学生证/一卡通照片URL */
    private String cardImage;

    /** 认证状态 (0:待审核 1:已通过 2:已拒绝) */
    @Excel(name = "认证状态", readConverterExp = "0=待审核,1=已通过,2=已拒绝")
    private String authStatus;

    /** 审核备注(拒绝原因) */
    private String auditRemark;

    /** 当前信誉分(默认100) */
    @Excel(name = "信誉分")
    private Integer creditScore;

    /** 是否有接单权限 (0:无 1:有) */
    @Excel(name = "接单权限", readConverterExp = "0=无,1=有")
    private String isRunner;

    /** 关联的用户昵称(非数据库字段,用于展示) */
    @Excel(name = "用户昵称")
    private String userName;
}
