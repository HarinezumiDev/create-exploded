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
            val fluidStackMethod = ReflectionCache.method(clazz, "fluidStack")
            val amountMethod = ReflectionCache.method(clazz, "getFuelAmountMb")
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
        val f = ReflectionCache.field(be.javaClass, "controllerPos")
        if (f != null) {
            return try {
                f.get(be) as? BlockPos ?: pos
            } catch (_: Exception) { pos }
        }
        val m = ReflectionCache.method(be.javaClass, "getControllerBE") ?: return pos
        val ctrl = try {
            m.invoke(be)
        } catch (_: Exception) {
            return pos
        } ?: return pos
        val cf = ReflectionCache.field(ctrl.javaClass, "controllerPos") ?: return pos
        val c = try {
            cf.get(ctrl) as? BlockPos
        } catch (_: Exception) { return pos }
        if (c != null) return c
        val pf = ReflectionCache.field(ctrl.javaClass, "blockPos") ?: return pos
        return try {
            pf.get(ctrl) as? BlockPos ?: pos
        } catch (_: Exception) {
            pos
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
            val wField = ReflectionCache.field(be.javaClass, "width")
            if (wField != null) {
                try {
                    width = wField.getInt(be).coerceIn(1, 3)
                } catch (_: Exception) {}
            }
            val ctrlField = ReflectionCache.field(be.javaClass, "controllerPos")
            if (ctrlField != null) {
                try {
                    origin = ctrlField.get(be) as? BlockPos
                } catch (_: Exception) {}
            }
            val ctrlMethod = ReflectionCache.method(be.javaClass, "getControllerBE")
            if (ctrlMethod != null) {
                try {
                    val ctrl = ctrlMethod.invoke(be)
                    if (ctrl != null) {
                        val w2 = ReflectionCache.field(ctrl.javaClass, "width")
                        if (w2 != null) {
                            width = w2.getInt(ctrl).coerceIn(1, 3)
                        }
                        val c2 = ReflectionCache.field(ctrl.javaClass, "controllerPos")
                        if (c2 != null) {
                            origin = c2.get(ctrl) as? BlockPos
                        }
                        if (origin == null) {
                            val posField = ReflectionCache.field(ctrl.javaClass, "blockPos")
                            if (posField != null) {
                                origin = posField.get(ctrl) as? BlockPos
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
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
