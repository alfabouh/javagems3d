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

package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public static ImageTexture DEFAULT;

    public void load(GameResources gameResources) {
        TextureAssetsInitializer.DEFAULT = gameResources.createTexture(null, new JGemsPath(JGems3D.DEF_PATHS.TEXTURES, "default.png"), new ImageTexture.Properties(false, false, true, false, false));
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
