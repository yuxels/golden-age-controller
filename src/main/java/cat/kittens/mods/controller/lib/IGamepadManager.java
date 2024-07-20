package cat.kittens.mods.controller.lib;

import java.util.List;
import java.util.Optional;

public interface IGamepadManager<E extends Throwable, C extends IGamepadDevice<I>, I extends IGamepadDeviceId> {
    Optional<C> currentController();

    void setCurrentController(C controller);

    String identifier();

    boolean isInitialized();

    /**
     * Initializes all the libraries required to interact with Input APIs.
     * @return An error resulting from the operation, null if none.
     */
    Optional<E> tryLibInit();

    /**
     * Rediscovers all connected controllers.
     */
    void discoverAll();

    /**
     * @param id The unique identification of the target device.
     * @return Whether the device that goes by the given id is a gamepad device.
     */
    boolean isGamepad(I id);

    /**
     * @param id The unique identification of the target device.
     * @return Whether the device that goes by the given id is a gamepad device.
     */
    boolean isGamepad(String id);

    /**
     * @param id The unique identification of the target device.
     * @return Empty optional if not found, gamepad entity otherwise.
     */
    Optional<C> findGamepad(String id);

    /**
     * @param id The unique identification of the target device.
     * @return Empty optional if not found, gamepad entity otherwise.
     */
    Optional<C> findGamepad(I id);

    /**
     * Get all available gamepads.
     */
    List<C> findAllGamepads();

    /**
     * Runs the required logic on this tick.
     */
    void tick();
}
