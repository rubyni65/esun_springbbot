import axios from 'axios';
import AuthService from './AuthService';

/**
 * 留言服務，處理留言相關的 API 調用
 */
const CommentService = {
  /**
   * 創建新留言
   * 
   * @param {number} postId - 發文ID
   * @param {string} content - 留言內容
   * @returns {Promise} - 返回創建的留言資料
   */
  createComment(postId, content) {
    // 確保已設置 Authorization 頭
    AuthService.setAuthHeader();
    return axios.post('/api/comments', { postId, content });
  },

  /**
   * 獲取指定發文的所有留言
   * 
   * @param {number} postId - 發文ID
   * @returns {Promise} - 返回留言列表
   */
  getCommentsByPostId(postId) {
    return axios.get(`/api/comments/post/${postId}`);
  },

  /**
   * 獲取當前登入用戶的所有留言
   * 
   * @returns {Promise} - 返回留言列表
   */
  getUserComments() {
    // 確保已設置 Authorization 頭
    AuthService.setAuthHeader();
    return axios.get('/api/comments/user');
  }
};

export default CommentService; 