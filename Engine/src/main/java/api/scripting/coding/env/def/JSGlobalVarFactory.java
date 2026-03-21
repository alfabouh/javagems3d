package api.scripting.coding.env.def;

import javagems3d.system.service.annotations.RequireEmptyConstructor;

@RequireEmptyConstructor
public interface JSGlobalVarFactory <T> {
    @JSHideFromDoc T newGlobalVar();
    @JSHideFromDoc String getVarName();
}
