package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public static JGemsPathSource defaultSkyCubeUPPath =    new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_1.png"), ISource.Source.INSIDE_JAR);
    public static JGemsPathSource defaultSkyCubeDOWNPath =  new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_2.png"), ISource.Source.INSIDE_JAR);
    public static JGemsPathSource defaultSkyCubeFRONTPath = new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_3.png"), ISource.Source.INSIDE_JAR);
    public static JGemsPathSource defaultSkyCubeBACKPath =  new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_4.png"), ISource.Source.INSIDE_JAR);
    public static JGemsPathSource defaultSkyCubeLEFTPath =  new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_5.png"), ISource.Source.INSIDE_JAR);
    public static JGemsPathSource defaultSkyCubeRIGHTPath = new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay/sky_6.png"), ISource.Source.INSIDE_JAR);
    public static final ICubeMapProgram.CMTextures DEF_CUBE_MAP_TEXTURES = new ICubeMapProgram.CMTextures(
            TextureAssetsInitializer.defaultSkyCubeUPPath,
            TextureAssetsInitializer.defaultSkyCubeDOWNPath,
            TextureAssetsInitializer.defaultSkyCubeFRONTPath,
            TextureAssetsInitializer.defaultSkyCubeBACKPath,
            TextureAssetsInitializer.defaultSkyCubeLEFTPath,
            TextureAssetsInitializer.defaultSkyCubeRIGHTPath
    );

    public ITexture2DProgram waterTexture;
    public ITexture2DProgram waterNormals;
    public ICubeMapProgram defaultSkyboxCubeMap;
    //public ICubeMapProgram skyboxCubeMap;
    //public ICubeMapProgram skyboxCubeMap2;
    public JGemsGuiFont standardFont2;
    public JGemsGuiFont standardFont;
    public JGemsGuiFont buttonFont;
    public ITexture2DProgram crosshair;
    public ITexture2DProgram gui1;

    public ImageTexture defaultParticle;

    public void load(SystemResources systemResources) {
        Font gameFont = SystemResources.createFontFromFile(new JGemsPathSource(new JGemsPath("/assets/gamefont.ttf"), ISource.Source.INSIDE_JAR));

        this.standardFont2 = new JGemsGuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 18), FontCode.Window);
        this.standardFont = new JGemsGuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);
        this.buttonFont = new JGemsGuiFont(systemResources, gameFont.deriveFont(Font.PLAIN, 24), FontCode.Window);

        this.waterNormals = systemResources.createTexture(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "liquids/water_n.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, true));
        this.waterTexture = systemResources.createTexture(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "liquids/water.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, true));
        this.crosshair = systemResources.createTexture(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "gui/crosshair.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(false, false, false, false, false));
        this.gui1 = systemResources.createTexture(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "gui/gui1.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(false, false, false, false, false));

        this.defaultParticle = (ImageTexture) systemResources.createTexture(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.TEXTURES, "particles/Smoke45Frames.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(false, false, false, false, false));

        this.defaultSkyboxCubeMap = systemResources.createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(TextureAssetsInitializer.DEF_CUBE_MAP_TEXTURES), new CubeMapTexture.Properties(true));
        //this.skyboxCubeMap = systemResources.createCubeMapTexture(JGemsStringSource.GetSource.JAR, null, new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay"), "png", new CubeMapTexture.JSTexture2DProperties(true));
        //this.skyboxCubeMap2 = systemResources.createCubeMapTexture(JGemsStringSource.GetSource.JAR, null, new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyNight"), "bmp", new CubeMapTexture.JSTexture2DProperties(true));
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
