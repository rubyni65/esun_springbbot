import axios from 'axios';
import AuthService from './AuthService';

/**
 * 發文服務，處理發文相關的 API 調用
 */
const PostService = {
  /**
   * 獲取所有發文
   * @returns {Promise} 包含所有發文的 Promise
   */
  getAllPosts() {
    return axios.get('/api/posts');
  },

  /**
   * 獲取特定使用者的發文
   * @param {Number} userId 使用者 ID
   * @returns {Promise} 包含使用者發文的 Promise
   */
  getUserPosts(userId) {
    return axios.get(`/api/posts/user/${userId}`);
  },

  /**
   * 獲取單一發文
   * @param {Number} postId 發文 ID
   * @returns {Promise} 包含發文資料的 Promise
   */
  getPost(postId) {
    return axios.get(`/api/posts/${postId}`);
  },

  /**
   * 創建新發文
   * @param {Object} postData 發文資料 (content, image)
   * @returns {Promise} 包含創建結果的 Promise
   */
  createPost(postData) {
    // 確保已設置 Authorization 頭
    AuthService.setAuthHeader();
    return axios.post('/api/posts', postData);
  },

  /**
   * 更新發文
   * @param {Number} postId 發文 ID
   * @param {Object} postData 發文更新資料 (content, image)
   * @returns {Promise} 包含更新結果的 Promise
   */
  updatePost(postId, postData) {
    // 確保已設置 Authorization 頭
    AuthService.setAuthHeader();
    return axios.put(`/api/posts/${postId}`, postData);
  },

  /**
   * 刪除發文
   * @param {Number} postId 發文 ID
   * @returns {Promise} 包含刪除結果的 Promise
   */
  deletePost(postId) {
    // 確保已設置 Authorization 頭
    AuthService.setAuthHeader();
    return axios.delete(`/api/posts/${postId}`);
  }
};

export default PostService; 