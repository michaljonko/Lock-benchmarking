package io.github.michaljonko;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static java.util.Objects.nonNull;

public final class MultiLock implements AutoCloseable {

    private final Path path;
    private final AtomicBoolean locked;
    private final AtomicBoolean running;

    public MultiLock() {
        try {
            this.path = Files.createTempFile("test", "text");
            this.locked = new AtomicBoolean(false);
            this.running = new AtomicBoolean(true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean withParkNanos(long parkingDurationNs) {
        while (!locked.compareAndSet(false, true) && running.get()) {
            LockSupport.parkNanos(parkingDurationNs);
        }
        try {
            return running.get() && write();
        } finally {
            locked.set(false);
        }
    }

    public boolean withSpin() {
        while (!locked.compareAndSet(false, true) && running.get()) {
            Thread.onSpinWait();
        }
        try {
            return running.get() && write();
        } finally {
            locked.set(false);
        }
    }

    private boolean write() {
        try {
            Files.writeString(path, "1", StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (nonNull(path)) {
            running.compareAndSet(true, false);
            locked.compareAndSet(true, false);
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
