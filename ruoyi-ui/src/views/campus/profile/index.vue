<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch && !isCommonUser" label-width="68px">
      <el-form-item label="用户昵称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户昵称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学号" prop="studentNo">
        <el-input
          v-model="queryParams.studentNo"
          placeholder="请输入学号"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="认证状态" prop="authStatus">
        <el-select v-model="queryParams.authStatus" placeholder="认证状态" clearable style="width: 160px">
          <el-option
            v-for="dict in dict.type.campus_auth_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="接单权限" prop="isRunner">
        <el-select v-model="queryParams.isRunner" placeholder="接单权限" clearable style="width: 130px">
          <el-option label="有" value="1" />
          <el-option label="无" value="0" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          :disabled="!canAdd"
          @click="handleAdd"
          v-hasPermi="['campus:profile:add']"
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
          v-hasPermi="['campus:profile:edit']"
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
          v-hasPermi="['campus:profile:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['campus:profile:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="profileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户昵称" align="center" prop="userName" width="120" />
      <el-table-column label="学号" align="center" prop="studentNo" width="140" />
      <el-table-column label="学生证照片" align="center" prop="cardImage" width="100">
        <template slot-scope="scope">
          <image-preview v-if="scope.row.cardImage" :src="scope.row.cardImage" :width="50" :height="50" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="认证状态" align="center" prop="authStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.campus_auth_status" :value="scope.row.authStatus" />
        </template>
      </el-table-column>
      <el-table-column label="信誉分" align="center" prop="creditScore" width="90">
        <template slot-scope="scope">
          <span :style="scope.row.creditScore < 60 ? 'color:#f56c6c;font-weight:bold' : scope.row.creditScore >= 90 ? 'color:#67c23a;font-weight:bold' : ''">
            {{ scope.row.creditScore }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="接单权限" align="center" prop="isRunner" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isRunner === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.isRunner === '1' ? '有' : '无' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['campus:profile:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            style="color: #67c23a;"
            @click="handleAudit(scope.row, '1')"
            v-if="scope.row.authStatus === '0'"
            v-hasPermi="['campus:profile:audit']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            style="color: #f56c6c;"
            @click="handleAudit(scope.row, '2')"
            v-if="scope.row.authStatus === '0'"
            v-hasPermi="['campus:profile:audit']"
          >拒绝</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['campus:profile:remove']"
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

    <!-- 添加或修改学生信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="550px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户姓名">
          <el-input :value="form.userName" disabled />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="学生证照片" prop="cardImage">
          <image-upload v-model="form.cardImage" :limit="1" />
        </el-form-item>
        <el-form-item label="接单权限" prop="isRunner">
          <el-radio-group v-model="form.isRunner">
            <el-radio label="0">无</el-radio>
            <el-radio label="1">有</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="信誉分" prop="creditScore">
          <el-input-number v-model="form.creditScore" :min="0" :max="200" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审核拒绝对话框 -->
    <el-dialog title="审核拒绝" :visible.sync="rejectOpen" width="450px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.auditRemark" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReject">确认拒绝</el-button>
        <el-button @click="rejectOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProfile, getProfile, getMyProfile, addProfile, updateProfile, delProfile, auditProfile } from "@/api/campus/profile";

export default {
  name: "Profile",
  dicts: ['campus_auth_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      profileList: [],
      title: "",
      open: false,
      hasProfile: false,
      rejectOpen: false,
      rejectForm: {
        profileId: undefined,
        authStatus: "2",
        auditRemark: ""
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        studentNo: undefined,
        authStatus: undefined,
        isRunner: undefined
      },
      form: {},
      rules: {
        studentNo: [
          { required: true, message: "学号不能为空", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    isCommonUser() {
      var roles = this.$store.state.user.roles || [];
      return roles.length === 1 && roles.indexOf("common") !== -1;
    },
    /** 普通用户已有学生信息时不允许新增 */
    canAdd() {
      if (this.isCommonUser && this.hasProfile) {
        return false;
      }
      return true;
    }
  },
  created() {
    this.getList();
    this.checkMyProfile();
  },
  methods: {
    /** 检查当前用户是否已有学生信息 */
    checkMyProfile() {
      getMyProfile().then(function(res) {
        this.hasProfile = res.data != null;
      }.bind(this));
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      listProfile(this.queryParams).then(function(response) {
        this.profileList = response.rows;
        this.total = response.total;
        this.loading = false;
      }.bind(this));
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        profileId: undefined,
        userId: undefined,
        userName: undefined,
        studentNo: undefined,
        cardImage: undefined,
        authStatus: "0",
        isRunner: "0",
        creditScore: 100
      };
      this.resetForm("form");
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
      this.ids = selection.map(function(item) { return item.profileId; });
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      if (!this.canAdd) {
        this.$modal.msgWarning("您已提交过学生信息，无法重复新增");
        return;
      }
      this.reset();
      this.form.userId = this.$store.state.user.id;
      this.form.userName = this.$store.getters.name;
      this.open = true;
      this.title = "新增学生信息";
    },
    handleUpdate(row) {
      this.reset();
      var profileId = row.profileId || this.ids;
      getProfile(profileId).then(function(response) {
        this.form = response.data;
        this.open = true;
        this.title = "修改学生信息";
      }.bind(this));
    },
    submitForm() {
      var self = this;
      this.$refs["form"].validate(function(valid) {
        if (valid) {
          if (self.form.profileId != undefined) {
            updateProfile(self.form).then(function() {
              self.$modal.msgSuccess("修改成功");
              self.open = false;
              self.getList();
            });
          } else {
            addProfile(self.form).then(function() {
              self.$modal.msgSuccess("新增成功");
              self.open = false;
              self.hasProfile = true;
              self.getList();
            });
          }
        }
      });
    },
    /** 审核操作 */
    handleAudit(row, status) {
      if (status === "1") {
        // 通过
        var self = this;
        this.$modal.confirm('确认通过"' + (row.userName || row.studentNo) + '"的学生认证？通过后将自动获得接单权限。').then(function() {
          return auditProfile({ profileId: row.profileId, authStatus: "1" });
        }).then(function() {
          self.$modal.msgSuccess("审核通过");
          self.getList();
        }).catch(function() {});
      } else {
        // 拒绝 - 弹出原因输入框
        this.rejectForm.profileId = row.profileId;
        this.rejectForm.authStatus = "2";
        this.rejectForm.auditRemark = "";
        this.rejectOpen = true;
      }
    },
    /** 提交拒绝 */
    submitReject() {
      var self = this;
      auditProfile(this.rejectForm).then(function() {
        self.$modal.msgSuccess("已拒绝");
        self.rejectOpen = false;
        self.getList();
      });
    },
    handleDelete(row) {
      var profileIds = row.profileId || this.ids;
      var self = this;
      this.$modal.confirm('是否确认删除所选数据项？').then(function() {
        return delProfile(profileIds);
      }).then(function() {
        self.getList();
        self.checkMyProfile();
        self.$modal.msgSuccess("删除成功");
      }).catch(function() {});
    },
    handleExport() {
      this.download('campus/profile/export', {
        ...this.queryParams
      }, 'profile_' + new Date().getTime() + '.xlsx');
    }
  }
};
</script>
