package api.application.workbench;

import api.application.workbench.manager.IAPIWBenchDataManager;

public interface IWorkBenchSetup {
    void setupEditorResources(IAPIWBenchDataManager manager);
}