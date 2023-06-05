package io.github.michaljonko;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LockParkState {

    @Param({"100"})
    private long parkDuration;
    private MultiLock multiLock;

    @Setup(Level.Trial)
    public void setup() {
        multiLock = new MultiLock();
    }

    @TearDown(Level.Trial)
    public void teardown() {
        multiLock.close();
    }

    public long parkDuration() {
        return TimeUnit.MILLISECONDS.toNanos(parkDuration);
    }

    public MultiLock multiLock() {
        return multiLock;
    }
}
