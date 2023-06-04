package io.github.michaljonko;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LockParkState {

  @Param({"10", "100"})
  private long parkDuration;
  private Semaphore semaphore;

  @Setup(Level.Trial)
  public void setup() {
    semaphore = new Semaphore(1, true);
  }

  @TearDown(Level.Trial)
  public void teardown() {
    semaphore.release();
  }

  public long parkDuration() {
    return TimeUnit.MILLISECONDS.toNanos(parkDuration);
  }

  public Semaphore semaphore() {
    return semaphore;
  }
}
