package com.vietbevis.authentication.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
@Slf4j(topic = "Key_Manager_Service")
public class KeyManagerService {

  private static final String RSA_ALGORITHM = "RSA";
  private static final int KEY_SIZE = 2048;

  private enum KeyType {
    ACCESS_TOKEN,
    REFRESH_TOKEN
  }

  @Value("${jwt.key.directory:./keys}")
  private String keyDirectory;

  @Value("${jwt.key.private_access:private_key_access.der}")
  private String privateKeyAccessFilename;

  @Value("${jwt.key.public_access:public_key_access.der}")
  private String publicKeyAccessFilename;

  @Value("${jwt.key.private_refresh:private_key_refresh.der}")
  private String privateKeyRefreshFilename;

  @Value("${jwt.key.public_refresh:public_key_refresh.der}")
  private String publicKeyRefreshFilename;

  @Getter
  private PrivateKey privateKeyAccess;

  @Getter
  private PublicKey publicKeyAccess;

  @Getter
  private PrivateKey privateKeyRefresh;

  @Getter
  private PublicKey publicKeyRefresh;

  @PostConstruct
  public void init() {
    try {
      createKeyDirectoryIfNeeded();

      File privateKeyAccessFile = new File(keyDirectory, privateKeyAccessFilename);
      File publicKeyAccessFile = new File(keyDirectory, publicKeyAccessFilename);
      File privateKeyRefreshFile = new File(keyDirectory, privateKeyRefreshFilename);
      File publicKeyRefreshFile = new File(keyDirectory, publicKeyRefreshFilename);

      if (allKeysExist(privateKeyAccessFile, publicKeyAccessFile, privateKeyRefreshFile,
          publicKeyRefreshFile)) {
        log.info("Đã phát hiện các cặp khóa tồn tại, tiến hành tải khóa...");
        loadKeys(privateKeyAccessFile, publicKeyAccessFile, privateKeyRefreshFile,
            publicKeyRefreshFile);
      } else {
        log.info("Không tìm thấy cặp khóa, tiến hành tạo mới...");
        generateAndSaveKeyPair(privateKeyAccessFile, publicKeyAccessFile, KeyType.ACCESS_TOKEN);
        generateAndSaveKeyPair(privateKeyRefreshFile, publicKeyRefreshFile,
            KeyType.REFRESH_TOKEN);
      }

      log.info("Khởi tạo khóa RSA thành công");
    } catch (IOException e) {
      log.error("Lỗi I/O trong quá trình khởi tạo khóa RSA", e);
      throw new KeyManagementException("Không thể đọc hoặc ghi file khóa", e);
    } catch (NoSuchAlgorithmException e) {
      log.error("Lỗi nhà cung cấp thuật toán RSA trong quá trình khởi tạo", e);
      throw new KeyManagementException("Thuật toán RSA không khả dụng", e);
    } catch (InvalidKeySpecException e) {
      log.error("Định dạng khóa không hợp lệ trong quá trình khởi tạo", e);
      throw new KeyManagementException("Không thể phân tích khóa đã có", e);
    } catch (Exception e) {
      log.error("Lỗi không mong muốn trong quá trình khởi tạo khóa RSA", e);
      throw new KeyManagementException("Không thể khởi tạo khóa RSA", e);
    }
  }

  private void createKeyDirectoryIfNeeded() throws KeyManagementException {
    File directory = new File(keyDirectory);
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        log.error("Không thể tạo thư mục chứa khóa: {}", keyDirectory);
        throw new KeyManagementException("Không thể tạo thư mục chứa khóa: " + keyDirectory);
      }
      log.info("Đã tạo thư mục chứa khóa: {}", keyDirectory);
    }
  }

  private boolean allKeysExist(File... files) {
    for (File file : files) {
      if (!file.exists()) {
        return false;
      }
    }
    return true;
  }

  private void loadKeys(File privateKeyAccessFile, File publicKeyAccessFile,
      File privateKeyRefreshFile, File publicKeyRefreshFile)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

    byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyAccessFile.getAbsolutePath()));
    byte[] publicKeyBytes = Files.readAllBytes(Paths.get(publicKeyAccessFile.getAbsolutePath()));
    privateKeyAccess = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    publicKeyAccess = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

    byte[] privateKeyRefreshBytes = Files.readAllBytes(
        Paths.get(privateKeyRefreshFile.getAbsolutePath()));
    byte[] publicKeyRefreshBytes = Files.readAllBytes(
        Paths.get(publicKeyRefreshFile.getAbsolutePath()));
    privateKeyRefresh = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyRefreshBytes));
    publicKeyRefresh = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyRefreshBytes));

    log.info("Tải các cặp khóa RSA từ file thành công");
  }

  private void generateAndSaveKeyPair(File privateKeyFile, File publicKeyFile, KeyType KeyType)
      throws NoSuchAlgorithmException, IOException {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
    keyPairGenerator.initialize(KEY_SIZE);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    // Store keys in instance variables based on token type
    if (KeyType == KeyManagerService.KeyType.ACCESS_TOKEN) {
      privateKeyAccess = privateKey;
      publicKeyAccess = publicKey;
    } else {
      privateKeyRefresh = privateKey;
      publicKeyRefresh = publicKey;
    }

    saveKeyToFile(privateKeyFile, privateKey.getEncoded());
    saveKeyToFile(publicKeyFile, publicKey.getEncoded());

    log.info("Đã tạo và lưu cặp khóa RSA cho token loại {}", KeyType);
  }

  private void saveKeyToFile(File file, byte[] keyData) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(keyData);
    }
  }

  public static class KeyManagementException extends RuntimeException {

    public KeyManagementException(String message) {
      super(message);
    }

    public KeyManagementException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
