package cat.kittens.mods.controller.config;

public record ControllerModConfig(float rightStickXSensitivity, float rightStickYSensitivity) {
    public static final ControllerModConfig DEFAULT = new ControllerModConfig(10.f, 10.f);
}
