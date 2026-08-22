package com.harinezumi_dev.cexploded.explosion

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.material.Fluid

object ExplosiveFluids {
    private val factors = mapOf(
        "gasoline" to 1.0,
        "diesel" to 1.15,
        "crude_oil" to 1.1,
        "biodiesel" to 0.9,
        "plant_oil" to 0.6,
        "ethanol" to 0.75
    )

    fun factor(fluid: Fluid): Double? {
        val key = BuiltInRegistries.FLUID.getKey(fluid)
        val path = key.path
        if (path.contains("biodiesel")) return factors["biodiesel"]
        if (path.contains("crude_oil")) return factors["crude_oil"]
        if (path.contains("plant_oil")) return factors["plant_oil"]
        if (path.contains("gasoline")) return factors["gasoline"]
        if (path.contains("ethanol")) return factors["ethanol"]
        if (path.contains("diesel")) return factors["diesel"]
        return null
    }
}
