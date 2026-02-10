package com.ruoyi.web.controller.campus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.campus.domain.ErrandOrder;
import com.ruoyi.campus.domain.dto.OrderRecommendationDTO;
import com.ruoyi.campus.service.IErrandOrderService;

/**
 * 校园跑腿订单 操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/campus/order")
public class ErrandOrderController extends BaseController
{
    @Autowired
    private IErrandOrderService errandOrderService;

    /**
     * 查询跑腿订单列表
     * 管理员可查看所有订单，普通用户只能查看自己发布的订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErrandOrder errandOrder)
    {
        Long userId = SecurityUtils.getUserId();
        // 非管理员用户只能查看自己发布的订单
        if (!SecurityUtils.isAdmin(userId))
        {
            errandOrder.setPublisherId(userId);
        }
        startPage();
        List<ErrandOrder> list = errandOrderService.selectErrandOrderList(errandOrder);
        return getDataTable(list);
    }

    /**
     * 查询我接的订单(跑腿员视角)
     */
    @PreAuthorize("@ss.hasPermi('campus:order:list')")
    @GetMapping("/myTaken")
    public TableDataInfo myTakenList(ErrandOrder errandOrder)
    {
        errandOrder.setRunnerId(SecurityUtils.getUserId());
        startPage();
        List<ErrandOrder> list = errandOrderService.selectErrandOrderList(errandOrder);
        return getDataTable(list);
    }

    /**
     * 查询大厅可接订单(待接单状态)
     */
    @PreAuthorize("@ss.hasPermi('campus:order:list')")
    @GetMapping("/hall")
    public TableDataInfo hallList(ErrandOrder errandOrder)
    {
        errandOrder.setOrderStatus("0"); // 待接单
        startPage();
        List<ErrandOrder> list = errandOrderService.selectErrandOrderList(errandOrder);
        return getDataTable(list);
    }

    /**
     * 智能推荐订单列表
     * 根据跑腿员当前位置，综合距离、时间紧迫度、信誉分计算匹配度排序
     *
     * @param runnerLat 跑腿员当前纬度
     * @param runnerLng 跑腿员当前经度
     * @return 推荐订单列表
     */
    @PreAuthorize("@ss.hasPermi('campus:order:list')")
    @GetMapping("/recommend")
    public AjaxResult recommendOrders(Double runnerLat, Double runnerLng)
    {
        Long runnerId = SecurityUtils.getUserId();
        List<OrderRecommendationDTO> list = errandOrderService.selectRecommendedOrders(runnerId, runnerLat, runnerLng);
        return success(list);
    }

    /**
     * 导出跑腿订单列表
     */
    @PreAuthorize("@ss.hasPermi('campus:order:export')")
    @Log(title = "跑腿订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ErrandOrder errandOrder)
    {
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId))
        {
            errandOrder.setPublisherId(userId);
        }
        List<ErrandOrder> list = errandOrderService.selectErrandOrderList(errandOrder);
        ExcelUtil<ErrandOrder> util = new ExcelUtil<ErrandOrder>(ErrandOrder.class);
        util.exportExcel(response, list, "跑腿订单数据");
    }

    /**
     * 根据订单ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(errandOrderService.selectErrandOrderById(orderId));
    }

    /**
     * 发布跑腿订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:add')")
    @Log(title = "跑腿订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ErrandOrder errandOrder)
    {
        errandOrder.setPublisherId(SecurityUtils.getUserId());
        errandOrder.setCreateBy(SecurityUtils.getUsername());
        return toAjax(errandOrderService.insertErrandOrder(errandOrder));
    }

    /**
     * 修改跑腿订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:edit')")
    @Log(title = "跑腿订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody ErrandOrder errandOrder)
    {
        return toAjax(errandOrderService.updateErrandOrder(errandOrder));
    }

    /**
     * 删除跑腿订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:remove')")
    @Log(title = "跑腿订单", businessType = BusinessType.DELETE)
    @PostMapping("/remove/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(errandOrderService.deleteErrandOrderByIds(orderIds));
    }

    /**
     * 接单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:accept')")
    @Log(title = "跑腿订单-接单", businessType = BusinessType.UPDATE)
    @PostMapping("/accept/{orderId}")
    public AjaxResult accept(@PathVariable Long orderId)
    {
        return toAjax(errandOrderService.acceptOrder(orderId, SecurityUtils.getUserId()));
    }

    /**
     * 开始配送
     */
    @PreAuthorize("@ss.hasPermi('campus:order:deliver')")
    @Log(title = "跑腿订单-开始配送", businessType = BusinessType.UPDATE)
    @PostMapping("/deliver/{orderId}")
    public AjaxResult deliver(@PathVariable Long orderId)
    {
        return toAjax(errandOrderService.startDelivery(orderId));
    }

    /**
     * 完成订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:complete')")
    @Log(title = "跑腿订单-完成", businessType = BusinessType.UPDATE)
    @PostMapping("/complete/{orderId}")
    public AjaxResult complete(@PathVariable Long orderId)
    {
        return toAjax(errandOrderService.completeOrder(orderId));
    }

    /**
     * 取消订单
     */
    @PreAuthorize("@ss.hasPermi('campus:order:cancel')")
    @Log(title = "跑腿订单-取消", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        return toAjax(errandOrderService.cancelOrder(orderId));
    }
}
