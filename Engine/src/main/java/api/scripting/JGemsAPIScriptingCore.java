package api.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.IOAccess;

public final class JGemsAPIScriptingCore {
    private final Context context;

    public JGemsAPIScriptingCore() {
        this.context = Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(s -> true).allowIO(IOAccess.NONE).option("js.ecmascript-version", "2022").build();
    }

    public void test() {
        this.getContext().eval("js", "print('Hello from GraalJS')");
    }

    public void clear() {
        if (this.getContext() != null) {
            this.getContext().close();
        }
    }

    public Context getContext() {
        return this.context;
    }
}