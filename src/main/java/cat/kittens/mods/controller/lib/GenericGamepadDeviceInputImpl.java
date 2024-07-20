package cat.kittens.mods.controller.lib;

import java.time.Duration;
import java.util.*;

public class GenericGamepadDeviceInputImpl implements IGamepadDevice.Input {
    protected Map<Button, Long> faces;
    protected Map<IGamepadDevice.Input.Axis, Float> axes;
    protected Map<IGamepadDevice.Input.Axis, Float> deadZones;

    public GenericGamepadDeviceInputImpl() {
        this.faces = new HashMap<>();
        this.axes = new HashMap<>();
        this.deadZones = new HashMap<>();
    }

    @Override
    public OptionalLong getPressDuration(Button button) {
        Long value = faces.get(button);
        if (value == null)
            return OptionalLong.empty();
        return OptionalLong.of(System.currentTimeMillis() - value);
    }

    @Override
    public void setButtonState(Button button, boolean pressed) {
        if (pressed) {
            if (!faces.containsKey(button)) faces.put(button, System.currentTimeMillis());
        } else {
            faces.remove(button);
        }
    }

    @Override
    public float getDeadZone(Axis axis) {
        return deadZones.getOrDefault(axis, 0.25f);
    }

    @Override
    public void setDeadZone(Axis axis, float value) {
        deadZones.put(axis, value);
    }

    @Override
    public OptionalDouble getAxisValue(Axis axis) {
        Float value = axes.get(axis);
        return value != null ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    @Override
    public void setAxisValue(Axis axis, float value) {
        if (value > getDeadZone(axis))
            axes.put(axis,value);
        else
            axes.remove(axis);
    }

    @Override
    public void clear() {
        axes.clear();
        faces.clear();
    }

    @Override
    public void rumble(short lowFreq, short highFreq, Duration duration) {
    }
}
