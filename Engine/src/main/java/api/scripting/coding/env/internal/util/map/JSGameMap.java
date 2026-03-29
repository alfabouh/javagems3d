package api.scripting.coding.env.internal.util.map;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.system.external.mapping.IGameMap;

@JSCodingClass(binding = "JSGameMap", description = "Wrapper for IGameMap interface, exposing map data and current player.")
public class JSGameMap {
    @JSHideFromDoc
    private final IGameMap gameMap;

    @JSHideFromDoc
    private JSPlayer currentJSPlayer;

    @JSHideFromDoc
    public JSGameMap(IGameMap gameMap) {
        this.gameMap = gameMap;
        IPlayer current = gameMap.getCurrentPlayer();
        this.currentJSPlayer = current != null ? () -> current : null;
    }

    @JSCodingFunctionOrMethod(description = "Get map name")
    public String getName() {
        return gameMap.getName();
    }

    @JSCodingFunctionOrMethod(description = "Get map information")
    public String getInformation() {
        return gameMap.getInformation();
    }

    @JSCodingFunctionOrMethod(description = "Get current player wrapper")
    public JSPlayer getCurrentPlayer() {
        IPlayer p = gameMap.getCurrentPlayer();
        if (p == null) return null;
        if (currentJSPlayer == null || currentJSPlayer.getJavaPlayer() != p) {
            currentJSPlayer = () -> p;
        }
        return currentJSPlayer;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java IGameMap object (unsafe)")
    @JSHideFromDoc
    public IGameMap getJavaGameMap() {
        return gameMap;
    }
}