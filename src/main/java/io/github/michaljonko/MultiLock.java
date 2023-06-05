package io.github.michaljonko;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

import static java.util.Objects.nonNull;

public final class MultiLock implements AutoCloseable {

    private final Path path;
    private final Semaphore semaphore;

    public MultiLock() {
        try {
            this.path = Files.createTempFile("test", "text");
            this.semaphore = new Semaphore(1);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean io() {
        try {
            Files.writeString(path, "1", StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean ioWithParkNanos(long parkingDurationNs) {
        while (!semaphore.tryAcquire()) {
            LockSupport.parkNanos(parkingDurationNs);
        }
        try {
            return io();
        } finally {
            semaphore.release();
        }
    }

    public boolean ioWithSpin() {
        while (!semaphore.tryAcquire()) {
            Thread.onSpinWait();
        }
        try {
            return io();
        } finally {
            semaphore.release();
        }
    }

    @Override
    public void close() {
        if (nonNull(path)) {
            try {
                System.out.println("File size: " + Files.size(path));
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
