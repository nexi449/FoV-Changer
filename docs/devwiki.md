# Developer Wiki

This page documents how Fov-Changer works for developers reading the project. It describes runtime behavior and source-code responsibilities; it does not provide instructions for building, modifying, or distributing the software.

## License and scope

Fov-Changer is **ARR (All Rights Reserved)**. Access to source code or this technical explanation does not grant permission to copy, modify, build, or redistribute the project. The repository's root `LICENSE` file states the applicable terms.

## What the mod changes

Minecraft's client creates its FOV option with a numeric upper bound of `110`. Fov-Changer changes that bound to `200` while the client options are initialized. It does not replace the slider, introduce a second setting, or change a server-side value.

The change is applied through a SpongePowered Mixin `@ModifyConstant` injection:

```java
@ModifyConstant(method = "<init>", constant = @Constant(intValue = 110))
private int maxFovValueHook(int constant) {
    return 200;
}
```

The target is the options class used by the Minecraft version in that project:

| Loader/project | Target class | Source Mixin |
| --- | --- | --- |
| Fabric | Yarn: `net.minecraft.client.option.GameOptions`; Mojang: `net.minecraft.client.Options` | `MixinGameOptions` and `MixinOptions` |
| Quilt | Yarn: `net.minecraft.client.option.GameOptions`; Mojang: `net.minecraft.client.Options` | `MixinGameOptions` and `MixinOptions` |
| NeoForge | `net.minecraft.client.Options` | `MixinOptions` |
| Forge | `net.minecraft.client.Options` | `MixinOptions` |

The injection targets the constructor (`<init>`). Mixin finds the integer constant `110` in that method, passes it to the handler, and uses the returned `200` instead. This only changes the matching constant at the injection point; the mixin does not read or store a separate Fov-Changer preference.

## Loader entrypoints and registration

Each loader project supplies its own metadata and entrypoint conventions:

- **Fabric** declares `me.damon.CustomFov` as a main entrypoint. Its initializer logs that the mod is initializing. `fabric.mod.json` registers `custom-fov.mixins.json`.
- **Quilt** declares the mod and its dependencies in `quilt.mod.json`; that metadata names `custom-fov.mixins.json`. Its implementation consists of the loader-independent options mixins.
- **NeoForge** has a `@Mod("custom_fov")` entrypoint class and declares the mixin in `neoforge.mods.toml`.
- **Forge** has a Forge entrypoint class and client mixin configuration. The version-specific project metadata uses `META-INF/mods.toml`.

Loader entrypoints are integration points, not where the FOV range is changed. The actual value change is made by the registered client mixin.

## Mixin configuration and client-only behavior

The `custom-fov.mixins.json` configuration identifies the `me.damon.mixin` package and lists the options mixins. Forge's configuration uses the `client` list explicitly; Fabric and Quilt register their client options targets through their loader-specific mixin metadata and client-only Minecraft classes. `required: true` makes the configuration required, and `defaultRequire: 1` requires at least one matching injection point for each injector.

The project targets differ in naming and mapping conventions. Fabric and Quilt register both the Yarn-named `GameOptions` mixin and a string-named Mojang `Options` mixin. The latter and the NeoForge mixin use `@Pseudo` plus a string target, so Mixin can resolve the target at runtime without requiring that exact name to be resolvable in the compile-time mappings. Forge versions use `Options.class` directly. These are loader/mapping adaptations of the same intended injection, not separate FOV features.

The Java compatibility level is declared in the Fabric/Quilt configuration and the Forge 1.21.1 configuration. The 26.x Forge configuration uses its modern Forge Gradle integration and does not declare that older-style refmap entry. These details affect how the Mixin target is resolved; they do not alter the returned FOV maximum.

## Forge version layout

Forge has separate projects for each supported game version because Forge and Minecraft APIs, mappings, Java requirements, and dependency coordinates vary between releases. The project currently includes:

- `1.21.1`
- `1.21.3` through `1.21.11`
- `26.1`, `26.1.1`, `26.1.2`, `26.2`, and `26.3`

There is no Forge project for Minecraft `1.21.2` in the project. Each Forge version directory carries its own source and exact Minecraft/Forge dependency metadata; a version-specific source folder should be read as the compatibility target for that project, rather than assuming one Forge artifact covers the whole range.

Fabric and Quilt metadata declare a broad Minecraft range, while their project dependencies and source use a specific mapping/API baseline. Treat the game-version labels of the particular published artifact as authoritative; a metadata range alone does not demonstrate that every intermediate game version has been tested.

## Runtime constraints and failure modes

The mixin requires the target constructor to contain the expected integer constant. Its configuration uses `defaultRequire: 1`, so if an injector cannot find a match, Mixin reports a failure rather than silently applying no change. A game update can rename the options class, change mappings, or alter the constructor and constant; any of those can break the injection. A version range in loader metadata does not guarantee that the runtime target is unchanged.

When investigating a compatibility issue, compare the affected version's target class, mappings, constructor bytecode, mixin configuration, and loader metadata. A successful source-level match is not by itself proof that the game starts or that the slider behaves correctly in a live client.

## Reading the implementation

Trace the change in this order:

1. Loader metadata registers `custom-fov.mixins.json`.
2. The mixin configuration selects the loader/version-appropriate options mixin.
3. `MixinOptions` or `MixinGameOptions` targets the constructor that creates the FOV option.
4. `@ModifyConstant` replaces `110` with `200`.
5. Minecraft's existing FOV slider exposes the resulting range.

No separate config model, settings screen, persistence layer, or game-state mutation is involved.
