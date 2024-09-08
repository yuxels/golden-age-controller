package cat.kittens.mods.controller.screen;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import cat.kittens.mods.controller.ui.ScrollableScreen;
import cat.kittens.mods.controller.ui.ScrollableScreenComponent;
import cat.kittens.mods.controller.ui.ScrollableScreenComponentColumn;
import cat.kittens.mods.controller.ui.ScrollableScreenLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ControllerConfigScreen {
    private static String id(IGamepadDevice<IGamepadDeviceId> device) {
        return "[" + device.id().location().vendor() + ":" + device.id().location().product() + "] " +
                device.id().name();
    }
    private static void controllerComponent(List<ScrollableScreenComponent> list) {
        list.add(ScrollableScreenComponent.text()
                .message("Select a controller to be used")
                .backgroundColor(-1));
        list.add(ScrollableScreenComponent.dropdown()
                .selectedValue(
                        ControllerSupport.support().manager().currentController().map(ControllerConfigScreen::id)
                                .orElse("No controller found.")
                )
                .options(ControllerSupport.support().manager().findAllGamepads()
                        .stream().map(ControllerConfigScreen::id)
                        .toList())
                .onSelect((ctx, option) -> {
                    for (var gamepad : ControllerSupport.support().manager().findAllGamepads()) {
                        if (option.equals(id(gamepad))) {
                            ControllerSupport.support().manager().setCurrentController(gamepad);
                            break;
                        }
                    }
                })
        );
        list.add(ScrollableScreenComponent.gap());
    }

    private static List<ScrollableScreenComponent> right() {
        var list = new ArrayList<ScrollableScreenComponent>();
        controllerComponent(list);
        return list;
    }

    public static ScrollableScreen screen() {
        var layout = ScrollableScreenLayout.create()
                .withXPadding(8)
                .withYPadding(8);
        Supplier<ScrollableScreenComponentColumn> left = () ->
                new ScrollableScreenComponentColumn(
                        List.of(
                                ScrollableScreenComponent.button()
                                        .message("Back to Main Menu")
                                        .fitDimensions(20)
                                        .onClick((ctx, mX, mY) -> {
                                            ctx.minecraft().currentScreen = ctx.previousScreen();
                                        }),
                                ScrollableScreenComponent.text()
                                        .message("Settings")
                        ),
                        List.of(),
                        layout,
                        8,
                        true
                );
        Supplier<ScrollableScreenComponentColumn> right = () -> new ScrollableScreenComponentColumn(
                right(),
                List.of(),
                layout,
                8,
                true
        );
        return new ScrollableScreen(layout, "Controller Support", left, right);
    }
}
