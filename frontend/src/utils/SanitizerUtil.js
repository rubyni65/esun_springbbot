import DOMPurify from 'dompurify';

/**
 * 前端內容淨化工具，用於防範XSS攻擊
 */
export default {
  /**
   * 淨化HTML內容，移除危險標籤和屬性
   * @param {string} input 輸入的可能包含HTML的內容
   * @returns {string} 淨化後的安全內容
   */
  sanitize(input) {
    if (!input) return input;
    return DOMPurify.sanitize(input);
  },

  /**
   * 將HTML轉換為純文本（完全去除HTML）
   * @param {string} input 輸入的可能包含HTML的內容
   * @returns {string} 純文本內容，所有HTML標籤被移除
   */
  stripAllHtml(input) {
    if (!input) return input;
    return DOMPurify.sanitize(input, { ALLOWED_TAGS: [] });
  },

  /**
   * 安全地對URL進行淨化
   * @param {string} url 輸入的URL
   * @returns {string} 淨化後的URL
   */
  sanitizeUrl(url) {
    if (!url) return url;

    // 只允許http和https開頭的URL
    const sanitized = DOMPurify.sanitize(url);
    if (sanitized.startsWith('http://') || sanitized.startsWith('https://')) {
      return sanitized;
    }
    return '';
  },

  /**
   * 淨化表單輸入，防止XSS攻擊
   * @param {Object} formData 表單數據對象
   * @returns {Object} 淨化後的表單數據
   */
  sanitizeFormInput(formData) {
    const sanitizedForm = {};

    // 遍歷表單的每個字段並進行淨化
    for (const key in formData) {
      if (Object.prototype.hasOwnProperty.call(formData, key)) {
        const value = formData[key];

        // 針對不同字段類型採用不同的淨化策略
        if (key.includes('content') || key.includes('biography')) {
          // 允許基本格式的內容
          sanitizedForm[key] = this.sanitize(value);
        } else if (key.includes('image') || key.includes('url')) {
          // URL類型的內容
          sanitizedForm[key] = this.sanitizeUrl(value);
        } else {
          // 其他字段全部轉為純文本
          sanitizedForm[key] = this.stripAllHtml(value);
        }
      }
    }

    return sanitizedForm;
  }
}; 