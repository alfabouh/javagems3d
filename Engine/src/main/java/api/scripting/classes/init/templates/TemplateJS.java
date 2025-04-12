package api.scripting.classes.init.templates;

public abstract class TemplateJS {
    private final String groupName;
    private final String name;

    public TemplateJS(String groupName, String name) {
        this.groupName = groupName;
        this.name = name;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getName() {
        return this.name;
    }
}
