package com.zhm.edges.plugins.api.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
  // Generate a new AES key (for demo; in real use, store/load securely)
  public static SecretKey generateKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256); // 128, 192, or 256 bits
    return keyGen.generateKey();
  }

  // Encrypt a string
  public static String encrypt(String data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encrypted);
  }

  // Decrypt a string
  public static String decrypt(String encryptedData, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decoded = Base64.getDecoder().decode(encryptedData);
    byte[] decrypted = cipher.doFinal(decoded);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  // Convert SecretKey to String for storage
  public static String keyToString(SecretKey key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  // Convert String to SecretKey
  public static SecretKey stringToKey(String keyStr) {
    byte[] decoded = Base64.getDecoder().decode(keyStr);
    return new SecretKeySpec(decoded, 0, decoded.length, "AES");
  }
}
