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

package javagems3d.graphics.environment.particles.data;

import javagems3d.graphics.environment.particles.JGemsParticlesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Vector2i;
import org.joml.Vector3f;

public final class ParticleFXRenderConfig {
    private final JGemsShaderManager transparencyShader;
    private final JGemsShaderManager mainShader;

    private float emissionStrength;
    private float alphaDiscard;

    private final ImageTexture texture;
    private Color4Texture diffuseColor;
    private Color3Texture emissionColor;

    private Vector2i cellsXY;
    private int maxSprites;
    private boolean loop;
    private float loopSpeed;
    private boolean fadeOut;
    private boolean normalizeY;

    private ParticleFXRenderConfig(Builder b) {
        this.transparencyShader = b.transparencyShader;
        this.mainShader = b.mainShader;

        this.emissionStrength = b.emissionStrength;
        this.alphaDiscard = b.alphaDiscard;

        this.texture = b.texture;
        this.diffuseColor = b.diffuseColor;
        this.emissionColor = b.emissionColor;

        this.cellsXY = b.cellsXY;
        this.maxSprites = b.maxSprites;
        this.loop = b.loop;
        this.loopSpeed = b.loopSpeed;
        this.fadeOut = b.fadeOut;
        this.normalizeY = b.normalizeY;
    }

    public ParticleFXRenderConfig copy() {
        ParticleFXRenderConfig cfg = new ParticleFXRenderConfig.Builder(this.texture)
                .shaders(this.transparencyShader, this.mainShader)
                .emission(this.emissionStrength)
                .alphaDiscard(this.alphaDiscard)
                .diffuse(new Color4Texture(this.diffuseColor.color()))
                .emissionColor(new Color3Texture(this.emissionColor.color()))
                .sprite(this.cellsXY.x, this.cellsXY.y, this.maxSprites)
                .loop(this.loop)
                .loopSpeed(this.loopSpeed)
                .fadeOut(this.fadeOut)
                .normalizeY(this.normalizeY)
                .build();
        return cfg;
    }

    public static Builder builder(ImageTexture texture) {
        return new Builder(texture);
    }

    public ParticleFXRenderConfig setEmissionStrength(float emissionStrength) {
        this.emissionStrength = emissionStrength;
        return this;
    }

    public ParticleFXRenderConfig setAlphaDiscard(float alphaDiscard) {
        this.alphaDiscard = alphaDiscard;
        return this;
    }

    public ParticleFXRenderConfig setDiffuseColor(Color4Texture diffuseColor) {
        this.diffuseColor = diffuseColor;
        return this;
    }

    public ParticleFXRenderConfig setEmissionColor(Color3Texture emissionColor) {
        this.emissionColor = emissionColor;
        return this;
    }

    public ParticleFXRenderConfig setCellsXY(Vector2i cellsXY) {
        this.cellsXY = cellsXY;
        return this;
    }

    public ParticleFXRenderConfig setMaxSprites(int maxSprites) {
        this.maxSprites = maxSprites;
        return this;
    }

    public ParticleFXRenderConfig setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public ParticleFXRenderConfig setLoopSpeed(float loopSpeed) {
        this.loopSpeed = loopSpeed;
        return this;
    }

    public ParticleFXRenderConfig setFadeOut(boolean fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public ParticleFXRenderConfig setNormalizeY(boolean normalizeY) {
        this.normalizeY = normalizeY;
        return this;
    }

    public JGemsShaderManager getTransparencyShader() {
        return this.transparencyShader;
    }

    public JGemsShaderManager getMainSceneShader() {
        return this.mainShader;
    }

    public float getEmissionStrength() {
        return this.emissionStrength;
    }

    public float getAlphaDiscard() {
        return this.alphaDiscard;
    }

    public ImageTexture getTexture() {
        return this.texture;
    }

    public Color4Texture getDiffuseColor() {
        return this.diffuseColor;
    }

    public Color3Texture getEmissionColor() {
        return this.emissionColor;
    }

    public Vector2i getCellsXY() {
        return this.cellsXY;
    }

    public int getMaxSprites() {
        return this.maxSprites;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public float getLoopSpeed() {
        return this.loopSpeed;
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    public boolean isNormalizeY() {
        return this.normalizeY;
    }

    public static final class Builder {
        private JGemsShaderManager transparencyShader;
        private JGemsShaderManager mainShader;

        private float emissionStrength = 0.0f;
        private float alphaDiscard = 0.0f;

        private final ImageTexture texture;
        private Color4Texture diffuseColor = new Color4Texture(1f, 1f, 1f, 0.99f);
        private Color3Texture emissionColor = new Color3Texture(new Vector3f(1.0f));

        private Vector2i cellsXY = new Vector2i(1, 1);
        private int maxSprites = 1;
        private boolean loop = false;
        private float loopSpeed = 5.0f;
        private boolean fadeOut = true;
        private boolean normalizeY = false;

        public Builder(ImageTexture texture) {
            this.texture = texture;
        }

        public Builder shaders(JGemsShaderManager transparency, JGemsShaderManager main) {
            this.transparencyShader = transparency;
            this.mainShader = main;
            return this;
        }

        public Builder emission(float strength) {
            this.emissionStrength = strength;
            return this;
        }

        public Builder alphaDiscard(float alphaDiscard) {
            this.alphaDiscard = alphaDiscard;
            return this;
        }

        public Builder diffuse(Color4Texture color) {
            this.diffuseColor = color;
            return this;
        }

        public Builder emissionColor(Color3Texture color) {
            this.emissionColor = color;
            return this;
        }

        public Builder sprite(int x, int y, int max) {
            this.cellsXY = new Vector2i(x, y);
            this.maxSprites = max;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder loopSpeed(float speed) {
            this.loopSpeed = speed;
            return this;
        }

        public Builder fadeOut(boolean fade) {
            this.fadeOut = fade;
            return this;
        }

        public Builder normalizeY(boolean normalizeY) {
            this.normalizeY = normalizeY;
            return this;
        }


        public ParticleFXRenderConfig build() {
            if (this.transparencyShader == null) {
                this.transparencyShader = JGemsParticlesManager.DEFAULT_JGEMS_TRANSPARENCY_SCENE_SHADER();
            }
            if (this.mainShader == null) {
                this.mainShader = JGemsParticlesManager.DEFAULT_JGEMS_MAIN_SCENE_SHADER();
            }
            return new ParticleFXRenderConfig(this);
        }
    }
}