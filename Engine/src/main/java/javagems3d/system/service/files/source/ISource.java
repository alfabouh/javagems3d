package javagems3d.system.service.files.source;

public interface ISource {
    enum Source {
        INSIDE_JAR,
        OUTSIDE_JAR
    }

    Source getSource();
}
