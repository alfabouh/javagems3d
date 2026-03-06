package api.scripting.legacy;

import api.scripting.legacy.classes.init.templates.EntityTemplateJS;
import api.scripting.legacy.classes.init.templates.PropTemplateJS;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;

import java.util.HashMap;
import java.util.Map;

public final class JGemsAPIScriptingManaging {
    private final Map<EntityTemplateJS, EntityRenderData> entityRenderDataMap;
    private final Map<PropTemplateJS, PropRenderData> propRenderDataMap;

    public JGemsAPIScriptingManaging() {
        this.propRenderDataMap = new HashMap<>();
        this.entityRenderDataMap = new HashMap<>();
    }

    public Map<EntityTemplateJS, EntityRenderData> getEntityRenderDataMap() {
        return this.entityRenderDataMap;
    }

    public Map<PropTemplateJS, PropRenderData> getPropRenderDataMap() {
        return this.propRenderDataMap;
    }
}