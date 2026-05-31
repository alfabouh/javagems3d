package javagems3d.system.resources.assets.texturing;

import org.lwjgl.opengl.GL46;

public interface ISample {
    interface IProperties {
        default int getTextureTypeByFormat(int format) {
            return switch (format) {
                case GL46.GL_R, GL46.GL_RG, GL46.GL_RGB, GL46.GL_RGBA, GL46.GL_RGBA32F, GL46.GL_RGB32F, GL46.GL_RG32F, GL46.GL_R32F, GL46.GL_RGBA16F, GL46.GL_RGB16F, GL46.GL_RG16F, GL46.GL_R16F -> GL46.GL_FLOAT;
                case GL46.GL_R16UI ->  GL46.GL_UNSIGNED_SHORT;
                default -> GL46.GL_UNSIGNED_BYTE;
            };
        }
    }
}
