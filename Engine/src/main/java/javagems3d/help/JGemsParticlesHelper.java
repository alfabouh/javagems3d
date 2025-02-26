package javagems3d.help;

import javagems3d.graphics.particles.ParticlesEmitter;
import javagems3d.graphics.particles.attributes.ParticleAttributes;
import javagems3d.graphics.particles.objects.SimpleColoredParticle;
import javagems3d.graphics.particles.objects.SimpleTexturedParticle;
import javagems3d.graphics.particles.objects.base.ParticleFX;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class JGemsParticlesHelper {
    public static SimpleTexturedParticle createSimpleTexturedParticle(ParticleAttributes particleAttributes, ParticleTexturesPack particleTexturesPack, Vector3f pos, Vector2f scaling) {
        return ParticlesEmitter.createSimpleTexturedParticle(JGemsCoreHelper.getSceneWorld(), particleAttributes, particleTexturesPack, pos, scaling);
    }

    public static SimpleColoredParticle createSimpleColoredParticle(ParticleAttributes particleAttributes, Vector3f color, Vector3f pos, Vector2f scaling) {
        return ParticlesEmitter.createSimpleColoredParticle(JGemsCoreHelper.getSceneWorld(), particleAttributes, color, pos, scaling);
    }

    public static ParticleFX emitParticle(ParticleFX particleFX) {
        getParticlesEmitter().emitParticle(particleFX);
        return particleFX;
    }

    public static ParticlesEmitter getParticlesEmitter() {
        return JGemsCoreHelper.getScreen().getScene().getWorld().getParticlesEmitter();
    }
}
