package workbench.project;

public class Project {
    private final String projectName;
    private final String projectPath;

    public Project(String projectName, String projectPath) {
        this.projectName = projectName;
        this.projectPath = projectPath;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getProjectPath() {
        return this.projectPath;
    }
}
