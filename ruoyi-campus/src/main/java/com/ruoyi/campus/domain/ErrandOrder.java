package com.ruoyi.campus.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 校园跑腿订单对象 campus_errand_order
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrandOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单编号(唯一业务号) */
    @Excel(name = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    @Size(min = 0, max = 32, message = "订单编号长度不能超过32个字符")
    private String orderNo;

    /** 发布者ID(关联sys_user) */
    @Excel(name = "发布者ID")
    @NotNull(message = "发布者ID不能为空")
    private Long publisherId;

    /** 接单跑腿员ID(关联sys_user) */
    @Excel(name = "跑腿员ID")
    private Long runnerId;

    /** 订单标题 */
    @Excel(name = "订单标题")
    @NotBlank(message = "订单标题不能为空")
    @Size(min = 0, max = 100, message = "订单标题长度不能超过100个字符")
    private String orderTitle;

    /** 备注详情 */
    @Size(min = 0, max = 500, message = "备注详情长度不能超过500个字符")
    private String orderDesc;

    /** 物品重量类型(SMALL/MEDIUM/LARGE) */
    @Excel(name = "重量类型", readConverterExp = "SMALL=小件,MEDIUM=中件,LARGE=大件")
    private String weightCategory;

    /** 是否加急 (0:否 1:是) */
    @Excel(name = "是否加急", readConverterExp = "0=否,1=是")
    private String isUrgent;

    /** 取货地址文本 */
    @Excel(name = "取货地址")
    @NotBlank(message = "取货地址不能为空")
    @Size(min = 0, max = 255, message = "取货地址长度不能超过255个字符")
    private String pickupAddress;

    /** 取货点经度 */
    private BigDecimal pickupLng;

    /** 取货点纬度 */
    private BigDecimal pickupLat;

    /** 送达地址文本 */
    @Excel(name = "送达地址")
    @NotBlank(message = "送达地址不能为空")
    @Size(min = 0, max = 255, message = "送达地址长度不能超过255个字符")
    private String deliveryAddress;

    /** 送达点经度 */
    private BigDecimal deliveryLng;

    /** 送达点纬度 */
    private BigDecimal deliveryLat;

    /** 预估路程(米) */
    @Excel(name = "预估路程(米)")
    private BigDecimal totalDistance;

    /** 系统建议价格 */
    @Excel(name = "建议价格")
    @NotNull(message = "建议价格不能为空")
    private BigDecimal suggestedPrice;

    /** 最终协商价格 */
    @Excel(name = "最终价格")
    private BigDecimal finalPrice;

    /** 状态 (0:待接单 1:已接单 2:配送中 3:已完成 4:已取消) */
    @Excel(name = "订单状态", readConverterExp = "0=待接单,1=已接单,2=配送中,3=已完成,4=已取消")
    private String orderStatus;

    /** 任务截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "截止时间不能为空")
    private Date deadlineTime;

    /** 实际完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    /** 删除标志(0代表存在 2代表删除) */
    private String delFlag;

    /** 发布者昵称(非数据库字段,用于展示) */
    @Excel(name = "发布者")
    private String publisherName;

    /** 跑腿员昵称(非数据库字段,用于展示) */
    @Excel(name = "跑腿员")
    private String runnerName;
}
