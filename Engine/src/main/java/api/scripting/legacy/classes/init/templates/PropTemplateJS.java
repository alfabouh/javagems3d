package api.scripting.legacy.classes.init.templates;

import api.scripting.legacy.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Template to create new prop object", priority = JSTypeDoc.Priority.MED)
public final class PropTemplateJS extends TemplateJS {
    public PropTemplateJS(String groupName, String name) {
        super(groupName, name);
    }
}
