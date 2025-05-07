package com.example.social_backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SanitizerUtilTest {

  private SanitizerUtil sanitizerUtil;

  @BeforeEach
  void setUp() {
    sanitizerUtil = new SanitizerUtil();
  }

  @Test
  void sanitize_nullInput_returnsNull() {
    // 執行
    String result = sanitizerUtil.sanitize(null);

    // 驗證
    assertNull(result);
  }

  @Test
  void sanitize_safeHtml_returnsSanitizedContent() {
    // 準備
    String safeHtml = "<p>這是<b>安全的</b> <i>HTML</i>內容</p>";

    // 執行
    String result = sanitizerUtil.sanitize(safeHtml);

    // 驗證 - 允許的標籤應被保留
    assertTrue(result.contains("<p>"));
    assertTrue(result.contains("<b>安全的</b>"));
    assertTrue(result.contains("<i>HTML</i>"));
  }

  @Test
  void sanitize_unsafeHtml_stripsUnsafeTags() {
    // 準備
    String unsafeHtml = "<script>alert('XSS');</script><p>這是內容</p><img src=\"x\" onerror=\"alert('XSS')\">";

    // 執行
    String result = sanitizerUtil.sanitize(unsafeHtml);

    // 驗證 - 危險標籤應被移除
    assertFalse(result.contains("<script>"));
    assertFalse(result.contains("alert('XSS')"));
    assertTrue(result.contains("<p>這是內容</p>"));
    assertFalse(result.contains("<img"));
    assertFalse(result.contains("onerror"));
  }

  @Test
  void sanitize_htmlWithLinks_addsNofollow() {
    // 準備
    String htmlWithLink = "<a href=\"https://example.com\">連結</a>";

    // 執行
    String result = sanitizerUtil.sanitize(htmlWithLink);

    // 驗證 - 連結應保留且添加 rel="nofollow"
    assertTrue(result.contains("rel=\"nofollow\""));
    assertTrue(result.contains("href=\"https://example.com\""));
  }

  @Test
  void stripAllHtml_nullInput_returnsNull() {
    // 執行
    String result = sanitizerUtil.stripAllHtml(null);

    // 驗證
    assertNull(result);
  }

  @Test
  void stripAllHtml_htmlContent_removesAllTags() {
    // 準備
    String html = "<p>這是<b>帶有<i>多種</i>標籤</b>的<a href=\"https://example.com\">內容</a></p>";

    // 執行
    String result = sanitizerUtil.stripAllHtml(html);

    // 驗證 - 所有標籤應被移除，只剩純文本
    assertEquals("這是帶有多種標籤的內容", result);
  }

  @Test
  void sanitizeUrl_nullInput_returnsNull() {
    // 執行
    String result = sanitizerUtil.sanitizeUrl(null);

    // 驗證
    assertNull(result);
  }

  @Test
  void sanitizeUrl_httpUrl_returnsOriginalUrl() {
    // 準備
    String url = "http://example.com";

    // 執行
    String result = sanitizerUtil.sanitizeUrl(url);

    // 驗證 - http 協議的 URL 應被保留
    assertEquals(url, result);
  }

  @Test
  void sanitizeUrl_httpsUrl_returnsOriginalUrl() {
    // 準備
    String url = "https://example.com";

    // 執行
    String result = sanitizerUtil.sanitizeUrl(url);

    // 驗證 - https 協議的 URL 應被保留
    assertEquals(url, result);
  }

  @Test
  void sanitizeUrl_javascriptUrl_returnsEmptyString() {
    // 準備
    String url = "javascript:alert('XSS')";

    // 執行
    String result = sanitizerUtil.sanitizeUrl(url);

    // 驗證 - 非 http/https 協議的 URL 應返回空字符串
    assertEquals("", result);
  }

  @Test
  void sanitizeUrl_dataUrl_returnsEmptyString() {
    // 準備
    String url = "data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4=";

    // 執行
    String result = sanitizerUtil.sanitizeUrl(url);

    // 驗證 - 非 http/https 協議的 URL 應返回空字符串
    assertEquals("", result);
  }

  @Test
  void sanitizeUrl_relativeUrl_returnsEmptyString() {
    // 準備
    String url = "/path/to/resource";

    // 執行
    String result = sanitizerUtil.sanitizeUrl(url);

    // 驗證 - 相對路徑應返回空字符串
    assertEquals("", result);
  }
}