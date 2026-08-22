package com.harinezumi_dev.cexploded.explosion

import kotlin.math.cbrt

object ExplosionMath {
    const val BASE_RADIUS = 4.0
    const val REFERENCE_BUCKETS = 8.0
    const val MAX_RADIUS = 24.0f

    fun compute(amountMb: Int, ef: Double): Float {
        if (amountMb <= 0) return 0f
        val buckets = amountMb / 1000.0
        val value = buckets * ef / REFERENCE_BUCKETS
        if (value <= 0.0) return 0f
        val power = BASE_RADIUS * cbrt(value)
        return power.toFloat().coerceIn(0f, MAX_RADIUS)
    }
}
