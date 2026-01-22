package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import org.joml.Vector2i;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.MapProjectPreview;
import workbench.graphics.scene.ui.game.editor.instances.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.TextureAssetPreview;
import workbench.graphics.scene.ui.game.editor.scenes.ScenePreviewMapG;
import workbench.graphics.scene.ui.game.editor.scenes.ScenePreviewModelG;
import workbench.graphics.scene.ui.game.editor.scenes.ScenePreviewPropG;
import workbench.graphics.scene.ui.game.editor.scenes.ScenePreviewTextureG;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.GameResourceModelAsset;
import workbench.project.managing.instances.GameResourcePropObjectAsset;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActionsInterfaceComponentG {
    private final ScenePreviewMapG scenePreviewMapG;
    private final ScenePreviewModelG scenePreviewModelG;
    private final ScenePreviewPropG scenePreviewPropG;
    private final ScenePreviewTextureG scenePreviewTextureG;

    public ActionsInterfaceComponentG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.scenePreviewMapG = new ScenePreviewMapG(resourcesInterfaceComponentG);
        this.scenePreviewModelG = new ScenePreviewModelG(resourcesInterfaceComponentG);
        this.scenePreviewPropG = new ScenePreviewPropG(resourcesInterfaceComponentG);
        this.scenePreviewTextureG = new ScenePreviewTextureG(resourcesInterfaceComponentG);
    }

    public void actionsContent() {
        this.scenePreviewMapG.render();
        this.scenePreviewModelG.render();
        this.scenePreviewPropG.render();
        this.scenePreviewTextureG.render();
    }

    public ScenePreviewMapG getScenePreviewMapG() {
        return this.scenePreviewMapG;
    }

    public ScenePreviewModelG getScenePreviewModelG() {
        return this.scenePreviewModelG;
    }

    public ScenePreviewPropG getScenePreviewPropG() {
        return this.scenePreviewPropG;
    }

    public ScenePreviewTextureG getScenePreviewTextureG() {
        return this.scenePreviewTextureG;
    }
}
