package io.github.michaljonko;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.concurrent.Semaphore;

@State(Scope.Benchmark)
public class LockSpinState {

  private Semaphore semaphore;

  @Setup(Level.Trial)
  public void setup() {
    semaphore = new Semaphore(1, true);
  }

  @TearDown(Level.Trial)
  public void teardown() {
    semaphore.release();
  }

  public Semaphore semaphore() {
    return semaphore;
  }
}
