package api.scripting.coding.env.internal.util.world.render.table;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSDirectRenderFabric;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSIndirectRenderFabric;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSRenderFabricI;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

@JSCodingClass(binding = "JSRenderTable", description = "Wrapper for RenderTable. Provides JS access to shaders, render fabrics, and pipeline data.")
public class JSRenderTable {
    @JSCodingField(description = "The underlying Java RenderTable object")
    protected final RenderTable table;

    // DefaultPhysTest shaders
    @JSCodingField(description = "DefaultPhysTest scene shader for direct rendering")
    public static JSShader DEFAULT_SCENE_SHADER = new JSShader(RenderTable.DEFAULT_SCENE_SHADER);
    @JSCodingField(description = "DefaultPhysTest background shader for direct rendering")
    public static JSShader DEFAULT_BACKGROUND_SHADER = new JSShader(RenderTable.DEFAULT_BACKGROUND_SHADER);
    @JSCodingField(description = "DefaultPhysTest sun shadow map shader (direct)")
    public static JSShader DEFAULT_SUN_L_SHADOW_MAP_SHADER = new JSShader(RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER);
    @JSCodingField(description = "DefaultPhysTest point light shadow map shader (direct)")
    public static JSShader DEFAULT_POINT_L_SHADOW_MAP_SHADER = new JSShader(RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER);
    @JSCodingField(description = "DefaultPhysTest transparency shader (direct)")
    public static JSShader DEFAULT_TRANSPARENCY_SHADER = new JSShader(RenderTable.DEFAULT_TRANSPARENCY_SHADER);

    // DefaultPhysTest indirect shaders
    @JSCodingField(description = "DefaultPhysTest scene shader for indirect rendering")
    public static JSShader DEFAULT_SCENE_SHADER_IND = new JSShader(RenderTable.DEFAULT_SCENE_SHADER_IND);
    @JSCodingField(description = "DefaultPhysTest background shader for indirect rendering")
    public static JSShader DEFAULT_BACKGROUND_SHADER_IND = new JSShader(RenderTable.DEFAULT_BACKGROUND_SHADER_IND);
    @JSCodingField(description = "DefaultPhysTest sun shadow map shader (indirect)")
    public static JSShader DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = new JSShader(RenderTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND);
    @JSCodingField(description = "DefaultPhysTest point light shadow map shader (indirect)")
    public static JSShader DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = new JSShader(RenderTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND);
    @JSCodingField(description = "DefaultPhysTest transparency shader (indirect)")
    public static JSShader DEFAULT_TRANSPARENCY_SHADER_IND = new JSShader(RenderTable.DEFAULT_TRANSPARENCY_SHADER_IND);

    // DefaultPhysTest render fabrics
    @JSCodingField(description = "DefaultPhysTest direct scene render fabric")
    public static JSDirectRenderFabric DEFAULT_SCENE_RENDER_FABRIC = new JSDirectRenderFabric((DirectRenderFabric) RenderTable.DEFAULT_SCENE_RENDER_FABRIC);
    @JSCodingField(description = "DefaultPhysTest forward scene render fabric")
    public static JSDirectRenderFabric DEFAULT_SCENE_RENDER_FABRIC_FOR = new JSDirectRenderFabric((DirectRenderFabric) RenderTable.DEFAULT_SCENE_RENDER_FABRIC_FOR);
    @JSCodingField(description = "DefaultPhysTest indirect scene render fabric")
    public static JSIndirectRenderFabric DEFAULT_SCENE_RENDER_FABRIC_IND = new JSIndirectRenderFabric((IndirectRenderFabric) RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND);

    @JSCodingField(description = "DefaultPhysTest transparency render fabric (direct)")
    public static JSDirectRenderFabric DEFAULT_TRANSPARENCY_RENDER_FABRIC = new JSDirectRenderFabric((DirectRenderFabric) RenderTable.DEFAULT_TRANSPARENCY_RENDER_FABRIC);
    @JSCodingField(description = "DefaultPhysTest transparency render fabric (indirect)")
    public static JSIndirectRenderFabric DEFAULT_TRANSPARENCY_RENDER_FABRIC_IND = DEFAULT_SCENE_RENDER_FABRIC_IND;

