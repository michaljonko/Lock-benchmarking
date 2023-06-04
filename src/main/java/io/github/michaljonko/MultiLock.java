package io.github.michaljonko;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

public final class MultiLock {

  private static boolean io() {
    try {
      Files.delete(Files.createTempFile("test", "text"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public static boolean ioWithParkNanos(Semaphore semaphore, long parkingDurationNs) {
    while (!semaphore.tryAcquire()) {
      LockSupport.parkNanos(parkingDurationNs);
    }
    try {
      return io();
    } finally {
      semaphore.release();
    }
  }

  public static boolean ioWithSpin(Semaphore semaphore) {
    while (!semaphore.tryAcquire()) {
      Thread.onSpinWait();
    }
    try {
      return io();
    } finally {
      semaphore.release();
    }
  }
}
