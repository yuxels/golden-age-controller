package cat.kittens.mods.controller.lib;

import java.time.Duration;

/**
 * Controller input device.
 */
public interface IGamepadDevice<I extends IGamepadDeviceId> {
    I id();

    Input input();

    Type gamepadType();

    enum Type {
        STANDARD,
        XBOX_360,
        XBOX_ONE,
        PS3,
        PS4,
        PS5,
        NS_PRO,
        NS_JOYCON_LEFT,
        NS_JOYCON_RIGHT,
        NS_JOYCON_PAIR,
        UNKNOWN
    }

    interface Input {
        enum Button {
            SOUTH,           /* Bottom face button (e.g. Xbox A button) */
            EAST,            /* Right face button (e.g. Xbox B button) */
            WEST,            /* Left face button (e.g. Xbox X button) */
            NORTH,           /* Top face button (e.g. Xbox Y button) */
            BACK,
            GUIDE,
            START,
            LEFT_STICK,
            RIGHT_STICK,
            LEFT_SHOULDER,
            RIGHT_SHOULDER,
            DPAD_UP,
            DPAD_DOWN,
            DPAD_LEFT,
            DPAD_RIGHT,
            MISC1,           /* Additional button (e.g. Xbox Series X share button, PS5 microphone button, Nintendo Switch Pro capture button, Amazon Luna microphone button, Google Stadia capture button) */
            RIGHT_PADDLE1,   /* Upper or primary paddle, under your right hand (e.g. Xbox Elite paddle P1) */
            LEFT_PADDLE1,    /* Upper or primary paddle, under your left hand (e.g. Xbox Elite paddle P3) */
            RIGHT_PADDLE2,   /* Lower or secondary paddle, under your right hand (e.g. Xbox Elite paddle P2) */
            LEFT_PADDLE2,    /* Lower or secondary paddle, under your left hand (e.g. Xbox Elite paddle P4) */
            TOUCHPAD,        /* PS4/PS5 touchpad button */
            MISC2,           /* Additional button */
            MISC3,           /* Additional button */
            MISC4,           /* Additional button */
            MISC5,           /* Additional button */
            MISC6,           /* Additional button */
        }

        enum Axis {
            LEFT_STICK_UP,
            LEFT_STICK_LEFT,
            LEFT_STICK_DOWN,
            LEFT_STICK_RIGHT,
            RIGHT_STICK_UP,
            RIGHT_STICK_LEFT,
            RIGHT_STICK_DOWN,
            RIGHT_STICK_RIGHT,
            LEFT_TRIGGER,
            RIGHT_TRIGGER,
        }

        void clear();

        DoubleStateOutput<Boolean> getPressState(Button button);

        void setButtonState(Button button, boolean pressed);

        float getDeadZone(Axis axis);

        void setDeadZone(Axis axis, float value);

        DoubleStateOutput<Float> getAxisValue(Axis axis);

        void setAxisValue(Axis axis, float value);

        void rumble(short lowFreq, short highFreq, Duration duration);
    }
}
