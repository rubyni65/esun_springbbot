<template>
  <div class="post-page">
    <h1 class="page-title">社群發文</h1>

    <div v-if="AuthService.isLoggedIn()" class="post-form-container">
      <form @submit.prevent="submitPost" class="post-form">
        <h2>發表新內容</h2>
        <div class="form-group">
          <textarea 
            v-model="newPost.content"
            class="form-control post-textarea"
            placeholder="分享你的想法..." 
            required
          ></textarea>
        </div>
        <div class="form-group">
          <input 
            v-model="newPost.image"
            type="text"
            class="form-control"
            placeholder="圖片連結 (選填)"
          >
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary">發布</button>
        </div>
      </form>
    </div>

    <div v-else class="login-notice">
      <p>請先<router-link to="/login">登入</router-link>後才能發文</p>
    </div>

    <div class="post-list">
      <div v-if="loading" class="loading">
        載入中...
      </div>
      <div v-else-if="posts.length === 0" class="no-posts">
        目前沒有發文
      </div>
      <div v-else class="posts-container">
        <div v-for="post in posts" :key="post.postId" class="post-card">
          <div class="post-header">
            <div class="post-user">用戶 ID: {{ post.userId }}</div>
            <div class="post-date">{{ formatDate(post.createdAt) }}</div>
          </div>
          <div class="post-content" v-html="sanitize(post.content)"></div>
          <div v-if="post.image" class="post-image">
            <img :src="sanitizeUrl(post.image)" :alt="'發文圖片 #' + post.postId" />
          </div>
          <div v-if="isOwnPost(post)" class="post-actions">
            <button @click="startEdit(post)" class="btn btn-edit">編輯</button>
            <button @click="confirmDelete(post)" class="btn btn-delete">刪除</button>
          </div>
          
          <!-- 留言區域 -->
          <div class="comments-section">
            <div class="comments-toggle" @click="toggleComments(post.postId)">
              {{ isCommentsVisible(post.postId) ? '隱藏留言' : '查看留言' }}
            </div>
            
            <div v-if="isCommentsVisible(post.postId)" class="comments-container">
              <!-- 留言列表 -->
              <div v-if="commentsByPostId[post.postId]?.length > 0" class="comments-list">
                <div v-for="comment in commentsByPostId[post.postId]" :key="comment.commentId" class="comment-item">
                  <div class="comment-header">
                    <span class="comment-user">用戶 ID: {{ comment.userId }}</span>
                    <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                  </div>
                  <div class="comment-content" v-html="sanitize(comment.content)"></div>
                </div>
              </div>
              <div v-else-if="loadingComments[post.postId]" class="comments-loading">
                載入留言中...
              </div>
              <div v-else class="no-comments">
                暫無留言
              </div>
              
              <!-- 留言表單 -->
              <div v-if="AuthService.isLoggedIn()" class="comment-form-container">
                <form @submit.prevent="submitComment(post.postId)" class="comment-form">
                  <div class="form-group">
                    <textarea 
                      v-model="newComments[post.postId]"
                      class="form-control comment-textarea"
                      placeholder="發表留言..." 
                      required
                    ></textarea>
                  </div>
                  <div class="comment-form-actions">
                    <button type="submit" class="btn btn-primary btn-sm">發表留言</button>
                  </div>
                </form>
              </div>
              <div v-else class="comment-login-notice">
                請<router-link to="/login">登入</router-link>後才能留言
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="editingPost" class="modal-overlay">
      <div class="modal-content">
        <h3>編輯發文</h3>
        <form @submit.prevent="updatePost">
          <div class="form-group">
            <textarea 
              v-model="editingPost.content"
              class="form-control post-textarea"
              placeholder="分享你的想法..." 
              required
            ></textarea>
          </div>
          <div class="form-group">
            <input 
              v-model="editingPost.image"
              type="text"
              class="form-control"
              placeholder="圖片連結 (選填)"
            >
          </div>
          <div class="modal-actions">
            <button type="button" @click="cancelEdit" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary">更新</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import PostService from '@/services/PostService';
import AuthService from '@/services/AuthService';
import CommentService from '@/services/CommentService';
import SanitizerUtil from '@/utils/SanitizerUtil';

