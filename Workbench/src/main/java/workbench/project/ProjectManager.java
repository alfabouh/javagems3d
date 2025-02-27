package workbench.project;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import javagems3d.JGems3D;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.SystemLogging;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.world.WBenchWorld;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public final class ProjectManager {
    private Project currentProject;
    private WBenchWorld world;

    public static final String extension = ".wbpj";
    private static JSONFileManaging.SerializationRules<Project> rules;

    static {
        ProjectManager.rules = new JSONFileManaging.SerializationRules<Project>() {
            @Override
            public String write(Project toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) {
                return toWrite.toString();
            }

            @Override
            public Project read(String readString, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) {
                String[] strings = readString.split("&");
                try {
                    return new Project(strings[1], strings[0]);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read file", e);
                }
            }
        };
    }

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
    public boolean createProject(JGemsPath path, String name) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create(new Pair<>(Project.class, ProjectManager.rules));
            Project project = new Project(JGemsCore.ENG_VER, name);
            jsonFileManaging.writeToFile(project, new File(path.toString()), null);
            this.setCurrentProject(project);
            this.createProjectSystemFiles(path, name);
            Log.get().debug("Created project: " + project);
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
            Log.get().debug("Read project: " + project);
            this.createProjectSystemFiles(path, project.getProjectName());

            WBench.get().openInterface(WBenchOpenGLRenderer.editorInterface);

            return true;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        }
    }

    public Project readMainFile(JGemsPath path) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create(new Pair<>(Project.class, ProjectManager.rules));
            Project project = jsonFileManaging.readFromFile(new File(path.toString()), Project.class, null);
            this.setCurrentProject(project);
            return project;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return null;
        }
    }

    public WBenchWorld getWorld() {
        return this.world;
    }

    public Project getCurrentProject() {
        return this.currentProject;
    }
}