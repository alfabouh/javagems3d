package javagems3d.graphics.rendering.programs.textures.base;

import javagems3d.system.service.files.source.JGemsPathSource;
import org.joml.Vector2i;

public interface ICubeMapProgram extends ITextureProgram {
    Vector2i[] getSize();

    class CMTextures {
        private JGemsPathSource textureUPPath;
        private JGemsPathSource textureBOTTOMPath;
        private JGemsPathSource textureFRONTPath;
        private JGemsPathSource textureBACKPath;
        private JGemsPathSource textureLEFTPath;
        private JGemsPathSource textureRIGHTPath;

        public CMTextures(JGemsPathSource textureUPPath, JGemsPathSource textureBOTTOMPath, JGemsPathSource textureFRONTPath, JGemsPathSource textureBACKPath, JGemsPathSource textureLEFTPath, JGemsPathSource textureRIGHTPath) {
            this.textureUPPath = textureUPPath;
            this.textureBOTTOMPath = textureBOTTOMPath;
            this.textureFRONTPath = textureFRONTPath;
            this.textureBACKPath = textureBACKPath;
            this.textureLEFTPath = textureLEFTPath;
            this.textureRIGHTPath = textureRIGHTPath;
        }

        public JGemsPathSource getTextureUPPath() {
            return this.textureUPPath;
        }

        public JGemsPathSource getTextureBOTTOMPath() {
            return this.textureBOTTOMPath;
        }

        public JGemsPathSource getTextureFRONTPath() {
            return this.textureFRONTPath;
        }

        public JGemsPathSource getTextureBACKPath() {
            return this.textureBACKPath;
        }

        public JGemsPathSource getTextureLEFTPath() {
            return this.textureLEFTPath;
        }

        public JGemsPathSource getTextureRIGHTPath() {
            return this.textureRIGHTPath;
        }

        public CMTextures setTextureUPPath(JGemsPathSource textureUPPath) {
            this.textureUPPath = textureUPPath;
            return this;
        }

        public CMTextures setTextureBOTTOMPath(JGemsPathSource textureBOTTOMPath) {
            this.textureBOTTOMPath = textureBOTTOMPath;
            return this;
        }

        public CMTextures setTextureFRONTPath(JGemsPathSource textureFRONTPath) {
            this.textureFRONTPath = textureFRONTPath;
            return this;
        }

        public CMTextures setTextureBACKPath(JGemsPathSource textureBACKPath) {
            this.textureBACKPath = textureBACKPath;
            return this;
        }

        public CMTextures setTextureLEFTPath(JGemsPathSource textureLEFTPath) {
            this.textureLEFTPath = textureLEFTPath;
            return this;
        }

        public CMTextures setTextureRIGHTPath(JGemsPathSource textureRIGHTPath) {
            this.textureRIGHTPath = textureRIGHTPath;
            return this;
        }
    }
}