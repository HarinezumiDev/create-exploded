package com.harinezumi_dev.cexploded.explosion

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

object ExplosiveFluids {
    private fun fluidTag(namespace: String, path: String): TagKey<Fluid> =
        TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, path))

    private val specificTags: List<Pair<TagKey<Fluid>, Double>> = listOf(
        fluidTag("c", "gasoline") to 1.0,
        fluidTag("c", "diesel") to 1.15,
        fluidTag("c", "crude_oil") to 1.1,
        fluidTag("c", "crudeoil") to 1.1,
        fluidTag("c", "biodiesel") to 0.9,
        fluidTag("c", "plantoil") to 0.6,
        fluidTag("c", "plant_oil") to 0.6,
        fluidTag("c", "ethanol") to 0.75,
        fluidTag("c", "naphtha") to 1.05,
        fluidTag("c", "kerosene") to 1.0,
        fluidTag("c", "heavy_oil") to 1.0,
        fluidTag("c", "lpg") to 1.1,
        fluidTag("c", "lubrication_oil") to 0.6,
        fluidTag("c", "creosote") to 0.7,
        fluidTag("c", "furnace_gas") to 0.5
    )

    private val genericTags: List<Pair<TagKey<Fluid>, Double>> = listOf(
        fluidTag("c", "fuel") to 1.0,
        fluidTag("tfmg", "flammable") to 1.0,
        fluidTag("tfmg", "fuel") to 1.0
    )

    fun factor(stack: FluidStack): Double? {
        if (stack.isEmpty) return null
        for ((tag, value) in specificTags) {
            if (stack.`is`(tag)) return value
        }
        val path = BuiltInRegistries.FLUID.getKey(stack.fluid).path
        if (path.contains("hydrogen")) return 1.25
        if (path.contains("napalm")) return 1.2
        if (path.contains("propane")) return 1.1
        if (path.contains("butane")) return 1.1
        for ((tag, value) in genericTags) {
            if (stack.`is`(tag)) return value
        }
        if (path.contains("biodiesel")) return 0.9
        if (path.contains("crude_oil") || path.contains("crudeoil")) return 1.1
        if (path.contains("plant_oil") || path.contains("plantoil")) return 0.6
        if (path.contains("gasoline")) return 1.0
        if (path.contains("ethanol")) return 0.75
        if (path.contains("diesel")) return 1.15
        if (path.contains("naphtha")) return 1.05
        if (path.contains("kerosene")) return 1.0
        if (path.contains("heavy_oil") || path.contains("heavyoil")) return 1.0
        if (path.contains("lpg")) return 1.1
        if (path.contains("lubrication_oil")) return 0.6
        if (path.contains("creosote")) return 0.7
        if (path.contains("furnace_gas") || path.contains("furnacegas")) return 0.5
        return null
    }
}