    @JSCodingField(description = "DefaultPhysTest shadow render fabric (direct)")
    public static JSDirectRenderFabric DEFAULT_SHADOW_RENDER_FABRIC = new JSDirectRenderFabric((DirectRenderFabric) RenderTable.DEFAULT_SHADOW_RENDER_FABRIC);
    @JSCodingField(description = "DefaultPhysTest shadow render fabric (indirect)")
    public static JSIndirectRenderFabric DEFAULT_SHADOW_RENDER_FABRIC_IND = new JSIndirectRenderFabric((IndirectRenderFabric) RenderTable.DEFAULT_SHADOW_RENDER_FABRIC_IND);

    @JSCodingConstructor(description = "Constructs a JS wrapper for a RenderTable", paramNames = {"table"})
    public JSRenderTable(@NotNull RenderTable table) {
        this.table = table;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java RenderTable", paramNames = {})
    public RenderTable getJavaTable() {
        return this.table;
    }

    @JSCodingFunctionOrMethod(description = "Get rendering data for a specific pipeline", paramNames = {"pipeline"})
    public JSRenderTableData getRenderingData(JSPipeline pipeline) {
        RenderTable.Data data = this.table.getRenderingData(pipeline.getJavaPipeline());
        return data != null ? new JSRenderTableData(data) : null;
    }

    @JSCodingFunctionOrMethod(description = "Set match between pipeline, shader, and direct render fabric", paramNames = {"pipeline", "jsShader", "jsDirectFabric"})
    public JSRenderTable setMatch(JSPipeline pipeline, JSShader jsShader, JSDirectRenderFabric jsDirectFabric) {
        this.table.setMatch(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager(), jsDirectFabric.getJavaFabric());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set match between pipeline, shader, and indirect render fabric", paramNames = {"pipeline", "jsShader", "jsIndirectFabric"})
    public JSRenderTable setMatch(JSPipeline pipeline, JSShader jsShader, JSIndirectRenderFabric jsIndirectFabric) {
        this.table.setMatch(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager(), jsIndirectFabric.getJavaFabric());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Replace shader manager for a given pipeline", paramNames = {"pipeline", "jsShader"})
    public JSRenderTable replaceShaderManager(JSPipeline pipeline, JSShader jsShader) {
        this.table.replaceShaderManager(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Replace render fabric for a given pipeline (generic)", paramNames = {"pipeline", "renderFabricI"})
    public JSRenderTable replaceRenderFabric(JSPipeline pipeline, JSRenderFabricI renderFabricI) {
        this.table.replaceRenderData(pipeline.getJavaPipeline(), renderFabricI.getJavaRenderFabric());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Replace render fabric for a given pipeline (direct)", paramNames = {"pipeline", "jsDirectFabric"})
    public JSRenderTable replaceRenderFabric(JSPipeline pipeline, JSDirectRenderFabric jsDirectFabric) {
        this.table.replaceRenderData(pipeline.getJavaPipeline(), jsDirectFabric.getJavaFabric());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Replace render fabric for a given pipeline (indirect)", paramNames = {"pipeline", "jsIndirectFabric"})
    public JSRenderTable replaceRenderFabric(JSPipeline pipeline, JSIndirectRenderFabric jsIndirectFabric) {
        this.table.replaceRenderData(pipeline.getJavaPipeline(), jsIndirectFabric.getJavaFabric());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set redirection from one pipeline to another", paramNames = {"redirection"})
    public JSRenderTable setRedirection(JSRedirections redirection) {
        this.table.setRedirection(redirection.getJavaRedirection());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Remove redirection for a pipeline", paramNames = {"redirection"})
    public JSRenderTable removeRedirection(JSRedirections redirection) {
        this.table.removeRedirection(redirection.getJavaRedirection());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Check if a pipeline is redirected", paramNames = {"pipeline"})
    public boolean isRedirected(JSPipeline pipeline) {
        return this.table.isRedirected(pipeline.getJavaPipeline());
    }

    @JSCodingFunctionOrMethod(description = "Get the final redirected pipeline for a given source", paramNames = {"from"})
    public JSPipeline getRedirection(JSPipeline from) {
        return new JSPipeline(this.table.getRedirection(from.getJavaPipeline()));
    }

    @JSCodingFunctionOrMethod(description = "Get all pipelines currently registered in the table", paramNames = {})
    public Set<JSPipeline> getPipelines() {
        return this.table.getDataMap().keySet().stream().map(JSPipeline::new).collect(Collectors.toSet());
    }
}