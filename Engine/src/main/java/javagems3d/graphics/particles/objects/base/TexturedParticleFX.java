package javagems3d.graphics.particles.objects.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.particles.attributes.ParticleAttributes;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;

public abstract class TexturedParticleFX extends ParticleFX {
    public TexturedParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull ParticleTexturesPack particleTexturesPack, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, particleTexturesPack, pos, scaling);
    }
}
