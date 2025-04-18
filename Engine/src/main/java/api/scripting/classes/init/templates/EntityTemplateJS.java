package api.scripting.classes.init.templates;

import api.scripting.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Template to create new entity object", priority = JSTypeDoc.Priority.MED)
public final class EntityTemplateJS extends TemplateJS {
    public EntityTemplateJS(String groupName, String name) {
        super(groupName, name);
    }
}