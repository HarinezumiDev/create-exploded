# Create: Exploded

**Your fuel tanks are a liability now.**

Create: Exploded adds volatile, explosive fluids to the [Create](https://modrinth.com/mod/create) ecosystem. Fuels and crude liquids stored in Fluid Tanks and Fluid Vessels are no longer inert — expose them to a nearby explosion, and they'll detonate with force proportional to how much flammable liquid they're holding.

No more parking a warehouse of gasoline next to your TNT cannon and walking away unscathed.

---

## How It Works

- **Chain reactions, not magic.** Tanks don't spontaneously combust — any explosion (TNT, creepers, other mods, or another exploded tank) that reaches a tank containing a flammable fluid will set it off in turn. Build your fuel depot next to a minefield at your own risk.
- **Explosion power scales with fluid volume.** A near-empty tank gives a modest pop. A fully stacked multiblock tank brimming with crude oil is a crater. The mod reads the *actual* fluid amount stored (not the tank's total capacity), so partially filled tanks are proportionally safer.
- **Not all fluids are equally dangerous.** Each supported liquid has its own explosiveness rating based on real-world combustion energy and flammability — crude oil and diesel hit harder than plant oil or ethanol, for example.
- **Multiblock aware.** Create's tanks merge into a single shared reservoir when connected — Create: Exploded treats the whole connected structure as one volume when calculating blast force, so building a bigger tank farm means building a bigger bomb.

---

## Supported Fluids & Compatibility

| Source | Container(s) | Status |
|---|---|---|
| **[Create](https://modrinth.com/mod/create)** | Fluid Tank | Required |
| **[Create: Diesel Generators](https://modrinth.com/mod/create-diesel-generators)** | Fluid Tank / Fluid Vessel (Crude Oil, Biodiesel, Diesel, Gasoline, Plant Oil, Ethanol) | Required |
| **[Create: Connected](https://modrinth.com/mod/create-connected)** | Fluid Vessel | Built-in support |
| **[Create Propulsion: Simulated](https://modrinth.com/mod/create-propulsion-simulated)** | Thruster, Liquid Vector Thruster | Built-in support |

**Required** — the mod won't load without these; Create provides the tanks, and Create: Diesel Generators provides the explosive fluids themselves.

**Built-in support** — this mod's fluid containers are explicitly handled in code with their own explosion logic, not just picked up by generic detection.

Beyond the mods listed above, Create: Exploded also auto-detects fluid containers from other mods it wasn't specifically built for, so it will generally work alongside other Create addons that store fluids. This detection is generic (based on capability and block-name matching) rather than mod-specific, so behavior for unlisted mods isn't guaranteed to be fully consistent — treat it as best-effort compatibility rather than a supported feature.

---

## Why Install This?

- Adds real stakes to fuel logistics in industrial Create builds
- Makes tank placement and blast shielding an actual design consideration
- Rewards contraptions that separate storage from combat/mining operations
- Plays nicely with modpacks built around Create's diesel/oil processing chains

---

## Requirements

- **Loader:** NeoForge
- **Required:** Create, Create: Diesel Generators

---

## Feedback & Issues

Found a fluid that should (or shouldn't) go boom? Balance feels off? Leave your feedback on my Discord server — this mod is actively tuned based on player feedback.
[Discord!](https://discord.gg/VhuDn6A7WA)