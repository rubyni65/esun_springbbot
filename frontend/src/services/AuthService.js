import axios from 'axios';

/**
 * 認證服務，處理登入、註冊和JWT令牌相關操作
 */
const AuthService = {
  /**
   * 使用者登入
   * @param {Object} credentials 登入憑證
   * @returns {Promise} 登入結果
   */
  login(credentials) {
    return axios.post('/api/login', credentials);
  },

  /**
   * 使用者註冊
   * @param {Object} userData 使用者資料
   * @returns {Promise} 註冊結果
   */
  register(userData) {
    return axios.post('/api/register', userData);
  },

  /**
   * 登出
   */
  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('token_type');
  },

  /**
   * 獲取JWT令牌
   * @returns {String|null} JWT令牌
   */
  getToken() {
    return localStorage.getItem('auth_token');
  },

  /**
   * 獲取令牌類型
   * @returns {String|null} 令牌類型
   */
  getTokenType() {
    return localStorage.getItem('token_type') || 'Bearer';
  },

  /**
   * 檢查使用者是否已登入
   * @returns {Boolean} 是否已登入
   */
  isLoggedIn() {
    return !!this.getToken();
  },

  /**
   * 設置認證頭部
   */
  setAuthHeader() {
    const token = this.getToken();
    if (token) {
      axios.defaults.headers.common['Authorization'] = `${this.getTokenType()} ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }
};

// 初始化時設置認證頭部
AuthService.setAuthHeader();

export default AuthService; 