package javagems3d.graphics.objects.rendering.pipeline;

import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.shadow.DefaultDirectShadowRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.shadow.DefaultIndirectShadowRenderFabric;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RenderTable {
    public static JGemsShaderManager DEFAULT_SCENE_SHADER = JGemsResourceManager.globalShaderAssets.world_gbuffer;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_sun;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_plight;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = JGemsResourceManager.globalShaderAssets.weighted_oit;

    public static JGemsShaderManager DEFAULT_SCENE_SHADER_IND = JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_sun_indirect;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_plight;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = JGemsResourceManager.globalShaderAssets.weighted_oit;

    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC = new DefaultDirectRenderFabric(Stage.DEFERRED_DIRECT);
    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC_FOR = new DefaultDirectRenderFabric(Stage.FORWARD);
    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC_IND = new DefaultIndirectRenderFabric(Stage.DEFERRED_INDIRECT, IndirectRenderFabric.defaultFuncScene);
    public static IRenderFabric DEFAULT_SHADOW_RENDER_FABRIC = new DefaultDirectShadowRenderFabric();
    public static IRenderFabric DEFAULT_SHADOW_RENDER_FABRIC_IND = new DefaultIndirectShadowRenderFabric(IndirectRenderFabric.defaultFuncShadow);

    private final Map<Pipeline, Data> dataMap;

    public RenderTable() {
        this(RenderTable.DEFAULT_SCENE_SHADER_IND, RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND);
    }

    public RenderTable(@NotNull JGemsShaderManager sceneStageShaderManager, @NotNull IRenderFabric sceneStageRenderFabric) {
        this.dataMap = new EnumMap<>(Pipeline.class);
        this.setDefaults(sceneStageShaderManager, sceneStageRenderFabric);
        if (this.dataMap.containsValue(null)) {
            throw new JGemsNullException("Shading table contains NULL value!");
        }
    }

    public void setDefaults(@NotNull JGemsShaderManager sceneStageShaderManager, @NotNull IRenderFabric sceneStageRenderFabric) {
        this.setSceneRenderMatch(sceneStageShaderManager, sceneStageRenderFabric);
        this.setPointLightShadowRenderMatch(RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC_IND);
        this.setSunLightShadowRenderMatch(RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC_IND);
        this.setTransparencyRenderMatch(RenderTable.DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    @SuppressWarnings("all")
    public RenderTable setSceneRenderMatch(@NotNull JGemsShaderManager sceneStageShaderManager, @NotNull IRenderFabric sceneStageRenderFabric) {
        this.setMatch(Pipeline.SCENE, new Data(sceneStageShaderManager, sceneStageRenderFabric));
        return this;
    }

    @SuppressWarnings("all")
    public RenderTable setPointLightShadowRenderMatch(@NotNull JGemsShaderManager pLightStageShaderManager, @NotNull IRenderFabric pLightStageRenderFabric) {
        this.setMatch(Pipeline.POINT_LIGHT_SHADOW_MAP, new Data(pLightStageShaderManager, pLightStageRenderFabric));
        return this;
    }

    @SuppressWarnings("all")
    public RenderTable setSunLightShadowRenderMatch(@NotNull JGemsShaderManager sLightStageShaderManager, @NotNull IRenderFabric sLightStageRenderFabric) {
        this.setMatch(Pipeline.SUN_LIGHT_SHADOW_MAP, new Data(sLightStageShaderManager, sLightStageRenderFabric));
        return this;
    }

    @SuppressWarnings("all")
    public RenderTable setTransparencyRenderMatch(@NotNull JGemsShaderManager sLightStageShaderManager) {
        this.setMatch(Pipeline.TRANSPARENCY, new Data(sLightStageShaderManager, null));
        return this;
    }

    protected void setMatch(@NotNull Pipeline pipeline, @NotNull Data data) {
        IRenderFabric renderFabric = data.getRenderFabric();
        if (renderFabric != null) {
            switch (renderFabric.getRenderingType()) {
                case DIRECT: {
                    if (!(renderFabric instanceof DirectRenderFabric)) {
                        throw new JGemsRuntimeException("If you are using DIRECT RenderFabric, it should be extended from DirectRenderFabric!");
                    }
                    break;
                }
                case INDIRECT: {
                    if (!(renderFabric instanceof IndirectRenderFabric)) {
                        throw new JGemsRuntimeException("If you are using INDIRECT RenderFabric, it should be extended from IndirectRenderFabric!");
                    }
                    break;
                }
            }
        }
        this.dataMap.put(pipeline, data);
    }

    public Set<IRenderFabric> getRenderFabricsSet() {
        return this.getDataMap().values().stream().map(Data::getRenderFabric).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public @NotNull JGemsShaderManager getShaderManager(Pipeline pipeline) {
        return this.getRenderingData(pipeline).getShaderManager();
    }

    public @Nullable IRenderFabric getRenderFabric(Pipeline pipeline) {
        return this.getRenderingData(pipeline).getRenderFabric();
    }

    public @NotNull  Data getRenderingData(Pipeline pipeline) {
        return this.dataMap.get(pipeline);
    }

    public Map<Pipeline, Data> getDataMap() {
        return new EnumMap<>(this.dataMap);
    }

    public static class Data {
        private final JGemsShaderManager shaderManager;
        private final IRenderFabric renderFabric;

        public Data(@NotNull JGemsShaderManager shaderManager, @Nullable IRenderFabric renderFabric) {
            this.shaderManager = shaderManager;
            this.renderFabric = renderFabric;
        }

        public @NotNull JGemsShaderManager getShaderManager() {
            return this.shaderManager;
        }

        public @Nullable IRenderFabric getRenderFabric() {
            return this.renderFabric;
        }
    }
}
