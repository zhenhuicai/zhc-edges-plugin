package com.zhm.edges.plugins.api.bootstrap;

import com.zhm.edges.plugins.api.WorkingConfiguration;
import com.zhm.edges.plugins.api.WorkingDirContext;
import com.zhm.edges.plugins.api.utils.AESUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zhm.edges.plugins.api.utils.Utils;
import com.zhm.edges.plugins.api.utils.exceptions.EngineException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkingDirBootstrap {
  protected final WorkingConfiguration workingConfiguration;
  AtomicBoolean started = new AtomicBoolean(false);

  public WorkingDirBootstrap(WorkingConfiguration workingConfiguration) {
    this.workingConfiguration = workingConfiguration;
  }

  public void start() {

    if (started.compareAndSet(false, true)) {
      final Path workDir = Utils.checkAndVerifyDir(workingConfiguration.getDir());

      for (final WorkingDirContext.WorkDirSetter setter : WorkingDirContext.setters) {
        Path path = Paths.get(workDir.toString(), setter.getName());
        if (!path.toFile().exists()) {
          try {
            Files.createDirectories(path);
          } catch (IOException e) {
            throw new EngineException("fail create dir " + path.toAbsolutePath(), e);
          }
        }
        setter.getPathConsumer().accept(path);
      }
    }
    // Try to check AES key
    Path aes = WorkingDirContext.INSTANCE.config.resolve("aes");
    File aesFile = aes.toFile();
    if (!aesFile.exists()) {
      try {
        Files.writeString(aes, AESUtil.keyToString(AESUtil.generateKey()));
      } catch (Exception e) {
        LoggerFactory.getLogger(WorkingDirBootstrap.class).warn("fail aes generate", e);
      }
    }
  }
}
