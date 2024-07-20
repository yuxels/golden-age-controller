package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.lib.IGamepadDevice;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalLong;

public interface IControllerMapping {
    /**
     * Context available for key mappings.
     * @param minecraft
     * @param durationMillis duration of the button press.
     * @param value The axis value.
     * @param hold Whether the action is being held for more than one cycle.
     */
    record Context(Minecraft minecraft, OptionalLong durationMillis, OptionalDouble value, boolean hold) {
        public boolean release() {
            return durationMillis.isEmpty();
        }
    }

    interface Action {
        /**
         * Button to press duration mapping. Required for acknowledging the necessary
         * inputs for performing this action.
         */
        default Map<IGamepadDevice.Input.Button, Long> buttonChord() {
            return Collections.emptyMap();
        }

        /**
         * Axis to minimum value. Required for acknowledging the necessary inputs
         * for performing this action.
         */
        default Map<IGamepadDevice.Input.Axis, Float> axisChord() {
            return Collections.emptyMap();
        }

        /**
         * Whether this input will be ticked on main loop instead of input tick loop. This is useful
         * for the fluidity of certain activities such as aiming and tasks that would work off-gameplay (such
         * as menus), due to be avoiding the extra overhead of ticking logic.
         * If set to false, this action will be run alongside Minecraft default input handling mechanisms,
         * which should be enough for most tasks.
         */
        default boolean offTick() {
            return false;
        }

        IReversibleActionExecutor executor();
    }

    interface ActionId {
        @Override
        String toString();

        @Override
        boolean equals(Object other);

        @Override
        int hashCode();

        static ActionId fromString(String value) {
            return new ActionId() {
                @Override
                public String toString() {
                    return value;
                }

                @Override
                public boolean equals(Object obj) {
                    return value.equals(obj);
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            };
        }
    }

    Map<ActionId, Action> getActions();
}
