package cat.kittens.mods.controller.lib

public typealias GenericGamepadManager = GamepadManager<Exception, GamepadDevice<IGamepadDeviceId>, IGamepadDeviceId>

public interface GamepadManager<E : Throwable, C : GamepadDevice<I>, I : IGamepadDeviceId> {
    public var currentController: C?

    public val identifier: String

    public val isInitialized: Boolean

    /**
     * Initializes all the libraries required to interact with Input APIs.
     * @return An error resulting from the operation, null if none.
     */
    public fun tryLibInit(): Result<Unit>

    /**
     * Rediscovers all connected controllers.
     */
    public fun discoverAll(): Result<Unit>

    /**
     * @param id The unique identification of the target device.
     * @return Whether the device that goes by the given id is a gamepad device.
     */
    public fun hasGamepad(id: I): Result<Boolean>

    /**
     * @param id The unique identification of the target device.
     * @return Whether the device that goes by the given id is a gamepad device.
     */
    public fun hasGamepad(id: String): Result<Boolean>

    /**
     * @param id The unique identification of the target device.
     * @return Empty optional if not found, gamepad entity otherwise.
     */
    public fun findGamepadOrNull(id: String): Result<C?>

    /**
     * @param id The unique identification of the target device.
     * @return Empty optional if not found, gamepad entity otherwise.
     */
    public fun findGamepadOrNull(id: I): Result<C?>

    /**
     * Get all available gamepads.
     */
    public fun findAllGamepads(): Result<List<C>>

    /**
     * Runs the required logic on this tick.
     */
    public fun tick()
}
