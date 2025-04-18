package api.scripting.classes.init.templates;

import api.scripting.doc.annotations.JSMethodDoc;

public abstract class TemplateJS {
    private final String groupName;
    private final String name;

    public TemplateJS(String groupName, String name) {
        this.groupName = groupName;
        this.name = name;
    }

    @JSMethodDoc(description = "Object's group", args = {})
    public String getGroupName() {
        return this.groupName;
    }

    @JSMethodDoc(description = "Object's name", args = {})
    public String getName() {
        return this.name;
    }
}
