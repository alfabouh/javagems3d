package javagems3d.graphics.objects.rendering.pipeline;

import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultIndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.shadow.DefaultDirectShadowRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.shadow.DefaultIndirectShadowRenderFabric;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class RenderTable implements ICopyable<RenderTable> {
    public static JGemsShaderManager DEFAULT_SCENE_SHADER = null;
    public static JGemsShaderManager DEFAULT_BACKGROUND_SHADER = null;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = null;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = null;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = null;

    public static JGemsShaderManager DEFAULT_SCENE_SHADER_IND = null;
    public static JGemsShaderManager DEFAULT_BACKGROUND_SHADER_IND = null;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = null;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = null;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = null;

    public static void SET_DEFAULT_SHADERS_INDIRECT(@NotNull JGemsShaderManager DEFAULT_SCENE_SHADER_IND, @NotNull JGemsShaderManager DEFAULT_BACKGROUND_SHADER_IND, @NotNull JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, @NotNull JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, @NotNull JGemsShaderManager DEFAULT_BLENDED_TRANSPARENCY_SHADER_IND) {
        RenderTable.DEFAULT_SCENE_SHADER_IND = DEFAULT_SCENE_SHADER_IND;
        RenderTable.DEFAULT_BACKGROUND_SHADER_IND = DEFAULT_BACKGROUND_SHADER_IND;
        RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND;
        RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND;
        RenderTable.DEFAULT_TRANSPARENCY_SHADER_IND = DEFAULT_BLENDED_TRANSPARENCY_SHADER_IND;
    }

    public static void SET_DEFAULT_SHADERS_DIRECT(@NotNull JGemsShaderManager DEFAULT_SCENE_SHADER, @NotNull JGemsShaderManager DEFAULT_BACKGROUND_SHADER, @NotNull JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER, @NotNull JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER, @NotNull JGemsShaderManager DEFAULT_BLENDED_TRANSPARENCY_SHADER) {
        RenderTable.DEFAULT_SCENE_SHADER = DEFAULT_SCENE_SHADER;
        RenderTable.DEFAULT_BACKGROUND_SHADER = DEFAULT_BACKGROUND_SHADER;
        RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER = DEFAULT_SUN_L_SHADOW_MAP_SHADER;
        RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER = DEFAULT_POINT_L_SHADOW_MAP_SHADER;
        RenderTable.DEFAULT_TRANSPARENCY_SHADER = DEFAULT_BLENDED_TRANSPARENCY_SHADER;
    }

    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC = new DefaultDirectRenderFabric(Stage.DEFERRED_DIRECT);
    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC_FOR = new DefaultDirectRenderFabric(Stage.FORWARD);
    public static IRenderFabric DEFAULT_SCENE_RENDER_FABRIC_IND = new DefaultIndirectRenderFabric(Stage.DEFERRED_INDIRECT, IndirectRenderFabric.DEFAULT_FUNC);

    public static IRenderFabric DEFAULT_TRANSPARENCY_RENDER_FABRIC = RenderTable.DEFAULT_SCENE_RENDER_FABRIC;
    public static IRenderFabric DEFAULT_TRANSPARENCY_RENDER_FABRIC_IND = RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND;

    public static IRenderFabric DEFAULT_SHADOW_RENDER_FABRIC = new DefaultDirectShadowRenderFabric();
    public static IRenderFabric DEFAULT_SHADOW_RENDER_FABRIC_IND = new DefaultIndirectShadowRenderFabric(IndirectRenderFabric.DEFAULT_FUNC);

    private final Map<Pipeline, Data> dataMap;
    private final Map<Pipeline, Pipeline> redirectionMap;

    private RenderTable(@NotNull Map<Pipeline, Data> dataMap) {
        this.dataMap = dataMap;
        this.redirectionMap = new HashMap<>();
    }

    protected RenderTable() {
        this(new EnumMap<>(Pipeline.class));
    }

    public static RenderTable getIndirect() {
        return new RenderTable().setDefaultTableValues(true);
    }

    public static RenderTable getDirect() {
        return new RenderTable().setDefaultTableValues(false);
    }

    public RenderTable setDefaultTableValues(boolean indirect) {
        if (indirect) {
            this.setMatch(Pipeline.SCENE, RenderTable.DEFAULT_SCENE_SHADER_IND, RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND);
            this.setMatch(Pipeline.BACKGROUND, RenderTable.DEFAULT_BACKGROUND_SHADER_IND, RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND);
            this.setMatch(Pipeline.POINT_LIGHT_SHADOW_MAP, RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC_IND);
            this.setMatch(Pipeline.SUN_LIGHT_SHADOW_MAP, RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC_IND);
            this.setMatch(Pipeline.TRANSPARENCY, RenderTable.DEFAULT_TRANSPARENCY_SHADER_IND, RenderTable.DEFAULT_TRANSPARENCY_RENDER_FABRIC_IND);
        } else {
            this.setMatch(Pipeline.SCENE, RenderTable.DEFAULT_SCENE_SHADER, RenderTable.DEFAULT_SCENE_RENDER_FABRIC);
            this.setMatch(Pipeline.BACKGROUND, RenderTable.DEFAULT_BACKGROUND_SHADER, RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND);
            this.setMatch(Pipeline.POINT_LIGHT_SHADOW_MAP, RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC);
            this.setMatch(Pipeline.SUN_LIGHT_SHADOW_MAP, RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER, RenderTable.DEFAULT_SHADOW_RENDER_FABRIC);
            this.setMatch(Pipeline.TRANSPARENCY, RenderTable.DEFAULT_TRANSPARENCY_SHADER, RenderTable.DEFAULT_TRANSPARENCY_RENDER_FABRIC);
        }
        return this;
    }

    public RenderTable removeRedirection(@NotNull Redirections redirection) {
        this.getRedirectionMap().remove(redirection.getFrom(), redirection.getTo());
        return this;
    }

    public RenderTable setRedirection(@NotNull Redirections redirection) {
        this.getRedirectionMap().put(redirection.getFrom(), redirection.getTo());
        return this;
    }

    public RenderTable setMatchNull(@NotNull Pipeline pipeline) {
        this.getDataMap().replace(pipeline, null);
        return this;
    }

    public RenderTable replaceShaderManager(@NotNull Pipeline pipeline, @NotNull JGemsShaderManager shaderManager) {
        RenderTable.Data data = this.dataMap.get(pipeline);
        if (data == null) {
            throw new JGemsRuntimeException("Couldn't replace shader to NULL pipeline data: " + pipeline);
        }
        this.setMatch(pipeline, new Data(shaderManager, data.getRenderFabric()));
        return this;
    }

    public RenderTable replaceRenderData(@NotNull Pipeline pipeline, @NotNull IRenderFabric renderFabric) {
        RenderTable.Data data = this.dataMap.get(pipeline);
        if (data == null) {
            throw new JGemsRuntimeException("Couldn't replace renderFabric to NULL pipeline data: " + pipeline);
        }
        this.setMatch(pipeline, new Data(data.getShaderManager(), renderFabric));
        return this;
    }

    public RenderTable setMatch(@NotNull Pipeline pipeline, @NotNull JGemsShaderManager shaderManager, @NotNull IRenderFabric renderFabric) {
        this.setMatch(pipeline, new Data(shaderManager, renderFabric));
        return this;
    }

    public RenderTable setMatch(@NotNull Pipeline pipeline, @Nullable Data data) {
        if (data != null) {
        IRenderFabric renderFabric = data.getRenderFabric();
            switch (renderFabric.getRenderingType()) {
                case DIRECT: {
                    if (!(renderFabric instanceof DirectRenderFabric)) {
                        throw new JGemsRuntimeException("If you are using DIRECT RenderFabric, it should be extended from DirectRenderFabric");
                    }
                    break;
                }
                case INDIRECT: {
                    if (!(renderFabric instanceof IndirectRenderFabric)) {
                        throw new JGemsRuntimeException("If you are using INDIRECT RenderFabric, it should be extended from IndirectRenderFabric");
                    }
                    break;
                }
            }
        }
        this.dataMap.put(pipeline, data);
        return this;
    }

    public Set<IRenderFabric> getRenderFabricsSet() {
        return this.getDataMap().values().stream().filter(Objects::nonNull).map(Data::getRenderFabric).map(e -> (IRenderFabric) e).collect(Collectors.toSet());
    }

    public Pipeline getRedirection(@NotNull Pipeline from) {
        Pipeline to = this.getRedirectionMap().get(from);
        if (to == null) {
            return from;
        }
        return this.getRedirection(to);
    }

    public boolean isRedirected(@NotNull Pipeline from) {
        return this.getRedirectionMap().get(from) != null;
    }

    public boolean isRedirected(Redirections redirection) {
        return this.getRedirectionMap().get(redirection.getFrom()) == redirection.getTo();
    }

    public Map<Pipeline, Pipeline> getRedirectionMap() {
        return this.redirectionMap;
    }

    public @Nullable Data getRenderingData(@NotNull Pipeline pipeline) {
        Pipeline redirected = this.getRedirection(pipeline);
        return this.dataMap.get(redirected);
    }

    public boolean validate(Pipeline pipeline) {
        return this.dataMap.containsKey(pipeline) && this.dataMap.get(pipeline) != null;
    }

    protected Map<Pipeline, Data> getDataMap() {
        return new EnumMap<>(this.dataMap);
    }

    @Override
    public RenderTable copy() {
        return new RenderTable(new HashMap<>(this.getDataMap()));
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

        @SuppressWarnings("all")
        public @NotNull <T extends IRenderFabric> T getRenderFabric() {
            try {
                return (T) this.renderFabric;
            } catch (ClassCastException e) {
                throw new JGemsRuntimeException(e);
            }
        }
    }
}
