# Graphics System

JavaGems3D uses an OpenGL 4.3 based rendering pipeline focused on practical real-time rendering for small and experimental 3D projects.

The rendering system is built around flexibility: both direct rendering and indirect rendering are supported, custom shaders can be fully integrated, and most engine rendering resources are accessible through the API.

The current renderer is based on OpenGL, but in the future I plan to move the rendering backend toward Vulkan. OpenGL remains the stable production path for now, while Vulkan migration is considered a long-term architectural goal.

This page covers only the basic concepts. The rendering system is large and will be documented in more detail later.

---

# Rendering Features

Currently implemented:

* Deferred Rendering
* Forward rendering where needed
* SSAO (Screen Space Ambient Occlusion)
* HDR
* Bloom
* FXAA
* Weighted OIT (Order Independent Transparency)
* Discard-based transparency
* Cascaded EVSM shadows for directional light
* Point light VSM shadow maps
* Bindless textures
* SSBO buffers support
* Direct rendering pipeline
* Indirect rendering pipeline (`glMultiDrawElementsIndirect`)
* Skeletal animation support
* Skybox rendering
* Dear ImGui integration
* Custom internal UI system
* Background scene rendering

Planned / future work:

* Vulkan migration
* PBR 
* Global illumination
* Rendering pipeline cleanup and optimization
* Better editor-side rendering tooling

---

# Shader System

JavaGems3D supports multiple shader stages:

```java
FRAGMENT("fragment.frag"),
VERTEX("vertex.vert"),
GEOMETTRIC("geometric.geom"),
COMPUTE("compute.comp"),
TESS_CONTROL("tess.tesc"),
TESS_EVALUATION("tess.tese");
```

Tessellation shader support exists in the engine API, but it has not been fully tested yet.

---

# Shader Folder Structure

Shaders must be placed inside a dedicated folder.

Each shader file must use strict predefined names:

```text
fragment.frag
vertex.vert
geometric.geom
compute.comp
tess.tesc
tess.tese
```

Example:

```text
assets/
└── shaders/
    └── my_shader/
        ├── vertex.vert
        └── fragment.frag
```

The engine automatically detects available stages from this folder.

It is strongly recommended to use existing shaders from the engine `shaders/` folder as the base for new development.

This makes integration significantly easier.

---

# Creating Custom Shaders

Custom shaders are usually loaded through a class extending:

```java
ShadersInitializer<JGemsShaderManager>
```

Example:

```java
public final class AppShadersInitializer extends ShadersInitializer<JGemsShaderManager> {

    public JGemsShaderManager customWorldShader;
    public ShaderStorageBufferObject sceneData;

    @Override
    protected void initStaticConstants(ShaderStaticConstants constants) {
        constants.createConstant(
            "MAX_OBJECTS",
            String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS)
        );
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager libraries) {
        libraries.createLibrary(
            new JGemsPathSource(
                "/assets/shaders/libs/lighting",
                ISource.Source.INSIDE_JAR
            )
        );
    }

    @Override
    protected void initObjects(ResourceCache cache) {
        this.sceneData = new ShaderStorageBufferObject(
            0,
            1024
        );

        ShaderStorageBufferProgram.createSSBOStorage(
            this.sceneData,
            GL46.GL_DYNAMIC_STORAGE_BIT
        );

        this.customWorldShader = this.createShaderManager(
            cache,
            new JGemsPathSource(
                new JGemsPath(
                    JGems3D.DEFAULT_PATHS.SHADERS,
                    "world/custom_world"
                ),
                ISource.Source.INSIDE_JAR
            )
        );
    }

    @Override
    protected JGemsShaderManager createShaderObject(
        @NotNull JGemsPathSource shaderPath,
        ShaderStaticConstants constants,
        ShaderLibrariesManager libraries
    ) {
        return new JGemsShaderManager(
            new ShadersContainer(
                shaderPath,
                constants,
                libraries
            )
        );
    }
}
```

This is the standard way to register application shaders.

---

# Static Constants

The engine supports compile-time shader constants.

Example GLSL:

```glsl
layout(std430, binding = 1) buffer IndirectBufferData {
    int entityId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int materialId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};
```

These values are injected directly from the engine:

