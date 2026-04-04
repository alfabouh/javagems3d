package api.scripting.coding.env.internal.map.events.mapping.data;

import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.world.render.data.JSPropRenderData;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

@JSCodingClass(binding = "JSPropData", description = "Wrapper for JGemsPropData, representing prop model path and render data.")
public class JSPropData {

    @JSHideFromDoc
    private final JGemsPropData propData;

    @JSCodingConstructor(description = "Wrap existing JGemsPropData.", paramNames = {"propData"})
    public JSPropData(JGemsPropData propData) {
        this.propData = propData;
    }

    @JSCodingConstructor(description = "Create JSPropData with path and render data.", paramNames = {"pathToModel", "propRenderData"})
    public JSPropData(JSPath pathToModel, JSPropRenderData propRenderData) {
        this.propData = new JGemsPropData(pathToModel != null ? new JGemsPathSource(pathToModel.getJavaPath(), ISource.Source.OUTSIDE_JAR) : null, propRenderData.getJavaPropRenderData());
    }

    @JSCodingConstructor(description = "Create JSPropData with only path.", paramNames = {"pathToModel"})
    public JSPropData(JSPath pathToModel) {
        this.propData = new JGemsPropData(pathToModel != null ? new JGemsPathSource(pathToModel.getJavaPath(), ISource.Source.OUTSIDE_JAR)  : null);
    }

    @JSCodingConstructor(description = "Create JSPropData with only render data.", paramNames = {"propRenderData"})
    public JSPropData(JSPropRenderData propRenderData) {
        this.propData = new JGemsPropData(propRenderData.getJavaPropRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java JGemsPropData object.")
    @JSHideFromDoc
    public JGemsPropData getJava() {
        return this.propData;
    }

    @JSCodingFunctionOrMethod(description = "Get the prop model path.")
    public JSPath getPathToModel() {
        return this.propData.pathToModel() != null ? new JSPath(this.propData.pathToModel().getPath()) : null;
    }

    @JSCodingFunctionOrMethod(description = "Get the prop render data.")
    public JSPropRenderData getPropRenderData() {
        return this.propData.propRenderData() != null ? new JSPropRenderData(this.propData.propRenderData()) : null;
    }
}