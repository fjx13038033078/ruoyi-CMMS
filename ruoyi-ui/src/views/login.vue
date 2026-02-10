<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 左侧：磨玻璃装饰区 -->
      <div class="card-left">
        <div class="left-content">
          <div class="logo-icon">
            <i class="el-icon-bicycle"></i>
          </div>
          <h2 class="system-name">校园跑腿</h2>
          <p class="system-slogan">智能匹配 · 高效配送 · 信用保障</p>
          <div class="decoration-dots">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
      <!-- 右侧：登录表单 -->
      <div class="card-right">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <h3 class="form-title">欢迎登录</h3>
          <p class="form-subtitle">校园跑腿任务匹配与管理系统</p>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div style="float: right; margin-top: 8px;" v-if="register">
              <router-link class="link-type" :to="'/register'">还没有账号？立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: true,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
}

.login-card {
  display: flex;
  width: 820px;
  min-height: 460px;
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
    background: inherit;
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

.login-form {
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
    margin: 0 0 30px 0;
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

  .remember-me {
    margin: 0 0 20px 0;
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

.login-code {
  width: 33%;
  height: 42px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 6px;
  }
}

.login-code-img {
  height: 42px;
}
</style>
