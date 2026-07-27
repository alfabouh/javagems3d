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

package workbench.resources.initialization.game;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;

public class WBenchGameEditorTextureAssetsInitializer implements IAssetsInitializer {
    public static ITexture2DProgram hintBACK;
    public static ITexture2DProgram hintUP;
    public static ITexture2DProgram hintFRONT;
    public static ITexture2DProgram hintBOTTOM;
    public static ITexture2DProgram hintRIGHT;
    public static ITexture2DProgram hintLEFT;

    public void load(SystemResources systemResources) {
        WBenchGameEditorTextureAssetsInitializer.hintBACK = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/BACK.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
        WBenchGameEditorTextureAssetsInitializer.hintUP = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/UP.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
        WBenchGameEditorTextureAssetsInitializer.hintFRONT = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/FRONT.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
        WBenchGameEditorTextureAssetsInitializer.hintBOTTOM = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/BOTTOM.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
        WBenchGameEditorTextureAssetsInitializer.hintRIGHT = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/RIGHT.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
        WBenchGameEditorTextureAssetsInitializer.hintLEFT = systemResources.createTexture(new JGemsPathSource(new JGemsPath("/assets/wbench/textures/LEFT.png"), ISource.Source.INSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(true, false));
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
