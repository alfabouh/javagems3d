package api.scripting.coding.env.internal.util.mapping.player;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.misc.JSPair;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import javagems3d.physics.world.PhysicsWorld;

import java.util.Collection;

@JSCodingClass(binding = "JSPlayerCreator", description = "Function interface for creating a player in the game world. " + "Returns a pair of JSPlayer and optionally JSEntityRenderData (can be null if no render data needed).")
public interface JSPlayerCreatorFunction {
    @JSCodingFunctionOrMethod(description = "JSPair<JSPlayer, JSEntityRenderData> / Collection<JSSpawnPlayerTranslateData>. Create a player in the given JSPhysicsWorld. " + "The returned JSPair contains JSPlayer and optionally JSEntityRenderData. " + "If render data is not needed, the second value can be null.")
    JSPair<JSPlayer, JSEntityRenderData> createPlayer(JSPhysicsWorld world, Collection<JSSpawnPlayerTranslateData> spawnPlayerData);
}