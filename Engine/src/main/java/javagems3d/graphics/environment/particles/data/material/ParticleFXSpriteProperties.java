package javagems3d.graphics.environment.particles.data.material;

import org.joml.Vector2i;

public record ParticleFXSpriteProperties(Vector2i cellsXY, int maxSprites, boolean loop, float loopNextFrameInSecSpeed, boolean fadeOut, boolean normalizeY) {
}
