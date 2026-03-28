package api.scripting.coding.env.internal.util.world.render.table;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSRedirections", description = "Wrapper for Redirections enum, enabling script access to pipeline redirections.")
public class JSRedirections {
    @JSCodingField(description = "Underlying Java Redirections enum value")
    protected final Redirections redirection;

    @JSCodingConstructor(description = "Constructs a JS wrapper for a given Redirections enum value", paramNames = {"redirection"})
    public JSRedirections(@NotNull Redirections redirection) {
        this.redirection = redirection;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java Redirections enum", paramNames = {})
    public Redirections getJavaRedirection() {
        return this.redirection;
    }

    @JSCodingFunctionOrMethod(description = "Gets the source pipeline of this redirection", paramNames = {})
    public @NotNull JSPipeline getFrom() {
        return new JSPipeline(this.redirection.getFrom());
    }

    @JSCodingFunctionOrMethod(description = "Gets the target pipeline of this redirection", paramNames = {})
    public @NotNull JSPipeline getTo() {
        return new JSPipeline(this.redirection.getTo());
    }

    @JSCodingFunctionOrMethod(description = "Represents SCENE pipeline redirected into TRANSPARENCY pipeline", paramNames = {})
    public static JSRedirections SCENE__IN__TRANSPARENCY() {
        return new JSRedirections(Redirections.SCENE__IN__TRANSPARENCY);
    }

    @JSCodingFunctionOrMethod(description = "Represents TRANSPARENCY pipeline redirected into SCENE pipeline", paramNames = {})
    public static JSRedirections TRANSPARENCY__IN__SCENE() {
        return new JSRedirections(Redirections.TRANSPARENCY__IN__SCENE);
    }
}