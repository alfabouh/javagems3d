package ru.jgems3d.engine.graphics.opengl.particles.objects.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.ParticleTexturePack;

public abstract class TexturedParticleFX extends ParticleFX {
    public TexturedParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, particleTexturePack, pos, scaling);
    }
}