export default {
  name: 'Post',
  data() {
    return {
      AuthService, // 使 AuthService 在模板中可用
      posts: [],
      loading: true,
      error: null,
      newPost: {
        content: '',
        image: ''
      },
      editingPost: null, // 用於編輯的發文對象
      currentUserId: null, // 當前登入用戶的 ID
      // 新增留言相關數據
      visibleComments: new Set(), // 存儲已展開留言區域的發文 ID
      commentsByPostId: {}, // 按發文 ID 存儲留言列表
      loadingComments: {}, // 按發文 ID 存儲留言載入狀態
      newComments: {} // 按發文 ID 存儲新留言內容
    }
  },
  created() {
    this.loadPosts();
    this.getCurrentUserId();
  },
  methods: {
    /**
     * 載入所有發文
     */
    loadPosts() {
      this.loading = true;
      PostService.getAllPosts()
        .then(response => {
          this.posts = response.data;
          this.loading = false;
        })
        .catch(error => {
          this.error = '載入發文時發生錯誤：' + (error.response?.data || error.message);
          this.loading = false;
          console.error('載入發文錯誤：', error);
        });
    },
    
    /**
     * 提交新發文
     */
    submitPost() {
      if (!this.newPost.content.trim()) {
        alert('請輸入發文內容');
        return;
      }
      
      // 送出前禁用表單
      this.loading = true;
      
      // 淨化數據，防範XSS攻擊
      const sanitizedPost = SanitizerUtil.sanitizeFormInput({...this.newPost});
      
      PostService.createPost(sanitizedPost)
        .then(response => {
          // 重置表單
          this.newPost.content = '';
          this.newPost.image = '';
          
          // 將新發文添加到列表頂部
          this.posts.unshift(response.data);
          this.loading = false;
        })
        .catch(error => {
          this.loading = false;
          alert('發文失敗：' + (error.response?.data || error.message));
          console.error('發文錯誤：', error);
        });
    },
    
    /**
     * 格式化日期
     */
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('zh-TW', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    },
    
    /**
     * 獲取當前登入用戶的 ID
     */
    getCurrentUserId() {
      const token = AuthService.getToken();
      if (token) {
        try {
          // 從 JWT 解析用戶 ID (簡單方法)
          // 注意：實際應用中最好在後端驗證令牌
          const base64Url = token.split('.')[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
          }).join(''));
          
          const payload = JSON.parse(jsonPayload);
          this.currentUserId = Number(payload.sub); // JWT 的 subject 是用戶 ID
        } catch (e) {
          console.error('無法解析令牌:', e);
        }
      }
    },
    
    /**
     * 檢查發文是否屬於當前用戶
     */
    isOwnPost(post) {
      return this.currentUserId && post.userId === this.currentUserId;
    },
    
    /**
     * 開始編輯發文
     */
    startEdit(post) {
      this.editingPost = {
        postId: post.postId,
        content: post.content,
        image: post.image
      };
    },
    
    /**
     * 取消編輯
     */
    cancelEdit() {
      this.editingPost = null;
    },
    
    /**
     * 更新發文
     */
    updatePost() {
      if (!this.editingPost) return;
      
      this.loading = true;
      
      // 淨化數據，防範XSS攻擊
      const sanitizedPost = SanitizerUtil.sanitizeFormInput({
        content: this.editingPost.content,
        image: this.editingPost.image
      });
      
      PostService.updatePost(this.editingPost.postId, sanitizedPost)
        .then(response => {
          // 更新本地列表中的發文
          const index = this.posts.findIndex(p => p.postId === this.editingPost.postId);
          if (index !== -1) {
            this.posts.splice(index, 1, response.data);
          }
          
          this.editingPost = null;
          this.loading = false;
        })
        .catch(error => {
          this.loading = false;
          alert('更新發文失敗：' + (error.response?.data || error.message));
          console.error('更新發文錯誤：', error);
        });
    },
    
    /**
     * 確認刪除發文
     */
    confirmDelete(post) {
      if (confirm(`確定要刪除這篇發文嗎？此操作無法恢復。`)) {
        this.deletePost(post.postId);
      }
    },
    
    /**
     * 刪除發文
     */
    deletePost(postId) {
      this.loading = true;
      
      PostService.deletePost(postId)
        .then(() => {
          // 從本地列表中移除發文
          this.posts = this.posts.filter(p => p.postId !== postId);
          this.loading = false;
        })
        .catch(error => {
          this.loading = false;
          alert('刪除發文失敗：' + (error.response?.data || error.message));
          console.error('刪除發文錯誤：', error);
        });
    },
    
    /**
     * 切換發文的留言區域顯示狀態
     */
    toggleComments(postId) {
      if (this.isCommentsVisible(postId)) {
        // 如果當前顯示留言，則隱藏
        this.visibleComments.delete(postId);
      } else {
        // 如果當前隱藏留言，則顯示並加載留言
        this.visibleComments.add(postId);
        this.loadComments(postId);
      }
    },
    
    /**
     * 檢查指定發文的留言區域是否可見
     */
    isCommentsVisible(postId) {
      return this.visibleComments.has(postId);
    },
    
    /**
     * 加載指定發文的留言
     */
    loadComments(postId) {
      // 如果已經加載過或正在加載，則不重複加載
      if (this.commentsByPostId[postId] || this.loadingComments[postId]) return;
      
      // 設置加載狀態
      this.loadingComments[postId] = true;
      
      CommentService.getCommentsByPostId(postId)
        .then(response => {
          // 更新留言列表
          this.commentsByPostId[postId] = response.data;
          this.loadingComments[postId] = false;
        })
        .catch(error => {
          console.error(`加載發文 #${postId} 的留言時出錯:`, error);
          this.loadingComments[postId] = false;
        });
    },
    
    /**
     * 提交新留言
     */
    submitComment(postId) {
      // 獲取留言內容
      const content = this.newComments[postId]?.trim();
      
      if (!content) {
        alert('請輸入留言內容');
        return;
      }
      
      // 淨化數據，防範XSS攻擊
      const sanitizedComment = SanitizerUtil.sanitizeFormInput({
        postId,
        content
      });
      
      CommentService.createComment(sanitizedComment.postId, sanitizedComment.content)
        .then(response => {
          // 將新留言添加到列表
          if (!this.commentsByPostId[postId]) {
            this.commentsByPostId[postId] = [];
          }
          this.commentsByPostId[postId].push(response.data);
          
          // 清空留言輸入框
          this.newComments[postId] = '';
        })
        .catch(error => {
          alert('發表留言失敗: ' + (error.response?.data || error.message));
          console.error('發表留言錯誤:', error);
        });
    },
    
    /**
     * 淨化HTML內容，防範XSS攻擊
     */
    sanitize(content) {
      return SanitizerUtil.sanitize(content);
    },
    
    /**
     * 淨化URL，防範XSS攻擊
     */
    sanitizeUrl(url) {
      return SanitizerUtil.sanitizeUrl(url);
    }
  }
}
</script>

