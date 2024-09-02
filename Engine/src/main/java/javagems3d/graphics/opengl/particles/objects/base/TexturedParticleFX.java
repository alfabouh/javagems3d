/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.particles.objects.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.opengl.particles.attributes.ParticleAttributes;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.system.resources.assets.material.samples.packs.ParticleTexturePack;

public abstract class TexturedParticleFX extends ParticleFX {
    public TexturedParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, particleTexturePack, pos, scaling);
    }
}
