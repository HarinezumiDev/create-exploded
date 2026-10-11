# Create: Exploded — API for mod developers

Your mod deals blast-like damage without vanilla explosions (custom physics, missiles, collisions)? Two method calls set fuel tanks off like TNT would.

## The methods

```java
CreateExplodedAPI.triggerTankAt(ServerLevel level, BlockPos pos) // returns boolean
CreateExplodedAPI.triggerTanksInRadius(ServerLevel level, BlockPos center, double radius) // returns int
```

- Call them on the **server thread** when your blast-like event hits.
- `triggerTankAt`: exact block you already know (e.g. collision point). Returns `true` if a fuel tank was queued to explode next tick.
- `triggerTanksInRadius`: blast center + radius (clamped to 64). Create: Exploded finds the tanks itself. Returns how many were queued.
- Same rules as a normal chain reaction in both cases: fluid check, 0.5 power floor. Safe for any block: misses return `false` / `0`, nothing happens.

## Java

```java
if (ModList.get().isLoaded(CreateExplodedAPI.MODID)) {
    CreateExplodedAPI.triggerTankAt(level, pos);
}
```

## Kotlin

```kotlin
if (ModList.get().isLoaded(CreateExplodedAPI.MODID)) {
    CreateExplodedAPI.triggerTankAt(level, pos)
}
```

## Without adding the jar (reflection)

```java
if (ModList.get().isLoaded("create_exploded")) {
    try {
        Class<?> api = Class.forName("com.harinezumi_dev.cexploded.api.CreateExplodedAPI");
        Object ok = api.getMethod("triggerTankAt", ServerLevel.class, BlockPos.class).invoke(null, level, pos);
    } catch (Exception ignored) {}
}
```

Add `create_exploded` as an optional dependency in your `neoforge.mods.toml` so load order is sane.
