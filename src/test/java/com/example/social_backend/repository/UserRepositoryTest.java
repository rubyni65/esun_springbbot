package com.example.social_backend.repository;

import com.example.social_backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 使用測試配置文件
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByPhoneNumber_existingPhoneNumber_returnsUser() {
    // 準備
    String phoneNumber = "1234567890";
    User newUser = new User(phoneNumber, "測試用戶", "test@example.com", "password123", null, null);
    userRepository.save(newUser);

    // 執行
    Optional<User> foundUser = userRepository.findByPhoneNumber(phoneNumber);

    // 驗證
    assertTrue(foundUser.isPresent());
    assertEquals(phoneNumber, foundUser.get().getPhoneNumber());
    assertEquals("測試用戶", foundUser.get().getUserName());
  }

  @Test
  void findByPhoneNumber_nonExistingPhoneNumber_returnsEmpty() {
    // 執行
    Optional<User> foundUser = userRepository.findByPhoneNumber("9999999999");

    // 驗證
    assertFalse(foundUser.isPresent());
  }

  @Test
  void save_validUser_storesInDatabase() {
    // 準備
    User newUser = new User("5555555555", "新用戶", "new@example.com", "securepass", "cover.jpg", "簡介");

    // 執行
    User savedUser = userRepository.save(newUser);

    // 驗證
    assertNotNull(savedUser.getUserId());
    assertEquals("5555555555", savedUser.getPhoneNumber());
    assertEquals("新用戶", savedUser.getUserName());
    assertEquals("new@example.com", savedUser.getEmail());
    assertEquals("securepass", savedUser.getPassword());
    assertEquals("cover.jpg", savedUser.getCoverImage());
    assertEquals("簡介", savedUser.getBiography());
  }

  @Test
  void update_existingUser_updatesValues() {
    // 準備
    User user = new User("7777777777", "原始用戶", "original@example.com", "oldpass", null, null);
    User savedUser = userRepository.save(user);
    Long userId = savedUser.getUserId();

    // 執行 - 更新用戶資料
    savedUser.setUserName("更新用戶");
    savedUser.setEmail("updated@example.com");
    userRepository.save(savedUser);

    // 驗證
    Optional<User> updatedUserOpt = userRepository.findById(userId);
    assertTrue(updatedUserOpt.isPresent());
    User updatedUser = updatedUserOpt.get();
    assertEquals("更新用戶", updatedUser.getUserName());
    assertEquals("updated@example.com", updatedUser.getEmail());
    assertEquals("7777777777", updatedUser.getPhoneNumber()); // 保持不變
  }

  @Test
  void delete_existingUser_removesFromDatabase() {
    // 準備
    User user = new User("8888888888", "將被刪除的用戶", "delete@example.com", "password", null, null);
    User savedUser = userRepository.save(user);
    Long userId = savedUser.getUserId();

    // 驗證儲存成功
    assertTrue(userRepository.findById(userId).isPresent());

    // 執行
    userRepository.delete(savedUser);

    // 驗證
    assertFalse(userRepository.findById(userId).isPresent());
  }

  @Test
  void findAll_multipleUsers_returnsAllUsers() {
    // 準備
    userRepository.deleteAll(); // 確保測試前數據庫為空
    User user1 = new User("1111111111", "用戶1", "user1@example.com", "pass1", null, null);
    User user2 = new User("2222222222", "用戶2", "user2@example.com", "pass2", null, null);
    userRepository.save(user1);
    userRepository.save(user2);

    // 執行
    List<User> users = userRepository.findAll();

    // 驗證
    assertEquals(2, users.size());
    assertTrue(users.stream().anyMatch(u -> u.getPhoneNumber().equals("1111111111")));
    assertTrue(users.stream().anyMatch(u -> u.getPhoneNumber().equals("2222222222")));
  }
}