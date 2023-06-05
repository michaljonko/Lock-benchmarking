package io.github.michaljonko;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 1)
@Warmup(time = 3, iterations = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@Threads(48)
public class LockBenchmark {

    @Benchmark
    public boolean measureLockPark(LockParkState state, Control control) {
        return state.multiLock()
                .ioWithParkNanos(state.parkDuration(),control);
    }

    @Benchmark
    public boolean measureLockSpin(LockSpinState state, Control control) {
        return state.multiLock()
                .ioWithSpin(control);
    }
}
