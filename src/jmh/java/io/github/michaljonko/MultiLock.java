package io.github.michaljonko;

import org.openjdk.jmh.infra.Control;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.nonNull;

public final class MultiLock implements AutoCloseable {

    private final Path path;
    private final ReentrantLock lock;

    public MultiLock() {
        try {
            this.path = Files.createTempFile("test", "text");
            this.lock = new ReentrantLock();
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

    public boolean ioWithParkNanos(long parkingDurationNs, org.openjdk.jmh.infra.Control control) {
        while (!lock.tryLock()) {
            if (control.stopMeasurement) {
                return false;
            }
            LockSupport.parkNanos(parkingDurationNs);
        }
        try {
            return io();
        } finally {
            lock.unlock();
        }
    }

    public boolean ioWithSpin(Control control) {
        while (!lock.tryLock()) {
            if (control.stopMeasurement) {
                return false;
            }
            Thread.onSpinWait();
        }
        try {
            return io();
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        //lock.unlock();
    }

    @Override
    public void close() {
        if (nonNull(path)) {
            try {
                //lock.unlock();
                System.out.println("File size: " + Files.size(path));
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
