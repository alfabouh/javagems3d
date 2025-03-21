package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public static JGemsPath defaultSkyCubeMapPath = new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "default", "sky_");

    public ITexture2DProgram waterTexture;
    public ITexture2DProgram waterNormals;
    public ParticleTexturesPack particleTexturesPack;
    public ICubeMapProgram defaultSkyboxCubeMap;
    public ICubeMapProgram skyboxCubeMap;
    public ICubeMapProgram skyboxCubeMap2;
    public GuiFont standardFont2;
    public GuiFont standardFont;
    public GuiFont buttonFont;
    public ITexture2DProgram crosshair;
    public ITexture2DProgram gui1;

    public ITexture2DProgram zippo1;
    public ITexture2DProgram zippo1_1;
    public ITexture2DProgram zippo2;

    public void load(SystemResources systemResources) {
        Font gameFont = SystemResources.createFontFromJAR(new JGemsPath("/assets/jgems/gamefont.ttf"));

        this.standardFont2 = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new GuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.waterNormals = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "liquids/water_n.png"), new ImageTexture.Properties(true, true));
        this.waterTexture = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "liquids/water.png"), new ImageTexture.Properties(true, true));
        this.crosshair = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "gui/crosshair.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.gui1 = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "gui/gui1.png"), new ImageTexture.Properties(false, false, false, false, false));

        this.zippo1 = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "items/zippo/zippo1.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.zippo1_1 = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "items/zippo/zippo1_1.png"), new ImageTexture.Properties(false, false, false, false, false));
        this.zippo2 = systemResources.createTexture(ResourceManager.DEFAULT_TEXTURE(), new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "items/zippo/zippo2.png"), new ImageTexture.Properties(false, false, false, false, false));

        this.particleTexturesPack = new ParticleTexturesPack(new JGemsPath(JGems3D.DEFAULT_PATHS.PARTICLES, "flame"), ".png", 4, 0.25f);

        this.defaultSkyboxCubeMap = systemResources.createCubeMapTexture(null, TextureAssetsInitializer.defaultSkyCubeMapPath, "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap = systemResources.createCubeMapTexture(null, new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay"), "png", new CubeMapTexture.Properties(true));
        this.skyboxCubeMap2 = systemResources.createCubeMapTexture(null, new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyNight"), "bmp", new CubeMapTexture.Properties(true));
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
