package cat.kittens.mods.controller.lib;

import java.util.Optional;

public class DoubleStateOutput<T> {
    private T current, previous;

    public DoubleStateOutput(T current, T previous) {
        this.current = current;
        this.previous = previous;
    }

    public DoubleStateOutput() {
        this(null, null);
    }

    public Optional<T> current() {
        return Optional.ofNullable(current);
    }

    public Optional<T> previous() {
        return Optional.ofNullable(previous);
    }

    public void set(T value) {
        this.previous = current;
        this.current = value;
    }
}
