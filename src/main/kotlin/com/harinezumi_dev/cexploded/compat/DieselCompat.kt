package com.harinezumi_dev.cexploded.compat

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.Capabilities

object DieselCompat {
    private const val MODID = "createdieselgenerators"

    fun isLoaded(): Boolean = ModList.get().isLoaded(MODID)

    fun isEngine(state: BlockState): Boolean {
        if (!isLoaded()) return false
        val key = BuiltInRegistries.BLOCK.getKey(state.block)
        if (key.namespace != MODID) return false
        val p = key.path
        return p == "diesel_engine" || p == "large_diesel_engine" || p == "huge_diesel_engine"
    }

    fun getExplosiveData(level: ServerLevel, pos: BlockPos): Pair<Int, Double>? {
        val handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null as Direction?) ?: run {
            val be = level.getBlockEntity(pos) ?: return null
            return try {
                val m = be.javaClass.getMethod("getTank")
                val tank = m.invoke(be) as? net.neoforged.neoforge.fluids.capability.templates.FluidTank ?: return null
                val stack = tank.fluid
                if (stack.isEmpty) return null
                val ef = com.harinezumi_dev.cexploded.explosion.ExplosiveFluids.factor(stack.fluid) ?: return null
                val amt = tank.fluidAmount
                if (amt <= 0) return null
                amt to ef
            } catch (_: Exception) { return null }
        }
        if (handler.tanks == 0) return null
        var total = 0
        var ef: Double? = null
        for (i in 0 until handler.tanks) {
            val s = handler.getFluidInTank(i)
            if (s.isEmpty) continue
            val f = com.harinezumi_dev.cexploded.explosion.ExplosiveFluids.factor(s.fluid) ?: continue
            total += s.amount
            if (ef == null) ef = f
        }
        if (total <= 0 || ef == null) return null
        return total to ef
    }

    fun getOrigin(level: ServerLevel, pos: BlockPos): BlockPos {
        val be = level.getBlockEntity(pos) ?: return pos
        return try {
            val f = be.javaClass.getDeclaredField("controller")
            f.isAccessible = true
            val c = f.get(be) as? BlockPos
            if (c != null) return c
            val f2 = be.javaClass.getDeclaredField("controllerPos")
            f2.isAccessible = true
            (f2.get(be) as? BlockPos) ?: pos
        } catch (_: Exception) {
            try {
                val m = be.javaClass.getMethod("getControllerBE")
                val ctrl = m.invoke(be) ?: return pos
                val cf = ctrl.javaClass.getDeclaredField("controller")
                cf.isAccessible = true
                (cf.get(ctrl) as? BlockPos) ?: pos
            } catch (_: Exception) { pos }
        }
    }

    fun removeEngine(level: ServerLevel, start: BlockPos) {
        val origin = getOrigin(level, start)
        val queue = java.util.ArrayDeque<BlockPos>()
        val visited = HashSet<BlockPos>()
        queue.add(start)
        visited.add(start)
        var count = 0
        while (queue.isNotEmpty() && count < 32) {
            val cur = queue.removeFirst()
            val curState = level.getBlockState(cur)
            if (!isEngine(curState)) continue
            val curOrigin = getOrigin(level, cur)
            if (curOrigin != origin) continue
            count++
            level.setBlock(cur, Blocks.AIR.defaultBlockState(), 3)
            for (dir in Direction.values()) {
                val next = cur.relative(dir)
                if (!visited.add(next)) continue
                if (visited.size > 32) break
                val ns = level.getBlockState(next)
                if (!isEngine(ns)) continue
                val no = getOrigin(level, next)
                if (no == origin) queue.add(next)
            }
        }
        if (count == 0) level.setBlock(start, Blocks.AIR.defaultBlockState(), 3)
    }
}
