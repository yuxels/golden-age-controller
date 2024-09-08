package cat.kittens.mods.controller.config

public data class ControllerModConfig(var rightStickXSensitivity: Float, var rightStickYSensitivity: Float) {
    public companion object {
        public const val SENS_MULTIPLIER: Float = .000075f

        public val DEFAULT: ControllerModConfig = ControllerModConfig(
            10 * SENS_MULTIPLIER, 10 * SENS_MULTIPLIER
        )
    }
}
