package com.harinezumi_dev.cexploded

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod(value = CreateExploded.MODID, dist = [Dist.CLIENT])
@EventBusSubscriber(modid = CreateExploded.MODID, value = [Dist.CLIENT])
object CreateExplodedClient {
    init {
        ModList.get().getModContainerById(CreateExploded.MODID).ifPresent { container ->
            container.registerExtensionPoint(
                IConfigScreenFactory::class.java,
                IConfigScreenFactory { modContainer, parent -> ConfigurationScreen(modContainer, parent) }
            )
        }
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        CreateExploded.LOGGER.info("HELLO FROM CLIENT SETUP")
        CreateExploded.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
    }
}
