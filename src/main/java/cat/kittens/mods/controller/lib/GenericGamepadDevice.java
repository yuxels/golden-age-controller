package cat.kittens.mods.controller.lib;

import cat.kittens.mods.controller.input.ChordHelper;

import java.util.HashSet;
import java.util.Objects;

public class GenericGamepadDevice<I extends IGamepadDeviceId> implements IGamepadDevice<I> {
    private final I id;
    private final Type gamepadType;
    private final Input input;
    private final ChordHelper chord;

    public GenericGamepadDevice(
            I id, Type gamepadType, Input input
    ) {
        this.id = id;
        this.gamepadType = gamepadType;
        this.input = input;
        this.chord = new ChordHelper(this, new HashSet<>());
    }

    @Override
    public ChordHelper chord() {
        return chord;
    }

    @Override
    public I id() {
        return id;
    }

    @Override
    public Type gamepadType() {
        return gamepadType;
    }

    @Override
    public Input input() {
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenericGamepadDevice) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gamepadType, input);
    }

    @Override
    public String toString() {
        return "GenericGamepadDevice[" +
                "id=" + id + ", " +
                "gamepadType=" + gamepadType + ", " +
                "input=" + input + ']';
    }

}
