package io.github.michaljonko;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 1)
@Warmup(time = 3, iterations = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@Threads(32)
public class LockBenchmark {

    @Benchmark
    public boolean measureLockPark(LockParkState state) {
        return state.multiLock()
                .ioWithParkNanos(state.parkDuration());
    }

    @Benchmark
    public boolean measureLockSpin(LockSpinState state) {
        return state.multiLock()
                .ioWithSpin();
    }
}
