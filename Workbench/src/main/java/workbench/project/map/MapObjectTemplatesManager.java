package workbench.project.map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.project.managing.instances.mapping.GameResourceSkyboxAsset;

public class MapObjectTemplatesManager {
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> entities;
    private final MapObjectTemplatesFolder<WBenchObjectTemplate> props;
    private final MapObjectTemplatesFolder<WBenchMarkerTemplate> markers;
    private final BiMap<String, ICubeMapProgram> skyBoxes;

    public MapObjectTemplatesManager() {
        this.entities = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.props = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.markers = new MapObjectTemplatesFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.skyBoxes = HashBiMap.create();
    }

    public void putPropWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getProps().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putEntityWithRawPath(@NotNull String path, @NotNull WBenchObjectTemplate wBenchObjectTemplate) {
        this.getEntities().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void putMarkerWithRawPath(@NotNull String path, @NotNull WBenchMarkerTemplate wBenchObjectTemplate) {
        this.getMarkers().putObjectInside(path, wBenchObjectTemplate, MapObjectTemplatesFolder::new);
    }

    public void addSkyBox(String name, ICubeMapProgram cubeMapProgram) {
        this.getSkyBoxes().put(name, cubeMapProgram);
    }

    public void clear() {
        this.getEntities().reset();
        this.getProps().reset();
        this.getMarkers().reset();
        this.getSkyBoxes().clear();
    }

    public BiMap<String, ICubeMapProgram> getSkyBoxes() {
        return this.skyBoxes;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getEntities() {
        return this.entities;
    }

    public MapObjectTemplatesFolder<WBenchObjectTemplate> getProps() {
        return this.props;
    }

    public MapObjectTemplatesFolder<WBenchMarkerTemplate> getMarkers() {
        return this.markers;
    }

    public static class SkyBoxWrapperContainer {
        private final String name;
        private final String textureUPPath;
        private final String textureBOTTOMPath;
        private final String textureFRONTPath;
        private final String textureBACKPath;
        private final String textureLEFTPath;
        private final String textureRIGHTPath;
        private ICubeMapProgram cubeMapProgram;

        public SkyBoxWrapperContainer(String name, String textureUPPath, String textureBOTTOMPath, String textureFRONTPath, String textureBACKPath, String textureLEFTPath, String textureRIGHTPath) {
            this.name = name;
            this.textureUPPath = textureUPPath;
            this.textureBOTTOMPath = textureBOTTOMPath;
            this.textureFRONTPath = textureFRONTPath;
            this.textureBACKPath = textureBACKPath;
            this.textureLEFTPath = textureLEFTPath;
            this.textureRIGHTPath = textureRIGHTPath;
        }

        public String getName() {
            return this.name;
        }

        public String getTextureUPPath() {
            return this.textureUPPath;
        }

        public String getTextureBOTTOMPath() {
            return this.textureBOTTOMPath;
        }

        public String getTextureFRONTPath() {
            return this.textureFRONTPath;
        }

        public String getTextureBACKPath() {
            return this.textureBACKPath;
        }

        public String getTextureLEFTPath() {
            return this.textureLEFTPath;
        }

        public String getTextureRIGHTPath() {
            return this.textureRIGHTPath;
        }

        public ICubeMapProgram getCubeMapProgram() {
            return this.cubeMapProgram;
        }

        public void setCubeMapProgram(ICubeMapProgram cubeMapProgram) {
            this.cubeMapProgram = cubeMapProgram;
        }
    }
}
