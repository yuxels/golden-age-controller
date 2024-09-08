package cat.kittens.mods.controller.config;

public record ControllerModConfig(float rightStickXSensitivity, float rightStickYSensitivity) {
    public static final float SENS_MULTIPLIER = .00075f;

    public static final ControllerModConfig DEFAULT = new ControllerModConfig(
            10 * SENS_MULTIPLIER, 10 * SENS_MULTIPLIER);
}
