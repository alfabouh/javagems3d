/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.programs.textures.base;

import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.joml.Vector2i;

public interface ICubeMapProgram extends ITextureProgram {
    Vector2i[] getSize();

    class CMTextures {
        private JGemsPathSource textureFRONTPath;
        private JGemsPathSource textureBACKPath;
        private JGemsPathSource textureUPPath;
        private JGemsPathSource textureBOTTOMPath;
        private JGemsPathSource textureLEFTPath;
        private JGemsPathSource textureRIGHTPath;

        public CMTextures(JGemsPath absPath, CMTextures another) {
            this(
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureFRONTPath().toString()), another.getTextureFRONTPath().getSource()),
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureBACKPath().toString()), another.getTextureBACKPath().getSource()),
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureUPPath().toString()), another.getTextureUPPath().getSource()),
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureBOTTOMPath().toString()), another.getTextureBOTTOMPath().getSource()),
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureLEFTPath().toString()), another.getTextureLEFTPath().getSource()),
                    new JGemsPathSource(new JGemsPath(absPath, another.getTextureRIGHTPath().toString()), another.getTextureRIGHTPath().getSource())
            );
        }

        public CMTextures(JGemsPathSource textureFRONTPath, JGemsPathSource textureBACKPath, JGemsPathSource textureUPPath, JGemsPathSource textureBOTTOMPath, JGemsPathSource textureLEFTPath, JGemsPathSource textureRIGHTPath) {
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