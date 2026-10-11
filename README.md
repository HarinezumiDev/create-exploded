# Create: Exploded

**Your fuel tanks are a liability now.**

Create: Exploded adds volatile, explosive fluids to the [Create](https://modrinth.com/mod/create) ecosystem. Fuels and crude liquids stored in Fluid Tanks and Fluid Vessels are no longer inert — expose them to a nearby explosion, and they'll detonate with force proportional to how much flammable liquid they're holding.

No more parking a warehouse of gasoline next to your TNT cannon and walking away unscathed.

---

## How It Works

- **Chain reactions, not magic.** Tanks don't spontaneously combust — any explosion (TNT, creepers, other mods, or another exploded tank) that reaches a tank containing a flammable fluid will set it off in turn. Build your fuel depot next to a minefield at your own risk.
- **Explosion power scales with fluid volume.** A near-empty tank gives a modest pop. A fully stacked multiblock tank brimming with crude oil is a crater. The mod reads the *actual* fluid amount stored (not the tank's total capacity), so partially filled tanks are proportionally safer.
- **Not all fluids are equally dangerous.** Each supported liquid has its own explosiveness rating based on real-world combustion energy and flammability — see the ratings table below.
- **Multiblock aware.** Create's tanks merge into a single shared reservoir when connected — Create: Exploded treats the whole connected structure as one volume when calculating blast force, so building a bigger tank farm means building a bigger bomb.

---

## Supported Fluids & Compatibility

| Fluid | Rating |
|---|---|
| Hydrogen | 1.25 |
| Napalm | 1.2 |
| Diesel | 1.15 |
| Crude Oil | 1.1 |
| Ethylene | 1.1 |
| Gas Oil | 1.1 |
| LPG / Propane / Butane | 1.1 |
| Oil / Desalted Oil | 1.1 |
| Naphtha / Naphta | 1.05 |
| Gasoline | 1.0 |
| Kerosene | 1.0 |
| Heavy Oil | 1.0 |
| Fuel Oil | 1.0 |
| Volatile Gas | 1.0 |
| Other fuel (`c:fuel`, `tfmg:flammable`, `tfmg:fuel`) | 1.0 |
| Biodiesel | 0.9 |
| Hydrogen Sulfide | 0.9 |
| Ethanol | 0.75 |
| Plant Oil | 0.6 |
| Furnace Gas | 0.5 |

| Source | Provides | Status |
|---|---|---|
| **[Create](https://modrinth.com/mod/create)** | Tanks | Required |
| **[Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge)** | — | Required |
| **[Create: Diesel Generators](https://modrinth.com/mod/create-diesel-generators)** | Fuels & Engines | Tested |
| **[Create: The Factory Must Grow](https://modrinth.com/mod/create-tfmg)** | Fuels | Tested |
| **[Create: Petrochem](https://www.curseforge.com/minecraft/mc-mods/create-petrochem)** | Fuels | Tested |
| **[Create: Connected](https://modrinth.com/mod/create-connected)** | Vessels | Tested |
| **[Create Propulsion: Simulated](https://modrinth.com/mod/create-propulsion-simulated)** | Thrusters | Tested |

**Required** — the mod won't load without these.

**Tested** — verified to work and covered by testing, but the mod loads fine without them.

Fluids are matched by fluid tags first (`c:*`, `tfmg:flammable`, `tfmg:fuel`), with a registry-name fallback for untagged or custom fluids. Tanks are picked up generically through the fluid capability and block-name matching (`tank`, `vessel`, `cistern`), while diesel engines and thrusters are handled explicitly with their own multiblock-aware explosion logic. Beyond the mods listed above, Create: Exploded will generally work alongside other Create addons that store fluids — treat unlisted mods as best-effort compatibility rather than a supported feature.

---

## Why Install This?

- Adds real stakes to fuel logistics in industrial Create builds
- Makes tank placement and blast shielding an actual design consideration
- Rewards contraptions that separate storage from combat/mining operations
- Plays nicely with modpacks built around Create's diesel/oil processing chains

---

## Requirements

- **Loader:** NeoForge
- **Required:** Create, Kotlin for Forge
- **Optional:** Create: Diesel Generators, Create: The Factory Must Grow, Create: Connected, Create Propulsion: Simulated

---

## For Mod Developers

Custom damage that bypasses vanilla explosions? See [API.md](API.md) — one method call sets fuel tanks off.

---

## Feedback & Issues

Found a fluid that should (or shouldn't) go boom? Balance feels off? Leave your feedback on my Discord server — this mod is actively tuned based on player feedback.
[Discord!](https://discord.gg/VhuDn6A7WA)