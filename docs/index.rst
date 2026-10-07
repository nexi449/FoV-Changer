Fov-Changer Developer Wiki
==========================

This wiki documents the code, source layout, and runtime behavior of the Fabric
and Quilt editions of Fov-Changer. The editions implement the same gameplay
change using their respective loader APIs. Fabric metadata targets Minecraft
1.21.1; Quilt metadata accepts Minecraft 1.21.1 through 1.21.11. Both require
Java 21 or later.

.. contents:: Contents
   :local:
   :depth: 2

Overview
--------

Fov-Changer raises Minecraft's maximum selectable field-of-view (FOV) value
from 110 to 200. It does not add a separate settings screen or continuously
change the player's FOV; it changes the maximum used by Minecraft's existing
options code.

The repository contains two source trees:

* ``Fabric/`` contains the Fabric edition.
* ``Quilt/`` contains the Quilt edition.

Each edition has its own Java sources, loader metadata, mixin configuration,
and icon. The repository does not include Gradle build files or wrapper
scripts.

Requirements
------------

* Java Development Kit (JDK) 21.
* A Minecraft version supported by the selected edition.
* A Fabric or Quilt installation matching the relevant mod loader and API
  dependencies.

Loader and Minecraft compatibility requirements are declared in each
edition's metadata. Keep them consistent when upgrading Minecraft, a loader,
or an API.

Project layout
--------------

Both editions follow the same source layout:

.. code-block:: text

   <Fabric-or-Quilt>/
   └── src/main/
       ├── java/me/damon/
       │   ├── CustomFov.java
       │   └── mixin/MixinGameOptions.java
       └── resources/
           ├── <loader metadata>
           ├── custom-fov.mixins.json
           └── assets/custom-fov/icon.png

The Fabric metadata file is ``src/main/resources/fabric.mod.json``. The Quilt
metadata file is ``src/main/resources/quilt.mod.json``.

Runtime flow
------------

1. The loader reads the edition's metadata and discovers the
   ``custom-fov.mixins.json`` mixin configuration.
2. The metadata entrypoint invokes ``me.damon.CustomFov`` during mod
   initialization. The initializer logs a startup message.
3. The mixin configuration registers
   ``me.damon.mixin.MixinGameOptions``.
4. ``MixinGameOptions`` targets Minecraft's ``GameOptions`` constructor and
   replaces the integer constant ``110`` with ``200``. Minecraft then uses
   the new upper bound for its normal FOV option.

The mixin configuration requires its injection to match once. This makes a
Minecraft update that changes or removes the target constant fail visibly
rather than silently leaving the old maximum in place.

Source reference
----------------

Fov-Changer mod initializer
~~~~~~~~~~~~~~~~~~~~~~~~~~

``CustomFov.java`` is deliberately small: it defines the mod's logger and
writes an initialization message. Fabric implements Fabric's
``ModInitializer``; Quilt implements Quilt's ``ModInitializer`` and receives
a ``ModContainer``. The actual FOV change is not performed by the entrypoint.

``mixin/MixinGameOptions.java``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The two versions contain the same behavior. ``@Mixin(GameOptions.class)``
targets Minecraft's client options class, and ``@ModifyConstant`` changes the
constructor's ``110`` integer constant to ``200``. Keep the target method,
constant, and mixin JSON registration synchronized when changing this logic.

Loader metadata and mixin configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Fabric's ``fabric.mod.json`` and Quilt's ``quilt.mod.json`` declare the mod
identifier, version, entrypoint, dependencies, and mixin file. The metadata
includes version placeholders that the build environment must resolve. The
mixin JSON declares Java 21 compatibility and the
``MixinGameOptions`` class.

Build and run in a development environment
------------------------------------------

The repository contains source code and resources only; it does not include
Gradle build scripts or wrapper files. To compile or launch either edition,
use a compatible Fabric or Quilt development environment and provide the
dependencies declared in that edition's loader metadata.

Configuration
-------------

Compatibility requirements are declared in each edition's metadata:

* Fabric: Minecraft 1.21.1, Java 21 or later, Fabric Loader 0.16.10 or later,
  and Fabric API.
* Quilt: Minecraft 1.21.1 through 1.21.11, Java 21 or later, Quilt Loader
  0.26.3 or later, and Quilted Fabric API 11.0.0-alpha.3 or later.

When updating compatibility, change the relevant loader metadata and verify
that the mixin target remains valid for that Minecraft version.

Testing and troubleshooting
---------------------------

There are currently no dedicated test sources or build scripts in the
repository. Validate changes by compiling and launching each edition in its
corresponding loader development environment.

If the upper FOV limit remains unchanged, verify that the correct edition is
installed, that its mixin JSON is listed in its metadata, and that the
``GameOptions`` constructor still contains the expected integer constant for
the targeted Minecraft version. A mixin application error in the game log
usually indicates a target/version mismatch.

If compilation fails, check that the development environment uses a
compatible JDK, Minecraft version, loader, and API set.

Development guidelines
----------------------

* Make the same gameplay change in both editions unless the loader APIs
  require a deliberate difference.
* Keep loader-specific changes in the matching ``Fabric/`` or ``Quilt/``
  source tree.
* Keep shared behavior aligned between the two ``MixinGameOptions`` classes.
* When upgrading Minecraft, verify the mixin target and constant against the
  new game version, then compile and launch both editions.
* Do not commit generated build output, local game runs, or IDE files.

License
-------

The repository's ``LICENSE`` file defines the applicable usage terms. Check
it before redistributing or incorporating project code.