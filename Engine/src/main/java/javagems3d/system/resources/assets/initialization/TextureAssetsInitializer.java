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

package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public static ImageTexture DEFAULT;
    public static JGemsPath defaultSkyCubeMapPath = new JGemsPath(JGems3D.Paths.CUBE_MAPS, "default", "sky_");

    public ImageTexture waterTexture;
    public ImageTexture waterNormals;
    public ParticleTexturesPack particleTexturesPack;
    public CubeMapTexture defaultSkyboxCubeMap;
    public CubeMapTexture skyboxCubeMap;
    public CubeMapTexture skyboxCubeMap2;
    public GuiFont standardFont2;
    public GuiFont standardFont;
    public GuiFont buttonFont;
    public ImageTexture crosshair;
    public ImageTexture gui1;

    public ImageTexture zippo1;
    public ImageTexture zippo1_1;
    public ImageTexture zippo2;

    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading textures...");
        TextureAssetsInitializer.DEFAULT = gameResources.createTexture(null, new JGemsPath(JGems3D.Paths.TEXTURES, "default.png"), new ImageTexture.Properties(false, true, false, false));

        Font gameFont = JGemsResourceManager.createFontFromJAR(new JGemsPath("/assets/jgems/gamefont.ttf"));

        this.standardFont2 = new GuiFont(gameResources, gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(gameResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(gameResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.waterNormals = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "liquids/water_n.png"), new ImageTexture.Properties(true));
        this.waterTexture = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "liquids/water.png"), new ImageTexture.Properties(true));
        this.crosshair = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "gui/crosshair.png"), new ImageTexture.Properties(false, false, false, false));
        this.gui1 = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "gui/gui1.png"), new ImageTexture.Properties(false, false, false, false));

        this.zippo1 = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1.png"), new ImageTexture.Properties(false, false, false, false));
        this.zippo1_1 = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo1_1.png"), new ImageTexture.Properties(false, false, false, false));
        this.zippo2 = gameResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.Paths.TEXTURES, "items/zippo/zippo2.png"), new ImageTexture.Properties(false, false, false, false));

        this.particleTexturesPack = new ParticleTexturesPack(new JGemsPath(JGems3D.Paths.PARTICLES, "flame"), ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = gameResources.createCubeMapTexture(null, TextureAssetsInitializer.defaultSkyCubeMapPath, "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap = gameResources.createCubeMapTexture(null, new JGemsPath(JGems3D.Paths.CUBE_MAPS, "skyDay", "sky_"), "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap2 = gameResources.createCubeMapTexture(null, new JGemsPath(JGems3D.Paths.CUBE_MAPS, "skyNight", "sky_"), "bmp", new CubeMapTexture.Properties(true));
    }

    @NotNull
    public static ImageTexture DEFAULT_2D_TEXTURE() {
        return TextureAssetsInitializer.DEFAULT;
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.HIGH;
    }
}
