package api.scripting.coding.env.internal.game.init.events.resources;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFont;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFontStyles;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshBuffer;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshGroup;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSOggSound;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSSoundFormats;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2DProperties;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureCubeMap;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureCubeMapProperties;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.FontCode;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

import java.awt.*;

@JSCodingClass(binding = "JSInitAssetsEvent", description = "Event used to load and register game assets (textures, models, sounds, fonts).")
public class JSInitAssetsEvent implements JSEventI {

    @JSCodingField(description = "Access to system resource manager for asset creation.")
    public JSSystemResources systemResources;

    @JSHideFromDoc private IAssetsInitializer assetsInitializer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSInitAssetsEvent() {
    }

    @JSHideFromDoc
    public JSInitAssetsEvent(IAssetsInitializer assetsInitializer, JSSystemResources systemResources) {
        this.systemResources = systemResources;
        this.assetsInitializer = assetsInitializer;
    }

    @JSCodingFunctionOrMethod(description = "Load OGG sound from file.", paramNames = {"pathToOggSound", "format"})
    public JSOggSound createOggSound(JSPath pathToOggSound, JSSoundFormats format) {
        return new JSOggSound(this.systemResources.getJavaSystemResources().createSoundBuffer(new JGemsPathSource(pathToOggSound.getJavaPath(), ISource.Source.OUTSIDE_JAR), format.getValue()));
    }

    @JSCodingFunctionOrMethod(description = "Load GLTF2 mesh buffer.", paramNames = {"pathToGLTF2Model", "keepNodesInMemory"})
    public JSMeshBuffer createGLTF2MeshBuffer(JSPath pathToGLTF2Model, boolean keepNodesInMemory) {
        return new JSMeshBuffer(this.systemResources.getJavaSystemResources().createMeshBuffer(new JGemsPathSource(pathToGLTF2Model.getJavaPath(), ISource.Source.OUTSIDE_JAR), keepNodesInMemory));
    }

    @JSCodingFunctionOrMethod(description = "Load GLTF2 mesh buffer with default settings.", paramNames = {"pathToGLTF2Model"})
    public JSMeshBuffer createGLTF2MeshBuffer(JSPath pathToGLTF2Model) {
        return this.createGLTF2MeshBuffer(pathToGLTF2Model, false);
    }

    @JSCodingFunctionOrMethod(description = "Load GLTF2 mesh group.", paramNames = {"pathToGLTF2Model", "keepNodesInMemory"})
    public JSMeshGroup createGLTF2MeshGroup(JSPath pathToGLTF2Model, boolean keepNodesInMemory) {
        return new JSMeshGroup(this.systemResources.getJavaSystemResources().createMeshGroup(new JGemsPathSource(pathToGLTF2Model.getJavaPath(), ISource.Source.OUTSIDE_JAR), keepNodesInMemory));
    }

    @JSCodingFunctionOrMethod(description = "Load GLTF2 mesh group with default settings.", paramNames = {"pathToGLTF2Model"})
    public JSMeshGroup createGLTF2MeshGroup(JSPath pathToGLTF2Model) {
        return this.createGLTF2MeshGroup(pathToGLTF2Model, false);
    }

    @JSCodingFunctionOrMethod(description = "Load 2D texture with custom properties.", paramNames = {"pathToTexture", "textureProperties"})
    public JSTexture2D createTexture2D(JSPath pathToTexture, JSTexture2DProperties textureProperties) {
        return new JSTexture2D(this.systemResources.getJavaSystemResources().createTexture(new JGemsPathSource(pathToTexture.getJavaPath(), ISource.Source.OUTSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties(textureProperties.mipMap(), textureProperties.linearFiltration(), textureProperties.shouldBeRepeated(), textureProperties.anisotropicFiltration(), textureProperties.qualityAffected())));
    }

    @JSCodingFunctionOrMethod(description = "Load 2D texture with default properties.", paramNames = {"pathToTexture"})
    public JSTexture2D createTexture2D(JSPath pathToTexture) {
        return new JSTexture2D(this.systemResources.getJavaSystemResources().createTexture(new JGemsPathSource(pathToTexture.getJavaPath(), ISource.Source.OUTSIDE_JAR), ResourceManager.DEFAULT_TEXTURE(), new ImageTexture.Properties()));
    }

    @JSCodingFunctionOrMethod(description = "Load cube map texture with custom properties.", paramNames = {"pathToTextureUP", "pathToTextureDOWN", "pathToTextureFRONT", "pathToTextureBACK", "pathToTextureLEFT", "pathToTextureRIGHT", "cubeMapProperties"})
    public JSTextureCubeMap createTextureCubeMap(JSPath pathToTextureUP, JSPath pathToTextureDOWN, JSPath pathToTextureFRONT, JSPath pathToTextureBACK, JSPath pathToTextureLEFT, JSPath pathToTextureRIGHT, JSTextureCubeMapProperties cubeMapProperties) {
        final JGemsPathSource up = new JGemsPathSource(pathToTextureUP.getJavaPath(), ISource.Source.OUTSIDE_JAR);
        final JGemsPathSource down = new JGemsPathSource(pathToTextureDOWN.getJavaPath(), ISource.Source.OUTSIDE_JAR);
        final JGemsPathSource front = new JGemsPathSource(pathToTextureFRONT.getJavaPath(), ISource.Source.OUTSIDE_JAR);
        final JGemsPathSource back = new JGemsPathSource(pathToTextureBACK.getJavaPath(), ISource.Source.OUTSIDE_JAR);
        final JGemsPathSource left = new JGemsPathSource(pathToTextureLEFT.getJavaPath(), ISource.Source.OUTSIDE_JAR);
        final JGemsPathSource right = new JGemsPathSource(pathToTextureRIGHT.getJavaPath(), ISource.Source.OUTSIDE_JAR);

        return new JSTextureCubeMap(this.systemResources.getJavaSystemResources().createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(new ICubeMapProgram.CMTextures(front, back, up, down, left, right)), new CubeMapTexture.Properties(cubeMapProperties.linearFiltration())));
    }

    @JSCodingFunctionOrMethod(description = "Load cube map texture with default properties.", paramNames = {"pathToTextureUP", "pathToTextureDOWN", "pathToTextureFRONT", "pathToTextureBACK", "pathToTextureLEFT", "pathToTextureRIGHT"})
    public JSTextureCubeMap createTextureCubeMap(JSPath pathToTextureUP, JSPath pathToTextureDOWN, JSPath pathToTextureFRONT, JSPath pathToTextureBACK, JSPath pathToTextureLEFT, JSPath pathToTextureRIGHT) {
        return this.createTextureCubeMap(pathToTextureUP, pathToTextureDOWN, pathToTextureFRONT, pathToTextureBACK, pathToTextureLEFT, pathToTextureRIGHT, new JSTextureCubeMapProperties());
    }

    @JSCodingFunctionOrMethod(description = "Load TTF font from file.", paramNames = {"pathToTTFFont", "fontStyles", "fontSize"})
    public JSFont createTTFFont(JSPath pathToTTFFont, JSFontStyles fontStyles, int fontSize) {
        final Font font = SystemResources.createFontFromFile(new JGemsPathSource(pathToTTFFont.getJavaPath(), ISource.Source.OUTSIDE_JAR));
        return new JSFont(new JGemsGuiFont(this.systemResources.getJavaSystemResources(), font.deriveFont(fontStyles.getValue(), fontSize), FontCode.Window));
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitAssetsEvent";
    }
}
