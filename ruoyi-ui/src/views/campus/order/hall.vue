<template>
  <div class="app-container">
    <!-- 模式切换 & 搜索 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="16">
        <el-radio-group v-model="viewMode" size="small" @change="handleModeChange">
          <el-radio-button label="hall">全部待接单</el-radio-button>
          <el-radio-button label="recommend">智能推荐</el-radio-button>
          <el-radio-button label="myTaken">我接的单</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col :span="8" style="text-align: right;">
        <el-button type="primary" icon="el-icon-refresh" size="small" @click="refreshList">刷新</el-button>
      </el-col>
    </el-row>

    <!-- 智能推荐模式 - 位置输入 -->
    <el-form v-if="viewMode === 'recommend'" :inline="true" size="small" class="mb8">
      <el-form-item label="我的纬度">
        <el-input-number v-model="runnerLat" :precision="6" :step="0.0001" controls-position="right" style="width: 180px" />
      </el-form-item>
      <el-form-item label="我的经度">
        <el-input-number v-model="runnerLng" :precision="6" :step="0.0001" controls-position="right" style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-position" @click="getLocation">自动定位</el-button>
        <el-button type="success" icon="el-icon-search" @click="handleRecommend">获取推荐</el-button>
      </el-form-item>
    </el-form>

    <!-- 大厅搜索条件 -->
    <el-form v-if="viewMode === 'hall'" :model="queryParams" ref="queryForm" :inline="true" size="small" class="mb8">
      <el-form-item label="订单标题" prop="orderTitle">
        <el-input v-model="queryParams.orderTitle" placeholder="请输入关键字" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 订单列表 -->
    <el-table v-loading="loading" :data="orderList" style="width: 100%">
      <el-table-column label="订单标题" prop="orderTitle" :show-overflow-tooltip="true" />
      <el-table-column label="取货地址" prop="pickupAddress" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="送达地址" prop="deliveryAddress" width="180" :show-overflow-tooltip="true" />
      <el-table-column label="建议价格" align="center" width="90">
        <template slot-scope="scope">
          <span style="color: #e6a23c; font-weight: bold;">¥{{ scope.row.suggestedPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否加急" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isUrgent === '1' ? 'danger' : 'info'" size="small">
            {{ scope.row.isUrgent === '1' ? '加急' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="截止时间" align="center" width="160">
        <template slot-scope="scope">
          <span :style="isUrgentTime(scope.row.deadlineTime) ? 'color:#f56c6c;font-weight:bold' : ''">
            {{ parseTime(scope.row.deadlineTime) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.campus_order_status" :value="scope.row.orderStatus" />
        </template>
      </el-table-column>
      <!-- 智能推荐模式特有列 -->
      <el-table-column v-if="viewMode === 'recommend'" label="匹配分" align="center" width="80" sortable prop="matchScore">
        <template slot-scope="scope">
          <span style="color: #409eff; font-weight: bold;">{{ scope.row.matchScore }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="viewMode === 'recommend'" label="距离(米)" align="center" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.distanceToPickup != null ? scope.row.distanceToPickup : '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <!-- 待接单: 显示接单按钮 -->
          <el-button
            v-if="scope.row.orderStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            style="color: #67c23a;"
            @click="handleAccept(scope.row)"
            v-hasPermi="['campus:order:accept']"
          >接单</el-button>
          <!-- 已接单: 显示开始配送 -->
          <el-button
            v-if="scope.row.orderStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-bicycle"
            style="color: #e6a23c;"
            @click="handleDeliver(scope.row)"
            v-hasPermi="['campus:order:deliver']"
          >配送</el-button>
          <!-- 配送中: 显示完成 -->
          <el-button
            v-if="scope.row.orderStatus === '2'"
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            style="color: #67c23a;"
            @click="handleComplete(scope.row)"
            v-hasPermi="['campus:order:complete']"
          >完成</el-button>
          <!-- 待接单/已接单: 显示取消 -->
          <el-button
            v-if="scope.row.orderStatus === '0' || scope.row.orderStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-close"
            style="color: #f56c6c;"
            @click="handleCancel(scope.row)"
            v-hasPermi="['campus:order:cancel']"
          >取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 (大厅和我的接单模式) -->
    <pagination
      v-show="viewMode !== 'recommend' && total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="refreshList"
    />

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ detailForm.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单标题">{{ detailForm.orderTitle }}</el-descriptions-item>
        <el-descriptions-item label="发布者">{{ detailForm.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="跑腿员">{{ detailForm.runnerName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <dict-tag :options="dict.type.campus_order_status" :value="detailForm.orderStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="重量类型">
          <span v-if="detailForm.weightCategory === 'SMALL'">小件</span>
          <span v-else-if="detailForm.weightCategory === 'MEDIUM'">中件</span>
          <span v-else-if="detailForm.weightCategory === 'LARGE'">大件</span>
        </el-descriptions-item>
        <el-descriptions-item label="是否加急">
          <el-tag :type="detailForm.isUrgent === '1' ? 'danger' : 'info'" size="small">
            {{ detailForm.isUrgent === '1' ? '加急' : '普通' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="建议价格">¥{{ detailForm.suggestedPrice }}</el-descriptions-item>
        <el-descriptions-item label="取货地址" :span="2">{{ detailForm.pickupAddress }}</el-descriptions-item>
        <el-descriptions-item label="送达地址" :span="2">{{ detailForm.deliveryAddress }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ parseTime(detailForm.deadlineTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detailForm.finishTime ? parseTime(detailForm.finishTime) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注详情" :span="2">{{ detailForm.orderDesc || '—' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHall, listMyTaken, recommendOrders, getOrder, acceptOrder, deliverOrder, completeOrder, cancelOrder } from "@/api/campus/order";

export default {
  name: "OrderHall",
  dicts: ['campus_order_status'],
  data() {
    return {
      loading: false,
      orderList: [],
      total: 0,
      viewMode: "hall",
      // 大厅查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderTitle: undefined
      },
      // 智能推荐参数
      runnerLat: null,
      runnerLng: null,
      // 详情
      detailOpen: false,
      detailForm: {}
    };
  },
  created() {
    this.getHallList();
  },
  methods: {
    /** 模式切换 */
    handleModeChange(mode) {
      this.orderList = [];
      this.total = 0;
      this.queryParams.pageNum = 1;
      if (mode === "hall") {
        this.getHallList();
      } else if (mode === "myTaken") {
        this.getMyTakenList();
      }
      // recommend 模式需要用户点击按钮触发
    },

    /** 刷新列表 */
    refreshList() {
      if (this.viewMode === "hall") {
        this.getHallList();
      } else if (this.viewMode === "myTaken") {
        this.getMyTakenList();
      } else if (this.viewMode === "recommend") {
        this.handleRecommend();
      }
    },

    /** 查询大厅待接单列表 */
    getHallList() {
      this.loading = true;
      listHall(this.queryParams).then(function(response) {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      }.bind(this));
    },

    /** 查询我接的单 */
    getMyTakenList() {
      this.loading = true;
      listMyTaken(this.queryParams).then(function(response) {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      }.bind(this));
    },

    /** 智能推荐 */
    handleRecommend() {
      if (!this.runnerLat || !this.runnerLng) {
        this.$modal.msgWarning("请先输入您的位置坐标或点击自动定位");
        return;
      }
      this.loading = true;
      recommendOrders({ runnerLat: this.runnerLat, runnerLng: this.runnerLng }).then(function(response) {
        this.orderList = response.data || [];
        this.total = this.orderList.length;
        this.loading = false;
      }.bind(this));
    },

    /** 浏览器自动定位 */
    getLocation() {
      var self = this;
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          function(pos) {
            self.runnerLat = parseFloat(pos.coords.latitude.toFixed(6));
            self.runnerLng = parseFloat(pos.coords.longitude.toFixed(6));
            self.$modal.msgSuccess("定位成功");
          },
          function() {
            self.$modal.msgWarning("定位失败，请手动输入坐标");
          }
        );
      } else {
        this.$modal.msgWarning("浏览器不支持定位，请手动输入坐标");
      }
    },

    /** 搜索 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getHallList();
    },

    /** 重置 */
    resetQuery() {
      this.queryParams.orderTitle = undefined;
      this.handleQuery();
    },

    /** 查看详情 */
    handleDetail(row) {
      getOrder(row.orderId).then(function(response) {
        this.detailForm = response.data;
        this.detailOpen = true;
      }.bind(this));
    },

    /** 接单 */
    handleAccept(row) {
      var self = this;
      this.$modal.confirm('确认接单"' + row.orderTitle + '"？接单后请按时完成配送。').then(function() {
        return acceptOrder(row.orderId);
      }).then(function() {
        self.$modal.msgSuccess("接单成功！请尽快开始配送");
        self.refreshList();
      }).catch(function() {});
    },

    /** 开始配送 */
    handleDeliver(row) {
      var self = this;
      this.$modal.confirm('确认开始配送"' + row.orderTitle + '"？').then(function() {
        return deliverOrder(row.orderId);
      }).then(function() {
        self.$modal.msgSuccess("已开始配送");
        self.refreshList();
      }).catch(function() {});
    },

    /** 完成订单 */
    handleComplete(row) {
      var self = this;
      this.$modal.confirm('确认已完成"' + row.orderTitle + '"的配送？').then(function() {
        return completeOrder(row.orderId);
      }).then(function() {
        self.$modal.msgSuccess("订单已完成，信誉分已更新");
        self.refreshList();
      }).catch(function() {});
    },

    /** 取消订单 */
    handleCancel(row) {
      var self = this;
      this.$modal.confirm('确认取消订单"' + row.orderTitle + '"？已接单后取消将扣除信誉分！').then(function() {
        return cancelOrder(row.orderId);
      }).then(function() {
        self.$modal.msgSuccess("订单已取消");
        self.refreshList();
      }).catch(function() {});
    },

    /** 判断截止时间是否紧迫(小于2小时) */
    isUrgentTime(deadlineTime) {
      if (!deadlineTime) return false;
      var deadline = new Date(deadlineTime).getTime();
      var now = new Date().getTime();
      return (deadline - now) < 2 * 60 * 60 * 1000 && (deadline - now) > 0;
    }
  }
};
</script>
