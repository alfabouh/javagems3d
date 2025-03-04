package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.GuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextureAssetsInitializer implements IAssetsInitializer {

    public void load(SystemResources systemResources) {
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
