package com.harinezumi_dev.cexploded.api

import com.harinezumi_dev.cexploded.explosion.TankExplosionHandler
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object CreateExplodedAPI {
    const val MODID = "create_exploded"

    @JvmStatic
    fun triggerTankAt(level: ServerLevel, pos: BlockPos): Boolean =
        TankExplosionHandler.triggerTank(level, pos)

    @JvmStatic
    fun triggerTanksInRadius(level: ServerLevel, center: BlockPos, radius: Double): Int =
        TankExplosionHandler.triggerTanksInRadius(level, center, radius)
}
