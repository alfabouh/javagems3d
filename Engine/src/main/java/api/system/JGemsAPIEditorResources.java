package api.system;

import api.application.workbench.manager.APIWBenchDataManager;

public final class JGemsAPIEditorResources {
    private APIWBenchDataManager APIWBenchDataManager;

    public JGemsAPIEditorResources() {
    }

    void setEditorResourcesManager(APIWBenchDataManager APIWBenchDataManager) {
        this.APIWBenchDataManager = APIWBenchDataManager;
    }

    public APIWBenchDataManager getEditorResourcesManager() {
        return this.APIWBenchDataManager;
    }
}
