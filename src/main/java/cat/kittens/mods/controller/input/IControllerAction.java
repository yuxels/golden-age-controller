package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.lib.IGamepadDevice;

import java.util.Collections;
import java.util.Map;

public interface IControllerAction {
    /**
     * Button to press duration mapping. Required for acknowledging the necessary
     * inputs for performing this action.
     */
    default Map<IGamepadDevice.Input.Button, Integer> buttonChord() {
        return Collections.emptyMap();
    }

    /**
     * Axis to minimum value. Required for acknowledging the necessary inputs
     * for performing this action.
     */
    default Map<IGamepadDevice.Input.Axis, Float> axisChord() {
        return Collections.emptyMap();
    }

    IReversibleActionExecutor executor();
}
