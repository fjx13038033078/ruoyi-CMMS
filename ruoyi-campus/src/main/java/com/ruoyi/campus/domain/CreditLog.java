package com.ruoyi.campus.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 用户信誉变更记录对象 campus_credit_log
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 变更类型 (COMPLETE:完成, LATE:超时, VIOLATION:违规) */
    @Excel(name = "变更类型", readConverterExp = "COMPLETE=完成,LATE=超时,VIOLATION=违规")
    private String changeType;

    /** 变动分数 (如 +2, -5) */
    @Excel(name = "变动分数")
    private Integer scoreChange;

    /** 变动后当前分数 */
    @Excel(name = "当前分数")
    private Integer currentScore;

    /** 关联的订单ID */
    @Excel(name = "关联订单ID")
    private Long refOrderId;

    /** 关联的用户昵称(非数据库字段,用于展示) */
    @Excel(name = "用户昵称")
    private String userName;

    /** 关联的订单编号(非数据库字段,用于展示) */
    @Excel(name = "订单编号")
    private String orderNo;
}
