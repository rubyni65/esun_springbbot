<template>
  <div class="login-container">
    <h1>登入帳號</h1>
    
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <form @submit.prevent="login" class="login-form">
      <div class="form-group">
        <label for="phoneNumber">手機號碼<span class="required">*</span></label>
        <input 
          id="phoneNumber" 
          v-model="form.phoneNumber" 
          type="text" 
          placeholder="請輸入手機號碼"
          required
        />
      </div>
      
      <div class="form-group">
        <label for="password">密碼<span class="required">*</span></label>
        <input 
          id="password" 
          v-model="form.password" 
          type="password" 
          placeholder="請輸入密碼"
          required
        />
      </div>
      
      <div class="form-actions">
        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '登入中...' : '登入' }}
        </button>
        <button type="button" class="register-btn" @click="goToRegister">
          還沒有帳號？註冊
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService';
import SanitizerUtil from '@/utils/SanitizerUtil';

export default {
  name: 'Login',
  data() {
    return {
      form: {
        phoneNumber: '',
        password: ''
      },
      isSubmitting: false,
      successMessage: '',
      errorMessage: ''
    };
  },
  created() {
    // 檢查是否從註冊頁面重定向過來
    const registered = this.$route.query.registered;
    const username = this.$route.query.username;
    
    if (registered === 'true' && username) {
      // 淨化顯示的用戶名稱
      this.successMessage = `${SanitizerUtil.stripAllHtml(username)}，您已成功註冊！請使用您的手機號碼和密碼登入。`;
    }
  },
  methods: {
    async login() {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      try {
        // 淨化表單數據，防範XSS攻擊
        const sanitizedForm = SanitizerUtil.sanitizeFormInput({...this.form});
        
        const response = await AuthService.login(sanitizedForm);
        
        if (response.data.success) {
          // 儲存JWT令牌到localStorage
          localStorage.setItem('auth_token', response.data.data.token);
          localStorage.setItem('token_type', response.data.data.tokenType);
          
          // 設置認證頭部
          AuthService.setAuthHeader();
          
          // 觸發登入狀態變更事件
          window.dispatchEvent(new Event('login-state-changed'));
          
          // 顯示成功消息
          this.successMessage = '登入成功，正在跳轉...';
          
          // 延遲後跳轉到發文頁面
          setTimeout(() => {
            this.$router.push('/posts');
          }, 1000);
        } else {
          this.errorMessage = response.data.message || '登入失敗，請稍後再試';
        }
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage = error.response.data.message || '登入失敗，請檢查您的輸入';
        } else {
          this.errorMessage = '伺服器連線失敗，請稍後再試';
        }
        console.error('登入錯誤:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style scoped>
.login-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.login-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.required {
  color: red;
  margin-left: 3px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-btn, .register-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn {
  background-color: #42b983;
  color: white;
}

.submit-btn:hover {
  background-color: #3aa876;
}

.submit-btn:disabled {
  background-color: #a0cfbe;
  cursor: not-allowed;
}

.register-btn {
  background-color: #f2f2f2;
  color: #333;
}

.register-btn:hover {
  background-color: #e0e0e0;
}

.error-message {
  background-color: #ffebee;
  color: #d32f2f;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.success-message {
  background-color: #e8f5e9;
  color: #388e3c;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}
</style> 