<template>
  <div class="register-container">
    <h1>註冊帳號</h1>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <form @submit.prevent="register" class="register-form">
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
        <label for="userName">用戶名稱<span class="required">*</span></label>
        <input 
          id="userName" 
          v-model="form.userName" 
          type="text" 
          placeholder="請輸入用戶名稱"
          required
        />
      </div>
      
      <div class="form-group">
        <label for="email">電子郵件</label>
        <input 
          id="email" 
          v-model="form.email" 
          type="email" 
          placeholder="請輸入電子郵件"
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
      
      <div class="form-group">
        <label for="biography">個人簡介</label>
        <textarea 
          id="biography" 
          v-model="form.biography" 
          placeholder="請簡短介紹自己"
          rows="3"
        ></textarea>
      </div>
      
      <div class="form-group">
        <label for="coverImage">封面圖片URL</label>
        <input 
          id="coverImage" 
          v-model="form.coverImage" 
          type="text" 
          placeholder="請輸入圖片URL"
        />
      </div>
      
      <div class="form-actions">
        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '註冊中...' : '註冊' }}
        </button>
        <button type="button" class="cancel-btn" @click="goToLogin">
          返回登入
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService';
import SanitizerUtil from '@/utils/SanitizerUtil';

export default {
  name: 'Register',
  data() {
    return {
      form: {
        phoneNumber: '',
        userName: '',
        email: '',
        password: '',
        biography: '',
        coverImage: ''
      },
      isSubmitting: false,
      errorMessage: ''
    };
  },
  methods: {
    async register() {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      try {
        // 淨化表單數據，防範XSS攻擊
        const sanitizedForm = SanitizerUtil.sanitizeFormInput({...this.form});
        
        const response = await AuthService.register(sanitizedForm);
        
        if (response.data.success) {
          // 註冊成功，導向登入頁
          this.$router.push({ 
            path: '/login',
            query: { 
              registered: 'true',
              username: sanitizedForm.userName
            }
          });
        } else {
          this.errorMessage = response.data.message || '註冊失敗，請稍後再試';
        }
      } catch (error) {
        if (error.response && error.response.data) {
          this.errorMessage = error.response.data.message || '註冊失敗，請檢查您的輸入';
        } else {
          this.errorMessage = '伺服器連線失敗，請稍後再試';
        }
        console.error('註冊錯誤:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
    goToLogin() {
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.register-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.register-form {
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

input, textarea {
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

.submit-btn, .cancel-btn {
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

.cancel-btn {
  background-color: #f2f2f2;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.error-message {
  background-color: #ffebee;
  color: #d32f2f;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}
</style> 