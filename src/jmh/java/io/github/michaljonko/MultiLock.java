package io.github.michaljonko;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.UUID;
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

    public boolean ioWithParkNanos(long parkingDurationNs) {
        while (!locked.compareAndSet(false, true) && running.get()) {
            LockSupport.parkNanos(parkingDurationNs);
        }
        try {
            return running.get() && write();
        } finally {
            locked.set(false);
        }
    }

    public boolean ioWithSpin() {
        while (!locked.compareAndSet(false, true) && running.get()) {
            Thread.onSpinWait();
        }
        try {
            return running.get() && write();
        } finally {
            locked.set(false);
        }
    }

    public long countWithParkNanos(Collection<UUID> uuids, long parkingDurationNs) {
        while (!locked.compareAndSet(false, true) && running.get()) {
            LockSupport.parkNanos(parkingDurationNs);
        }
        try {
            if (running.get()) {
                return uuids.stream()
                        .map(UUID::toString)
                        .map(String::toLowerCase)
                        .map(String::chars)
                        .map(chars -> chars.filter(value -> value == 'a').count())
                        .reduce(0L, Long::sum);

            }
            return Long.MIN_VALUE;
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
