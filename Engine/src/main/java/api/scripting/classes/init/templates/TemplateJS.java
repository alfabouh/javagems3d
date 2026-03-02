package api.scripting.classes.init.templates;

import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Abstract template", priority = JSTypeDoc.Priority.MED)
public abstract class TemplateJS {
    private final String groupName;
    private final String name;

    public TemplateJS(String groupName, String name) {
        this.groupName = groupName;
        this.name = name;
    }

    @JSMethodDoc(description = "Object's util", args = {})
    public String getGroupName() {
        return this.groupName;
    }

    @JSMethodDoc(description = "Object's name", args = {})
    public String getName() {
        return this.name;
    }
}
