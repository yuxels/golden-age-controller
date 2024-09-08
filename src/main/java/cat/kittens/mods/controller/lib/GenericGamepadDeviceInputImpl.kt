package cat.kittens.mods.controller.lib;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GenericGamepadDeviceInputImpl implements IGamepadDevice.Input {
    protected Map<Button, DoubleStateOutput<Boolean>> faces;
    protected Map<IGamepadDevice.Input.Axis, DoubleStateOutput<Float>> axes;
    protected Map<IGamepadDevice.Input.Axis, Float> deadZones;

    public GenericGamepadDeviceInputImpl() {
        this.faces = new HashMap<>();
        this.axes = new HashMap<>();
        this.deadZones = new HashMap<>();
    }

    @Override
    public DoubleStateOutput<Boolean> getPressState(Button button) {
        return faces.computeIfAbsent(button, (btn) -> new DoubleStateOutput<>());
    }

    @Override
    public void setButtonState(Button button, boolean pressed) {
        getPressState(button).set(pressed);
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
    public DoubleStateOutput<Float> getAxisValue(Axis axis) {
        return axes.computeIfAbsent(axis, (ax) -> new DoubleStateOutput<>());
    }

    @Override
    public void setAxisValue(Axis axis, float value) {
        getAxisValue(axis).set(value > getDeadZone(axis) ? value : 0);
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
