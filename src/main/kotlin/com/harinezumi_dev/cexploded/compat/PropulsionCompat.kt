package com.harinezumi_dev.cexploded.compat

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack

object PropulsionCompat {
    private const val MODID = "createpropulsion"

    fun isLoaded(): Boolean = ModList.get().isLoaded(MODID)

    fun isThruster(state: BlockState): Boolean {
        if (!isLoaded()) return false
        val key = BuiltInRegistries.BLOCK.getKey(state.block)
        if (key.namespace != MODID) return false
        val path = key.path
        return path == "thruster" || path == "liquid_vector_thruster"
    }

    fun getExplosiveData(level: ServerLevel, pos: BlockPos): Pair<Int, Double>? {
        val be = level.getBlockEntity(pos) ?: return null
        return try {
            val clazz = be.javaClass
            val fluidStackMethod = try { clazz.getMethod("fluidStack") } catch (_: NoSuchMethodException) { null }
            val amountMethod = try { clazz.getMethod("getFuelAmountMb") } catch (_: NoSuchMethodException) { null }
            if (fluidStackMethod != null && amountMethod != null) {
                val stack = fluidStackMethod.invoke(be) as? FluidStack ?: return null
                if (stack.isEmpty) return null
                val ef = com.harinezumi_dev.cexploded.explosion.ExplosiveFluids.factor(stack) ?: return null
                val amount = amountMethod.invoke(be) as? Int ?: stack.amount
                if (amount <= 0) return null
                amount to ef
            } else {
                val handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null as Direction?) ?: return null
                if (handler.tanks == 0) return null
                var total = 0
                var ef: Double? = null
                for (i in 0 until handler.tanks) {
                    val s = handler.getFluidInTank(i)
                    if (s.isEmpty) continue
                    val f = com.harinezumi_dev.cexploded.explosion.ExplosiveFluids.factor(s) ?: continue
                    total += s.amount
                    if (ef == null) ef = f
                }
                if (total <= 0 || ef == null) return null
                total to ef
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getOrigin(level: ServerLevel, pos: BlockPos): BlockPos {
        val be = level.getBlockEntity(pos) ?: return pos
        return try {
            val f = be.javaClass.getDeclaredField("controllerPos")
            f.isAccessible = true
            (f.get(be) as? BlockPos) ?: pos
        } catch (_: Exception) {
            try {
                val m = be.javaClass.getMethod("getControllerBE")
                val ctrl = m.invoke(be) ?: return pos
                val cf = ctrl.javaClass.getDeclaredField("controllerPos")
                cf.isAccessible = true
                (cf.get(ctrl) as? BlockPos) ?: run {
                    val pf = ctrl.javaClass.getDeclaredField("blockPos")
                    pf.isAccessible = true
                    pf.get(ctrl) as? BlockPos ?: pos
                }
            } catch (_: Exception) {
                pos
            }
        }
    }

    fun removeThruster(level: ServerLevel, start: BlockPos) {
        val startState = level.getBlockState(start)
        if (!isThruster(startState)) {
            level.setBlock(start, Blocks.AIR.defaultBlockState(), 3)
            return
        }
        val be = level.getBlockEntity(start)
        var width = 1
        var origin: BlockPos? = null
        if (be != null) {
            try {
                val wField = be.javaClass.getDeclaredField("width")
                wField.isAccessible = true
                width = wField.getInt(be).coerceIn(1, 3)
            } catch (_: Exception) {}
            try {
                val ctrlField = be.javaClass.getDeclaredField("controllerPos")
                ctrlField.isAccessible = true
                origin = ctrlField.get(be) as? BlockPos
            } catch (_: Exception) {}
            try {
                val ctrlMethod = be.javaClass.getMethod("getControllerBE")
                val ctrl = ctrlMethod.invoke(be)
                if (ctrl != null) {
                    val w2 = ctrl.javaClass.getDeclaredField("width")
                    w2.isAccessible = true
                    width = w2.getInt(ctrl).coerceIn(1, 3)
                    val c2 = ctrl.javaClass.getDeclaredField("controllerPos")
                    c2.isAccessible = true
                    origin = c2.get(ctrl) as? BlockPos
                    if (origin == null) {
                        val posField = ctrl.javaClass.getDeclaredField("blockPos")
                        posField.isAccessible = true
                        origin = posField.get(ctrl) as? BlockPos
                    }
                }
            } catch (_: Exception) {}
        }
        if (origin == null) origin = start
        if (width <= 1) {
            level.setBlock(start, Blocks.AIR.defaultBlockState(), 3)
            return
        }
        for (dx in 0 until width) {
            for (dy in 0 until width) {
                for (dz in 0 until width) {
                    val p = origin.offset(dx, dy, dz)
                    val s = level.getBlockState(p)
                    if (isThruster(s)) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3)
                    }
                }
            }
        }
    }
}
