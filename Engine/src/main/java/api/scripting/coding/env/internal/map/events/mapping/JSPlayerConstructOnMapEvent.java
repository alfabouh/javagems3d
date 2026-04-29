package api.scripting.coding.env.internal.map.events.mapping;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.mapping.player.JSSpawnPlayerTranslateData;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;

import java.util.Collection;

@JSCodingClass(binding = "JSPlayerConstructOnMapEvent", description = "Event triggered when a player is constructed on the map.")
public class JSPlayerConstructOnMapEvent implements JSEventCancellableI {
    @JSHideFromDoc
    private JSPhysicsWorld jsWorld;

    @JSHideFromDoc
    private Collection<JSSpawnPlayerTranslateData> jsSpawnDataList;

    @JSHideFromDoc
    private JSPlayer jsPlayer;

    @JSHideFromDoc
    private JSEntityRenderData jsRenderData;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSPlayerConstructOnMapEvent() {
    }

    @JSHideFromDoc
    public JSPlayerConstructOnMapEvent(JSPhysicsWorld jsWorld, Collection<JSSpawnPlayerTranslateData> jsSpawnDataList) {
        this.jsWorld = jsWorld;
        this.jsSpawnDataList = jsSpawnDataList;
    }

    @JSCodingFunctionOrMethod(description = "Get the physics world where the player will be spawned", paramNames = {})
    public JSPhysicsWorld getWorld() { return this.jsWorld; }

    @JSCodingFunctionOrMethod(description = "Get the spawn data list for player creation", paramNames = {})
    public Collection<JSSpawnPlayerTranslateData> getSpawnDataList() { return this.jsSpawnDataList; }

    @JSCodingFunctionOrMethod(description = "Get the created JSPlayer", paramNames = {})
    public JSPlayer getPlayer() { return this.jsPlayer; }

    @JSCodingFunctionOrMethod(description = "Set the created JSPlayer", paramNames = {"player"})
    public void setPlayer(JSPlayer player) { this.jsPlayer = player; }

    @JSCodingFunctionOrMethod(description = "Get the optional render data for the player", paramNames = {})
    public JSEntityRenderData getRenderData() { return this.jsRenderData; }

    @JSCodingFunctionOrMethod(description = "Set the optional render data for the player", paramNames = {"renderData"})
    public void setRenderData(JSEntityRenderData renderData) { this.jsRenderData = renderData; }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSPlayerConstructOnMapEvent"; }
}