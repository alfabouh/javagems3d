package javagems3d.system.resources.assets.models.animation.components;

import org.joml.Matrix4f;

public record Bone(int boneId, String boneName, Matrix4f offset) {
}
