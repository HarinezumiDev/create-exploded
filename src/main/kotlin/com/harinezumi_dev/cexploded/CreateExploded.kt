package com.harinezumi_dev.cexploded

import com.mojang.logging.LogUtils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(CreateExploded.MODID)
object CreateExploded {
    const val MODID = "create_exploded"

    @JvmField
    val LOGGER: Logger = LogUtils.getLogger()

    init {
        MOD_BUS.addListener(::commonSetup)
        NeoForge.EVENT_BUS.register(this)
        ModList.get().getModContainerById(MODID).ifPresent { container ->
            container.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
        }
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("HELLO FROM COMMON SETUP")
        if (Config.LOG_DIRT_BLOCK.get()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.get())
        Config.ITEM_STRINGS.get().forEach { item -> LOGGER.info("ITEM >> {}", item) }
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        LOGGER.info("HELLO from server starting")
    }
}
