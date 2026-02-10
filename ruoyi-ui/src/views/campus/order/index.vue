<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select
          v-model="queryParams.orderStatus"
          placeholder="订单状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in dict.type.campus_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单标题" prop="orderTitle">
        <el-input
          v-model="queryParams.orderTitle"
          placeholder="请输入订单标题"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['campus:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['campus:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['campus:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['campus:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单编号" align="center" prop="orderNo" width="200" />
      <el-table-column label="订单标题" align="center" prop="orderTitle" :show-overflow-tooltip="true" />
      <el-table-column label="发布者" align="center" prop="publisherName" width="100" />
      <el-table-column label="跑腿员" align="center" prop="runnerName" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.runnerName || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="重量类型" align="center" prop="weightCategory" width="90">
        <template slot-scope="scope">
          <span v-if="scope.row.weightCategory === 'SMALL'">小件</span>
          <span v-else-if="scope.row.weightCategory === 'MEDIUM'">中件</span>
          <span v-else-if="scope.row.weightCategory === 'LARGE'">大件</span>
          <span v-else>{{ scope.row.weightCategory }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否加急" align="center" prop="isUrgent" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isUrgent === '1' ? 'danger' : 'info'" size="small">
            {{ scope.row.isUrgent === '1' ? '加急' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="建议价格" align="center" prop="suggestedPrice" width="90">
        <template slot-scope="scope">
          <span>¥{{ scope.row.suggestedPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="orderStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.campus_order_status" :value="scope.row.orderStatus" />
        </template>
      </el-table-column>
      <el-table-column label="截止时间" align="center" prop="deadlineTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deadlineTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['campus:order:edit']"
            v-if="scope.row.orderStatus === '0'"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['campus:order:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单标题" prop="orderTitle">
              <el-input v-model="form.orderTitle" placeholder="请输入订单标题，如：取快递" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="重量类型" prop="weightCategory">
              <el-select v-model="form.weightCategory" placeholder="请选择" style="width: 100%">
                <el-option label="小件" value="SMALL" />
                <el-option label="中件" value="MEDIUM" />
                <el-option label="大件" value="LARGE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否加急" prop="isUrgent">
              <el-radio-group v-model="form.isUrgent">
                <el-radio label="0">普通</el-radio>
                <el-radio label="1">加急</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 取货点 - 地图选点 -->
        <el-divider content-position="left">取货点</el-divider>
        <el-row>
          <el-col :span="24">
            <el-form-item label="取货地址" prop="pickupAddress">
              <el-input v-model="form.pickupAddress" placeholder="请在下方地图上选点或搜索地址" readonly>
                <template slot="append">
                  <span v-if="form.pickupLng && form.pickupLat" style="font-size: 12px; color: #67c23a;">
                    {{ form.pickupLng }}, {{ form.pickupLat }}
                  </span>
                  <span v-else style="font-size: 12px; color: #909399;">未选点</span>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地图选点" class="map-form-item">
              <map-location-selector
                v-if="open"
                :init-lng="form.pickupLng || 116.397470"
                :init-lat="form.pickupLat || 39.908823"
                :init-address="form.pickupAddress || ''"
                amap-key="6a315262c9424c19d98bccd8169a5f2d"
                security-js-code="447e8e95ce1fb2619f4858586d9eece6"
                height="280px"
                @select="handlePickupSelect"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 送达点 - 地图选点 -->
        <el-divider content-position="left">送达点</el-divider>
        <el-row>
          <el-col :span="24">
            <el-form-item label="送达地址" prop="deliveryAddress">
              <el-input v-model="form.deliveryAddress" placeholder="请在下方地图上选点或搜索地址" readonly>
                <template slot="append">
                  <span v-if="form.deliveryLng && form.deliveryLat" style="font-size: 12px; color: #67c23a;">
                    {{ form.deliveryLng }}, {{ form.deliveryLat }}
                  </span>
                  <span v-else style="font-size: 12px; color: #909399;">未选点</span>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地图选点" class="map-form-item">
              <map-location-selector
                v-if="open"
                :init-lng="form.deliveryLng || 116.397470"
                :init-lat="form.deliveryLat || 39.908823"
                :init-address="form.deliveryAddress || ''"
                amap-key="6a315262c9424c19d98bccd8169a5f2d"
                security-js-code="447e8e95ce1fb2619f4858586d9eece6"
                height="280px"
                @select="handleDeliverySelect"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 价格与时间 -->
        <el-divider content-position="left">费用与时间</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="建议价格" prop="suggestedPrice">
              <el-input-number v-model="form.suggestedPrice" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间" prop="deadlineTime">
              <el-date-picker
                v-model="form.deadlineTime"
                type="datetime"
                placeholder="选择截止时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注详情" prop="orderDesc">
              <el-input v-model="form.orderDesc" type="textarea" :rows="3" placeholder="请输入备注详情" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

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
        <el-descriptions-item label="最终价格">{{ detailForm.finalPrice ? '¥' + detailForm.finalPrice : '—' }}</el-descriptions-item>
        <el-descriptions-item label="预估距离">{{ detailForm.totalDistance ? detailForm.totalDistance + ' 米' : '—' }}</el-descriptions-item>
        <el-descriptions-item label="取货地址" :span="2">{{ detailForm.pickupAddress }}</el-descriptions-item>
        <el-descriptions-item label="送达地址" :span="2">{{ detailForm.deliveryAddress }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ parseTime(detailForm.deadlineTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detailForm.finishTime ? parseTime(detailForm.finishTime) : '—' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detailForm.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注详情" :span="2">{{ detailForm.orderDesc || '—' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, addOrder, updateOrder, delOrder } from "@/api/campus/order";
import MapLocationSelector from "@/components/MapLocationSelector";

export default {
  name: "Order",
  components: { MapLocationSelector },
  dicts: ['campus_order_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 订单表格数据
      orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 详情弹窗
      detailOpen: false,
      // 详情表单
      detailForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: undefined,
        orderStatus: undefined,
        orderTitle: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderTitle: [
          { required: true, message: "订单标题不能为空", trigger: "blur" }
        ],
        pickupAddress: [
          { required: true, message: "取货地址不能为空", trigger: "blur" }
        ],
        deliveryAddress: [
          { required: true, message: "送达地址不能为空", trigger: "blur" }
        ],
        suggestedPrice: [
          { required: true, message: "建议价格不能为空", trigger: "blur" }
        ],
        deadlineTime: [
          { required: true, message: "截止时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询订单列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        orderId: undefined,
        orderTitle: undefined,
        orderDesc: undefined,
        weightCategory: "SMALL",
        isUrgent: "0",
        pickupAddress: undefined,
        pickupLng: undefined,
        pickupLat: undefined,
        deliveryAddress: undefined,
        deliveryLng: undefined,
        deliveryLat: undefined,
        suggestedPrice: undefined,
        deadlineTime: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "发布跑腿订单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const orderId = row.orderId || this.ids;
      getOrder(orderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改跑腿订单";
      });
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      getOrder(row.orderId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.orderId != undefined) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
              this.$modal.msgSuccess("发布成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const orderIds = row.orderId || this.ids;
      this.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('campus/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`);
    },
    /** 取货点地图选点回调 */
    handlePickupSelect(location) {
      this.form.pickupAddress = location.address;
      this.form.pickupLng = location.lng;
      this.form.pickupLat = location.lat;
    },
    /** 送达点地图选点回调 */
    handleDeliverySelect(location) {
      this.form.deliveryAddress = location.address;
      this.form.deliveryLng = location.lng;
      this.form.deliveryLat = location.lat;
    }
  }
};
</script>

<style scoped>
.map-form-item >>> .el-form-item__content {
  line-height: normal;
}
</style>
