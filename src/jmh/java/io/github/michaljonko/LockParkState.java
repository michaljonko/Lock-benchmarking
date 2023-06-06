package io.github.michaljonko;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@State(Scope.Benchmark)
public class LockParkState {

    @Param({"10", "100"})
    private long parkDuration;
    private MultiLock multiLock;
    private Collection<UUID> uuids;

    @Setup(Level.Iteration)
    public void setup(BenchmarkParams benchmarkParams) {
        multiLock = new MultiLock();
        uuids = IntStream.range(0, benchmarkParams.getThreads())
                .mapToObj(ignored -> UUID.randomUUID())
                .collect(toSet());
    }

    @TearDown(Level.Iteration)
    public void teardownIteration() {
        multiLock.close();
    }

    public long parkDuration() {
        return TimeUnit.MILLISECONDS.toNanos(parkDuration);
    }

    public MultiLock multiLock() {
        return multiLock;
    }

    public Collection<UUID> uuids() {
        return uuids;
    }
}
