package com.harinezumi_dev.cexploded.explosion

import java.util.ArrayDeque
import java.util.Collections
import java.util.IdentityHashMap
import java.util.WeakHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.event.level.ExplosionEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import com.harinezumi_dev.cexploded.compat.DieselCompat
import com.harinezumi_dev.cexploded.compat.PropulsionCompat

@EventBusSubscriber(modid = "create_exploded", bus = EventBusSubscriber.Bus.GAME)
object TankExplosionHandler {
    private val pending = Collections.synchronizedMap(WeakHashMap<ServerLevel, MutableList<Pair<BlockPos, Float>>>())

    @SubscribeEvent
    fun onDetonate(event: ExplosionEvent.Detonate) {
        val level = event.level as? ServerLevel ?: return
        if (level.isClientSide) return
        val affected = event.affectedBlocks
        if (affected.isEmpty()) return
        val snapshot = affected.toList()
        val visited = Collections.newSetFromMap(IdentityHashMap<IFluidHandler, Boolean>())
        val visitedPropulsion = HashSet<BlockPos>()
        val visitedDiesel = HashSet<BlockPos>()
        val targets = ArrayList<Pair<BlockPos, Float>>(4)
        val targetHandlers = ArrayList<IFluidHandler>(4)
        val propulsionTargets = ArrayList<Pair<BlockPos, Float>>(4)
        val dieselTargets = ArrayList<Pair<BlockPos, Float>>(4)
        for (pos in snapshot) {
            val state = level.getBlockState(pos)
            if (PropulsionCompat.isThruster(state)) {
                val origin = PropulsionCompat.getOrigin(level, pos)
                if (!visitedPropulsion.add(origin)) continue
                val data = PropulsionCompat.getExplosiveData(level, pos) ?: continue
                val power = ExplosionMath.compute(data.first, data.second)
                if (power < 0.5f) continue
                propulsionTargets.add(pos to power)
                continue
            }
            if (DieselCompat.isEngine(state)) {
                val origin = DieselCompat.getOrigin(level, pos)
                if (!visitedDiesel.add(origin)) continue
                val data = DieselCompat.getExplosiveData(level, pos) ?: continue
                val power = ExplosionMath.compute(data.first, data.second)
                if (power < 0.5f) continue
                dieselTargets.add(pos to power)
                continue
            }
            if (!isTank(state)) continue
            val handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null as Direction?) ?: continue
            if (!visited.add(handler)) continue
            if (handler.tanks == 0) continue
            var total = 0
            var ef: Double? = null
            for (i in 0 until handler.tanks) {
                val stack = handler.getFluidInTank(i)
                if (stack.isEmpty) continue
                val f = ExplosiveFluids.factor(stack.fluid) ?: continue
                total += stack.amount
                if (ef == null) ef = f
            }
            if (total <= 0 || ef == null) continue
            val power = ExplosionMath.compute(total, ef)
            if (power < 0.5f) continue
            targets.add(pos to power)
            targetHandlers.add(handler)
        }
        if (targets.isEmpty() && propulsionTargets.isEmpty() && dieselTargets.isEmpty()) return
        for ((pos, power) in propulsionTargets) {
            PropulsionCompat.removeThruster(level, pos)
            val list = pending.computeIfAbsent(level) { Collections.synchronizedList(mutableListOf()) }
            list.add(pos to power)
        }
        for ((pos, power) in dieselTargets) {
            DieselCompat.removeEngine(level, pos)
            val list = pending.computeIfAbsent(level) { Collections.synchronizedList(mutableListOf()) }
            list.add(pos to power)
        }
        for (idx in targets.indices) {
            val pos = targets[idx].first
            val power = targets[idx].second
            val handler = targetHandlers[idx]
            removeTank(level, pos, handler)
            val list = pending.computeIfAbsent(level) { Collections.synchronizedList(mutableListOf()) }
            list.add(pos to power)
        }
    }

    @SubscribeEvent
    fun onLevelTick(event: LevelTickEvent.Post) {
        val level = event.level as? ServerLevel ?: return
        val list = pending.remove(level) ?: return
        if (list.isEmpty()) return
        val snapshot = synchronized(list) { list.toList() }
        for ((pos, power) in snapshot) {
            if (!level.isLoaded(pos)) continue
            level.explode(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, power, true, Level.ExplosionInteraction.BLOCK)
        }
    }

    private fun isTank(state: net.minecraft.world.level.block.state.BlockState): Boolean {
        val id = BuiltInRegistries.BLOCK.getKey(state.block).path
        return id.contains("tank") || id.contains("vessel") || id.contains("cistern")
    }

    private fun removeTank(level: ServerLevel, start: BlockPos, handler: IFluidHandler) {
        val queue = ArrayDeque<BlockPos>()
        val visited = HashSet<BlockPos>()
        queue.add(start)
        visited.add(start)
        var count = 0
        while (queue.isNotEmpty() && count < 128) {
            val cur = queue.removeFirst()
            val curHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, cur, null as Direction?)
            if (curHandler !== handler) continue
            count++
            level.setBlock(cur, Blocks.AIR.defaultBlockState(), 3)
            for (dir in Direction.values()) {
                val next = cur.relative(dir)
                if (!visited.add(next)) continue
                if (visited.size > 128) break
                val nextHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, next, null as Direction?)
                if (nextHandler === handler) {
                    queue.add(next)
                }
            }
        }
        if (count == 1) {
            val singleHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, start, null as Direction?)
            if (singleHandler != null && singleHandler === handler) {
                level.setBlock(start, Blocks.AIR.defaultBlockState(), 3)
            }
        }
    }
}
