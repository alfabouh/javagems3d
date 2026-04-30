# Game Build and Compilation

Game compilation in JGems3D is performed through the **Asset Editor** (Game Editor).

This is the standard way to prepare a playable standalone build of a game.

---

# Compile Process

To build the game:

1. Open the Asset Editor

2. Open your game project

3. In the top menu, press:

```text
Compile
```

The engine will start the export/build process.

No external build tools are required for standard project compilation.

---

# Build Output Structure

After compilation, the engine creates a directory in the selected output path containing all required runtime files.

The structure is generally split into two main parts:

```text
build/
├── core/
└── game_dir/
```

---

# `core/`

This folder contains engine runtime files.

Usually it includes:

* system JAR files
* internal engine modules
* optional API extension JARs
* required platform resources

This is the executable runtime layer of the project.

If your game uses custom API extensions, their JAR files can also be placed here.

---

# `game_dir/`

This folder contains actual game content.

Usually it includes:

* maps
* models
* textures
* scripts
* sounds
* entities
* props
* tags
* skyboxes
* project configurations

This is the editable project data layer.

---

# Important: No Encryption

Project data inside `game_dir` is intentionally:

* not encrypted
* not obfuscated
* not packed into proprietary archives

This is done for flexibility and development convenience.

Because of this, game_dir can be opened again directly inside the Game Editor

This allows:

* reopening shipped projects
* fast iteration
* debugging builds
* manual project migration
* easier modding workflows

The engine currently prioritizes usability over content protection.

---

# Reopening a Build

A compiled project is not a “final locked package”.

You can take the exported `game_dir` and reopen it again inside the editor.

---

# Opening Through IDE

Game projects can also be launched manually outside the editor.

For example, directly from an IDE using runtime arguments.

The main argument for this is:

```text
external_def
```

This allows the engine to start using an external project definition instead of the default internal project path.

Example runtime usage:

```text
external_def="C://game_test/"
```

This is commonly used during development when launching directly from IntelliJ IDEA or another IDE.

---

# Recommended Workflow

Recommended production workflow:

```text
Editor → Project → Compile → Build Folder → Launch
```

Recommended development workflow:

```text
IDE → external_def → Fast Testing
```

Both approaches are valid and can be used together.