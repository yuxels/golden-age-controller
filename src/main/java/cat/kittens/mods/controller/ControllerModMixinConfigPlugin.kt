package cat.kittens.mods.controller

import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

public class ControllerModMixinConfigPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String): Unit = Unit

    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean =
        mixinClassName != "ModMenuMixin" || FabricLoader.getInstance().isModLoaded("modmenu")

    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>): Unit = Unit

    override fun getMixins(): List<String>? = null

    override fun preApply(
        targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo
    ): Unit = Unit

    override fun postApply(
        targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo
    ): Unit = Unit
}