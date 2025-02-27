package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public static ImageTexture DEFAULT;
    public static JGemsPath defaultSkyCubeMapPath = new JGemsPath(JGems3D.DEF_PATHS.CUBE_MAPS, "default", "sky_");

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

    public void load(SystemResources systemResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading textures...");
        TextureAssetsInitializer.DEFAULT = systemResources.createTexture(null, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "default.png"), new ImageTexture.Properties(false, false, true, false, false));

        Font gameFont = SystemResources.createFontFromJAR(new JGemsPath("/assets/jgems/gamefont.ttf"));

        this.standardFont2 = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.waterNormals = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "liquids/water_n.png"), new ImageTexture.Properties(true, true));
        this.waterTexture = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "liquids/water.png"), new ImageTexture.Properties(true, true));
        this.crosshair = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "gui/crosshair.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.gui1 = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "gui/gui1.png"), new ImageTexture.Properties(false, false, false, false, false));

        this.zippo1 = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "items/zippo/zippo1.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.zippo1_1 = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "items/zippo/zippo1_1.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.zippo2 = systemResources.createTexture(TextureAssetsInitializer.DEFAULT, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "items/zippo/zippo2.png"), new ImageTexture.Properties(false, false, false, false, false));

        this.particleTexturesPack = new ParticleTexturesPack(new JGemsPath(JGems3D.DEF_PATHS.PARTICLES, "flame"), ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = systemResources.createCubeMapTexture(null, TextureAssetsInitializer.defaultSkyCubeMapPath, "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap = systemResources.createCubeMapTexture(null, new JGemsPath(JGems3D.DEF_PATHS.CUBE_MAPS, "skyDay", "sky_"), "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap2 = systemResources.createCubeMapTexture(null, new JGemsPath(JGems3D.DEF_PATHS.CUBE_MAPS, "skyNight", "sky_"), "bmp", new CubeMapTexture.Properties(true));
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
