package com.zhm.edges.plugins.api.utils;

import javax.crypto.SecretKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AESUtilTest {

  static final Logger logger = LoggerFactory.getLogger(AESUtilTest.class);

  @Test
  void generateKey() throws Exception {
    // Generate and store key securely!
    SecretKey key = AESUtil.generateKey();
    String keyStr = AESUtil.keyToString(key); // Save this string securely
    logger.warn("key: "+keyStr);

    // Encrypt
    String username = "myUser";
    String password = "myPassword";
    String encryptedUsername = AESUtil.encrypt(username, key);
    String encryptedPassword = AESUtil.encrypt(password, key);

    logger.warn("Encrypted Username: " + encryptedUsername);
    logger.warn("Encrypted Password: " + encryptedPassword);

    // Decrypt
    SecretKey loadedKey = AESUtil.stringToKey(keyStr); // Load key from storage
    String decryptedUsername = AESUtil.decrypt(encryptedUsername, loadedKey);
    String decryptedPassword = AESUtil.decrypt(encryptedPassword, loadedKey);

    Assertions.assertThat(decryptedPassword).isEqualTo(password);
    Assertions.assertThat(decryptedUsername).isEqualTo(username);
  }
}