<style scoped>
.post-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.post-form-container {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.post-form h2 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.post-textarea {
  min-height: 120px;
  resize: vertical;
}

.form-actions {
  text-align: right;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0069d9;
}

.login-notice {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 30px;
}

.login-notice a {
  color: #007bff;
  text-decoration: none;
}

.post-list {
  margin-top: 30px;
}

.loading, .no-posts {
  text-align: center;
  padding: 20px;
  color: #666;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 0.9rem;
  color: #666;
}

.post-content {
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-image {
  margin-top: 10px;
}

.post-image img {
  max-width: 100%;
  border-radius: 4px;
}

.post-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.btn-edit {
  background-color: #6c757d;
  color: white;
}

.btn-edit:hover {
  background-color: #5a6268;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover {
  background-color: #c82333;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

/* 彈出式編輯表單樣式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 15px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

/* 留言區域樣式 */
.comments-section {
  margin-top: 15px;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.comments-toggle {
  color: #007bff;
  cursor: pointer;
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.comments-toggle:hover {
  text-decoration: underline;
}

.comments-container {
  margin-top: 10px;
}

.comments-list {
  margin-bottom: 15px;
}

.comment-item {
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 5px;
}

.comment-content {
  font-size: 0.95rem;
  white-space: pre-wrap;
  word-break: break-word;
}

.comments-loading, .no-comments {
  text-align: center;
  padding: 10px;
  color: #666;
  font-size: 0.9rem;
}

.comment-form-container {
  margin-top: 15px;
}

.comment-textarea {
  min-height: 60px;
  resize: vertical;
}

.comment-form-actions {
  text-align: right;
  margin-top: 5px;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 0.9rem;
}

.comment-login-notice {
  text-align: center;
  padding: 10px;
  font-size: 0.9rem;
  color: #666;
}

.comment-login-notice a {
  color: #007bff;
  text-decoration: none;
}
</style> 