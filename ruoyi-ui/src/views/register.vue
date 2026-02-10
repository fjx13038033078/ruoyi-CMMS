<template>
  <div class="register-page">
    <div class="register-card">
      <!-- 左侧：磨玻璃装饰区 -->
      <div class="card-left">
        <div class="left-content">
          <div class="logo-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <h2 class="system-name">加入我们</h2>
          <p class="system-slogan">注册成为校园跑腿用户<br/>发布任务 · 接单跑腿 · 赚取收入</p>
          <div class="decoration-dots">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
      <!-- 右侧：注册表单 -->
      <div class="card-right">
        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
          <h3 class="form-title">用户注册</h3>
          <p class="form-subtitle">校园跑腿任务匹配与管理系统</p>
          <el-form-item prop="username">
            <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="请输入账号">
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              auto-complete="off"
              placeholder="请再次确认密码"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="registerForm.code"
              auto-complete="off"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="register-code">
              <img :src="codeUrl" @click="getCode" class="register-code-img"/>
            </div>
          </el-form-item>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleRegister"
            >
              <span v-if="!loading">注 册</span>
              <span v-else>注 册 中...</span>
            </el-button>
            <div style="float: right; margin-top: 8px;">
              <router-link class="link-type" :to="'/login'">已有账号？返回登录</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login";

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.registerForm.uuid = res.uuid;
        }
      });
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          register(this.registerForm).then(res => {
            const username = this.registerForm.username;
            this.$alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              this.$router.push("/login");
            }).catch(() => {});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          })
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
}

.register-card {
  display: flex;
  width: 820px;
  min-height: 520px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* ---- 左侧磨玻璃区 ---- */
.card-left {
  width: 340px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: inherit;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -20px;
    left: -20px;
    right: -20px;
    bottom: -20px;
    background-image: url("../assets/images/login-background.jpg");
    background-size: cover;
    background-position: center;
    filter: blur(12px);
    z-index: 0;
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(76, 175, 80, 0.35);
    z-index: 1;
  }
}

.left-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #fff;
  padding: 40px 30px;

  .logo-icon {
    font-size: 64px;
    margin-bottom: 20px;
    text-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }

  .system-name {
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 4px;
    margin: 0 0 12px 0;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  }

  .system-slogan {
    font-size: 13px;
    opacity: 0.9;
    letter-spacing: 2px;
    margin: 0;
    line-height: 1.8;
  }

  .decoration-dots {
    margin-top: 30px;
    display: flex;
    justify-content: center;
    gap: 8px;

    span {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.6);

      &:first-child {
        background: rgba(255, 255, 255, 0.95);
      }
    }
  }
}

/* ---- 右侧表单区 ---- */
.card-right {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-form {
  width: 100%;

  .form-title {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 6px 0;
  }

  .form-subtitle {
    font-size: 13px;
    color: #909399;
    margin: 0 0 28px 0;
  }

  .el-input {
    height: 42px;

    .el-input__inner {
      height: 42px;
      border-radius: 8px;
      background-color: #f5f7fa;
      border: 1px solid #e4e7ed;
      transition: all 0.3s;

      &:focus {
        border-color: #4CAF50;
        background-color: #fff;
      }
    }

    input {
      height: 42px;
    }
  }

  .input-icon {
    height: 42px;
    width: 14px;
    margin-left: 2px;
  }

  .el-button--primary {
    height: 42px;
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    border: none;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 4px;
    transition: all 0.3s;

    &:hover {
      background: linear-gradient(135deg, #45a049 0%, #3d8b40 100%);
      transform: translateY(-1px);
      box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .link-type {
    color: #4CAF50;
    font-size: 13px;
    text-decoration: none;

    &:hover {
      color: #45a049;
      text-decoration: underline;
    }
  }
}

.register-code {
  width: 33%;
  height: 42px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 6px;
  }
}

.register-code-img {
  height: 42px;
}
</style>
