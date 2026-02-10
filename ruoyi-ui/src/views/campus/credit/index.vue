<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="变更类型" clearable style="width: 160px">
          <el-option
            v-for="dict in dict.type.campus_credit_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['campus:credit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['campus:credit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="creditList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" align="center" prop="logId" width="80" />
      <el-table-column label="用户昵称" align="center" prop="userName" width="120" />
      <el-table-column label="变更类型" align="center" prop="changeType" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.campus_credit_type" :value="scope.row.changeType" />
        </template>
      </el-table-column>
      <el-table-column label="变动分数" align="center" prop="scoreChange" width="100">
        <template slot-scope="scope">
          <span :style="scope.row.scoreChange > 0 ? 'color:#67c23a;font-weight:bold' : 'color:#f56c6c;font-weight:bold'">
            {{ scope.row.scoreChange > 0 ? '+' + scope.row.scoreChange : scope.row.scoreChange }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="变动后分数" align="center" prop="currentScore" width="100">
        <template slot-scope="scope">
          <span :style="scope.row.currentScore < 60 ? 'color:#f56c6c;font-weight:bold' : ''">
            {{ scope.row.currentScore }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="关联订单" align="center" prop="orderNo" min-width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.orderNo || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="记录时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['campus:credit:remove']"
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
  </div>
</template>

<script>
import { listCredit, delCredit } from "@/api/campus/credit";

export default {
  name: "Credit",
  dicts: ['campus_credit_type'],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      creditList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: undefined,
        changeType: undefined
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      listCredit(this.queryParams).then(function(response) {
        this.creditList = response.rows;
        this.total = response.total;
        this.loading = false;
      }.bind(this));
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(function(item) { return item.logId; });
      this.multiple = !selection.length;
    },
    handleDelete(row) {
      var logIds = row.logId || this.ids;
      var self = this;
      this.$modal.confirm('是否确认删除所选信誉记录？').then(function() {
        return delCredit(logIds);
      }).then(function() {
        self.getList();
        self.$modal.msgSuccess("删除成功");
      }).catch(function() {});
    },
    handleExport() {
      this.download('campus/credit/export', {
        ...this.queryParams
      }, 'credit_' + new Date().getTime() + '.xlsx');
    }
  }
};
</script>
