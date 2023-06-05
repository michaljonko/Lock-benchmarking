package io.github.michaljonko;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class LockSpinState {

    private MultiLock multiLock;

    @Setup(Level.Trial)
    public void setup() {
        multiLock = new MultiLock();
    }

    @TearDown(Level.Trial)
    public void teardown() {
        multiLock.close();
    }

    public MultiLock multiLock() {
        return multiLock;
    }
}
