package com.example.social_backend.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

/**
 * HTML內容淨化工具類，用於防範XSS攻擊
 */
@Component
public class SanitizerUtil {

  private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
      .allowCommonBlockElements() // 允許常見區塊元素如 <p>, <div>, <h1>-<h6>
      .allowCommonInlineFormattingElements() // 允許內聯格式元素如 <b>, <i>, <em>, <strong>
      .allowElements("span", "a") // 允許額外的元素
      .allowUrlProtocols("https", "http") // 只允許 http 和 https URL
      .allowAttributes("href").onElements("a") // 允許 a 標籤的 href 屬性
      .requireRelNofollowOnLinks() // 在連結上加入 rel="nofollow"
      .toFactory();

  /**
   * 淨化HTML內容，移除危險標籤和屬性
   *
   * @param input 輸入的可能包含HTML的內容
   * @return 淨化後的安全內容
   */
  public String sanitize(String input) {
    if (input == null) {
      return null;
    }
    return POLICY.sanitize(input);
  }

  /**
   * 將所有HTML標籤轉換為純文本（完全去除HTML）
   *
   * @param input 輸入的可能包含HTML的內容
   * @return 純文本內容，所有HTML標籤被移除
   */
  public String stripAllHtml(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("<[^>]*>", "");
  }

  /**
   * 安全地對URL進行淨化
   *
   * @param url 輸入的URL
   * @return 淨化後的URL，如不安全則返回空字符串
   */
  public String sanitizeUrl(String url) {
    if (url == null) {
      return null;
    }

    // 只允許http和https開頭的URL
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }
    return "";
  }
}