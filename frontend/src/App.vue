<template>
  <div id="app">
    <nav>
      <span v-if="!isLoggedIn">
        <router-link to="/register">註冊</router-link> |
        <router-link to="/login">登入</router-link> |
      </span>
      <router-link to="/posts">發文</router-link>
      <span v-if="isLoggedIn">
        | <a href="#" @click.prevent="logout">登出</a>
      </span>
    </nav>
    <div v-if="tokenValidationMessage" class="alert" :class="{ 'alert-success': !isError, 'alert-danger': isError }">
      {{ tokenValidationMessage }}
    </div>
    <router-view/>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService';

export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false,
      tokenValidationMessage: '',
      isError: false
    };
  },
  created() {
    // 初始化時檢查登入狀態
    this.checkLoginStatus();
    
    // 每次路由變化時都檢查令牌有效性和登入狀態
    this.$router.beforeEach((to, from, next) => {
      this.checkTokenValidity();
      this.checkLoginStatus();
      next();
    });
  },
  mounted() {
    // 監聽自定義事件：登入狀態變更
    window.addEventListener('login-state-changed', this.checkLoginStatus);
  },
  beforeUnmount() {
    // 移除事件監聽
    window.removeEventListener('login-state-changed', this.checkLoginStatus);
  },
  methods: {
    checkLoginStatus() {
      const newLoginState = AuthService.isLoggedIn();
      // 只有當登入狀態確實發生變化時才更新
      if (this.isLoggedIn !== newLoginState) {
        this.isLoggedIn = newLoginState;
        console.log('登入狀態已更新:', this.isLoggedIn);
      }
    },
    async checkTokenValidity() {
      if (AuthService.isLoggedIn()) {
        const isValid = await AuthService.validateToken();
        if (!isValid) {
          this.tokenValidationMessage = '您的登入已過期，請重新登入';
          this.isError = true;
          // 自動重定向到登入頁面
          setTimeout(() => {
            this.tokenValidationMessage = '';
            if (this.$route.path !== '/login') {
              this.$router.push('/login');
            }
          }, 3000);
        }
        this.isLoggedIn = isValid;
      }
    },
    logout() {
      AuthService.logout();
      this.isLoggedIn = false;
      // 觸發登入狀態變更事件
      window.dispatchEvent(new Event('login-state-changed'));
      this.tokenValidationMessage = '您已成功登出';
      this.isError = false;
      // 重定向到登入頁面
      setTimeout(() => {
        this.tokenValidationMessage = '';
        this.$router.push('/login');
      }, 2000);
    }
  }
};
</script>

<style>
#app {
  font-family: 'Microsoft JhengHei', 'PMingLiU', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  margin: 0 10px;
}

nav a.router-link-exact-active {
  color: #42b983;
}

.alert {
  padding: 10px;
  margin: 0 auto 20px;
  width: 80%;
  border-radius: 5px;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style> 