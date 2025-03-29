package javagems3d.system.resources.assets.models.parsing;

import javagems3d.system.resources.assets.models.parsing.space.ParsedMesh;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

public interface IParser {
    JGemsPath getJson();
    ParsedMesh parse() throws JGemsIOException;
    String descriptor();

    default JGemsIOException newException(Exception e) {
        throw new JGemsIOException(this.getJson().getFullPath() + " - Couldn't read GLTF model: " + this.descriptor(), e);
    }

    default JGemsIOException newException(String e) {
        throw new JGemsIOException(this.getJson().getFullPath() + " - Couldn't read GLTF model: " + this.descriptor() + ". " + e);
    }
}
