package com.harinezumi_dev.cexploded.compat

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object ReflectionCache {
    private class Box<T>(val value: T)

    private val fields = ConcurrentHashMap<Class<*>, ConcurrentHashMap<String, Box<Field?>>>()
    private val methods = ConcurrentHashMap<Class<*>, ConcurrentHashMap<String, Box<Method?>>>()

    fun field(clazz: Class<*>, name: String): Field? =
        fields.getOrPut(clazz) { ConcurrentHashMap() }
            .getOrPut(name) { Box(findField(clazz, name)) }
            .value

    fun method(clazz: Class<*>, name: String): Method? =
        methods.getOrPut(clazz) { ConcurrentHashMap() }
            .getOrPut(name) { Box(findMethod(clazz, name)) }
            .value

    private fun findField(clazz: Class<*>, name: String): Field? =
        runCatching { clazz.getDeclaredField(name).apply { isAccessible = true } }.getOrNull()

    private fun findMethod(clazz: Class<*>, name: String): Method? =
        runCatching { clazz.getMethod(name).apply { isAccessible = true } }.getOrNull()
}
