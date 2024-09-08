@file:JvmName("ControllerConfigScreen")

package cat.kittens.mods.controller.screen

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.config.ControllerModConfig
import cat.kittens.mods.controller.dsl.scrollableScreen
import cat.kittens.mods.controller.lib.GenericGamepadDeviceType
import cat.kittens.mods.controller.ui.ScrollableScreen

public val GenericGamepadDeviceType.uiId: String
    get() = "[" + id.location.vendor + ":" + id.location.product + "] " + id.name

@JvmName("screen")
public fun controllerConfigScreen(): ScrollableScreen = scrollableScreen("Controller Support") {
    layout {
        x = 8
        y = 8
    }
    left {
        componentLayout {
            x = 8
            y = 8
        }
        top + button {
            message = "Back to Main Menu"
            fitDimensions(20)
            onClick = { ctx, _, _ ->
                ctx.minecraft.currentScreen = ctx.renderContext.previousScreen
            }
        }
        top + text("Settings")
    }
    right {
        componentLayout {
            x = 8
            y = 8
        }
        top + text("Select a controller to be used")
        top + dropdown {
            selectedValue = ControllerSupport.manager.currentController?.uiId ?: "No controller found"
            ControllerSupport.manager.findAllGamepads().getOrNull()?.forEach {
                options.add(it.uiId)
            }
            onSelect = { _, option ->
                val gamepad = ControllerSupport.manager.findAllGamepads().getOrNull()?.firstOrNull {
                    it.uiId == option
                }
                if (gamepad != null)
                    ControllerSupport.manager.currentController = gamepad
            }
        }
        top + slider {
            min = 5
            max = 50
            current = (ControllerSupport.config.rightStickXSensitivity / ControllerModConfig.SENS_MULTIPLIER).toInt()
            message = "Horizontal sensitivity"
            onChange = { ctx, _, curr ->
                ControllerSupport.config.rightStickXSensitivity = curr * ControllerModConfig.SENS_MULTIPLIER
            }
        }
        top + slider {
            min = 5
            max = 50
            current = (ControllerSupport.config.rightStickYSensitivity / ControllerModConfig.SENS_MULTIPLIER).toInt()
            message = "Vertical sensitivity"
            onChange = { ctx, _, curr ->
                ControllerSupport.config.rightStickYSensitivity = curr * ControllerModConfig.SENS_MULTIPLIER
            }
        }
    }
}.build()
