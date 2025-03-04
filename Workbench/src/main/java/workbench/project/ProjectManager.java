package workbench.project;

import com.google.gson.JsonSyntaxException;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.scene.renderer.IProjectActionsCallback;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

import java.io.File;

public final class ProjectManager {
    private Project currentProject;
    private WBenchWorld world;

    public static final String extension = ".wbpj";

    public ProjectManager() {
        this.currentProject = null;
        this.world = null;
    }

    private void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public void setWorld(@NotNull WBenchWorld world) {
        this.world = world;
    }

    @SuppressWarnings("all")
    public boolean createProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            Project project = new Project(JGemsCore.ENG_VER, name);
            jsonFileManaging.writeToFile(project, new File(path.toString()), null);
            this.setCurrentProject(project);
            this.createProjectSystemFiles(absPath, name);
            Log.get().debug("Created project: " + project);

            this.initWorkingSpace(this.getWorld(), WBenchOpenGLRenderer.getEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            Log.get().showExceptionDialog("Internal error! Couldn't create project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        }
    }

    @SuppressWarnings("all")
    private void createProjectSystemFiles(JGemsPath path, String name) {
        File resources = new File(new JGemsPath(path, "resources").getFullPath());
        File compiled = new File(new JGemsPath(path, "compiled").getFullPath());
        File scripts = new File(new JGemsPath(path, "scripts").getFullPath());

        resources.mkdirs();
        compiled.mkdirs();
        scripts.mkdirs();
    }

    public void closeProject(boolean total) {
        Log.get().info("Closing project " + this.getCurrentProject());
        if (this.getCurrentProject() != null) {
            this.destroyLocalResources(this.getCurrentProject());
            if (!total) {
                this.closeWorkingSpace(WBenchOpenGLRenderer.getProjectInterface());
            }
            this.currentProject = null;
        }
        Log.get().info("Project successfully closed");
    }

    @SuppressWarnings("all")
    public boolean openProject(JGemsPath path) {
        try {
            File projectFolder = new File(path.getFullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid path: " + path);
            }

            File[] files = projectFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wbpj"));
            if (files == null || files.length != 1) {
                throw new JGemsIOException("Couldn't find project .wbpj file: " + path);
            }

            File projectFile = files[0];
            Project project = this.readMainFile(new JGemsPath(projectFile.getPath()));
            if (project == null) {
                return false;
            }
            this.createProjectSystemFiles(path, project.getProjectName());
            Log.get().debug("Opened project: " + project);

            this.initWorkingSpace(this.getWorld(), WBenchOpenGLRenderer.getEditorInterface());
            this.initLocalResources(project);

            return true;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        }
    }

    public Project readMainFile(JGemsPath path) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            Project project = jsonFileManaging.readFromFile(new File(path.toString()), Project.class, null);
            this.setCurrentProject(project);
            return project;
        } catch (JGemsIOException | JsonSyntaxException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return null;
        }
    }

    private void initLocalResources(Project project) {
        WBench.get().getResourceManager().initLocalResources();
        WBench.get().getResourceManager().loadLocalResources();
        WBenchResourceManager.createLocalShaders();
        this.getWorld().onWorldStart();
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onOpeningProject(WBench.get().getResourceManager(), project);
    }

    private void destroyLocalResources(Project project) {
        ((IProjectActionsCallback) WBench.get().getScreen().getScene().getSceneRenderer()).onClosingProject(WBench.get().getResourceManager(), project);
        this.getWorld().onWorldEnd();
        WBench.get().getResourceManager().destroyLocalResources();
    }

    private void initWorkingSpace(WBenchWorld world, DearUIInterface dearUIInterface) {
        world.setCamera(new ControlledCamera(WBench.get().getControllerDispatcher().getCurrentController(), new Vector3f(), new Vector3f()));
        WBench.get().openInterface(dearUIInterface);
    }

    private void closeWorkingSpace(DearUIInterface dearUIInterface) {
        world.setCamera(null);
        WBench.get().openInterface(dearUIInterface);
    }

    public WBenchWorld getWorld() {
        return this.world;
    }

    public Project getCurrentProject() {
        return this.currentProject;
    }
}