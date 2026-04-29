package api.scripting.coding.env.internal.util.world.physical.player.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.entities.kinematic.player.IPlayer;

@JSCodingClass(binding = "JSPlayer", description = "Wrapper for IPlayer interface, exposing player properties.")
public interface JSPlayer {
    @JSCodingFunctionOrMethod(description = "Get underlying Java IPlayer object (unsafe)")
    @JSHideFromDoc
     IPlayer getJavaPlayer();
}