```java
constants.createConstant(
    "MAX_INDIRECT_RENDERING_MESH_DATASETS",
    String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS)
);
```

This avoids hardcoded values inside GLSL and keeps rendering configuration synchronized with engine limits.

---

# Shader Libraries

Shader libraries are also supported.

Example:

```glsl
#include "/assets/shaders/libs/animations"
```

The engine parses this include and injects the shader code automatically.

This is heavily used for:

* shadows
* skeletal animation
* lighting logic
* shared utility functions

---

# Uniform System

You do not need to manually register uniforms.

During shader loading, uniforms are automatically parsed and registered by the engine.

This significantly simplifies shader integration.

Basic usage:

```java
shader.beginShading();

shader.performUniformTexture(
    new UniformString(DefaultUniformDefinitions.TEXTURE_MAP),
    finalFBO.getTextureByIndex(0)
);

shader.performOrthographicMatrix(
    new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX),
    this.getScreenModel(),
    JGemsTransformManager.INSTANCE.getOrthographicMatrix()
);

JGemsHelper.render().renderModel2D(
    this.getScreenModel(),
    GL46.GL_TRIANGLES
);

shader.endShading();
```

---

# Custom Uniform Values

For custom values, helper functions already exist:

```java
UniformFunctions.FLOAT(...)
UniformFunctions.INTEGER(...)
UniformFunctions.VEC3F(...)
UniformFunctions.VEC4F(...)
UniformFunctions.MAT4F(...)
UniformFunctions.BOOLEAN(...)
UniformFunctions.TEXTURE64ARB(...)
```

Example:

```java
shaderManager.performUniform(
    new UniformString(DefaultUniformDefinitions.ALPHA_DISCARD),
    UniformFunctions.FLOAT(
        JGemsConfig.SYSTEM.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT
    )
);
```

This is the standard way to pass custom shader values.

---

# Direct and Indirect Rendering

The engine supports two rendering approaches:

* Direct Rendering
* Indirect Rendering

Both can work inside the same scene.

---

# Mesh Types

Two different mesh structures exist:

## MeshGroup

Used for direct rendering.

```java
createMeshGroup(...)
```

This is the standard per-object rendering path.

---

## MeshBuffer

Used for indirect rendering.

```java
createMeshBuffer(...)
```

This is used for GPU-side batched rendering and large scene optimization.

---

# Render Table System

Entities and props use the Render Table system for render configuration.

This controls:

* direct vs indirect rendering
* rendering stage
* render properties
* object behavior inside pipelines

Example:

```java
RenderTable.getDirect()
RenderTable.getIndirect()
```

Rendering behavior can be configured using object properties and scene tags.

This allows both rendering approaches to coexist naturally.

---

# Render Fabrics

To simplify rendering logic, JavaGems3D uses Render Fabric classes.

## Direct

```java
DirectRenderFabric
```

Used for standard rendering passes.

Includes:

* pre-render
* render
* post-render

---

## Indirect

```java
IndirectRenderFabric
```

Used for indirect rendering pipelines.

Includes:

* matrix buffer preparation
* property buffer preparation
* indirect GPU command execution

Internally this uses:

```java
glMultiDrawElementsIndirect(...)
```

for large batched rendering.

This is the main optimization path for heavy scenes.

---

# Useful Global Rendering Resources

Many useful resources are available through:

```java
JGemsResourceManager
```

Examples:

```java
JGemsResourceManager.globalShaderAssets
JGemsResourceManager.globalTextureAssets
JGemsResourceManager.globalModelAssets
JGemsResourceManager.globalRenderDataAssets
```

Also:

```java
JGemsHelper.render()
```

contains many useful rendering helpers and utility functions.

This is usually the first place to check before implementing custom rendering code.

---

# Resource Cache

The engine includes resource caching for:

* models
* textures
* shaders
* sounds

This avoids duplicated loading and improves runtime performance.

Multiple loading paths are also supported depending on rendering mode.

---

# Final Notes

The graphics system is one of the largest parts of JavaGems3D.

This page only explains the most important basics.

Future documentation will cover:

* full rendering pipeline stages
* shadow system internals
* indirect rendering architecture
* animation rendering
* material system
* bindless textures
* editor rendering internals
* Vulkan migration roadmap
