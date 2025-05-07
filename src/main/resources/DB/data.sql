-- 插入測試使用者（密碼使用 BCrypt 加密）
INSERT INTO User (PhoneNumber, UserName, Password, Email, Biography)
VALUES ('1234567890', 'TestUser', '$2a$10$hiuQZHsOQzgIGF/oJaOX9uNkPWe7fL8nF5VYYR6UGkukQ5MxBLHta', 'test@example.com', '這是一個測試帳號');

-- 插入測試發文
INSERT INTO Post (UserID, Content, CreatedAt)
VALUES (1, '這是第一篇測試發文', CURRENT_TIMESTAMP);

INSERT INTO Post (UserID, Content, CreatedAt)
VALUES (1, '這是第二篇測試發文', CURRENT_TIMESTAMP);

-- 插入測試留言
INSERT INTO Comment (UserID, PostID, Content, CreatedAt)
VALUES (1, 1, '這是第一篇發文的留言', CURRENT_TIMESTAMP);
