package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.lib.IGamepadDevice;

import java.util.HashMap;
import java.util.Map;

public class DefaultControllerMapping implements IControllerMapping {
    private static IControllerMapping INSTANCE;
    private static final Map<ActionId, Action> ACTIONS;

    public static IControllerMapping mapping() {
        return INSTANCE == null ? INSTANCE = new DefaultControllerMapping() : INSTANCE;
    }

    static {
        ACTIONS = new HashMap<>();
        ACTIONS.put(ActionIds.BREAK, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.RIGHT_TRIGGER, 0.75f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.BREAK;
            }
        });
        ACTIONS.put(ActionIds.INTERACT, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.LEFT_TRIGGER, 0.75f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.INTERACT;
            }
        });
        ACTIONS.put(ActionIds.PICK_BLOCK, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.DPAD_LEFT, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.PICK_BLOCK;
            }
        });
        ACTIONS.put(ActionIds.JUMP, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.SOUTH, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.JUMP;
            }
        });
        ACTIONS.put(ActionIds.OPEN_INVENTORY, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.NORTH, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.OPEN_INVENTORY;
            }
        });
        ACTIONS.put(ActionIds.DROP_ITEM, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.EAST, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.DROP_ITEM;
            }
        });
        ACTIONS.put(ActionIds.SNEAK, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.RIGHT_STICK, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.SNEAK;
            }
        });
        ACTIONS.put(ActionIds.OPEN_CHAT, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Button, Long> buttonChord() {
                return Map.of(IGamepadDevice.Input.Button.DPAD_RIGHT, 0L);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.OPEN_CHAT;
            }
        });
        ACTIONS.put(ActionIds.WALK_FORWARD, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.LEFT_STICK_UP, 0f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.WALK_FORWARD;
            }
        });
        ACTIONS.put(ActionIds.WALK_BACKWARD, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.LEFT_STICK_DOWN, 0f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.WALK_BACKWARD;
            }
        });
        ACTIONS.put(ActionIds.WALK_LEFTWARD, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.LEFT_STICK_LEFT, 0f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.WALK_LEFTWARD;
            }
        });
        ACTIONS.put(ActionIds.WALK_RIGHTWARD, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.LEFT_STICK_RIGHT, 0f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.WALK_RIGHTWARD;
            }
        });
        ACTIONS.put(ActionIds.AIM_UP, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.RIGHT_STICK_UP, 0.f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.AIM_UP;
            }
            @Override
            public boolean offTick() {
                return true;
            }
        });
        ACTIONS.put(ActionIds.AIM_DOWN, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.RIGHT_STICK_DOWN, 0.f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.AIM_DOWN;
            }
            @Override
            public boolean offTick() {
                return true;
            }
        });
        ACTIONS.put(ActionIds.AIM_LEFT, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.RIGHT_STICK_LEFT, 0.f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.AIM_LEFT;
            }
            @Override
            public boolean offTick() {
                return true;
            }
        });
        ACTIONS.put(ActionIds.AIM_RIGHT, new Action() {
            @Override
            public Map<IGamepadDevice.Input.Axis, Float> axisChord() {
                return Map.of(IGamepadDevice.Input.Axis.RIGHT_STICK_RIGHT, 0.f);
            }
            @Override
            public IReversibleActionExecutor executor() {
                return ActionExecutors.AIM_RIGHT;
            }
            @Override
            public boolean offTick() {
                return true;
            }
        });
    }

    @Override
    public Map<ActionId, Action> getActions() {
        return ACTIONS;
    }
}
