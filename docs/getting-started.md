# Getting Started

There are several ways to start working with JavaGems3D.

## Requirements

* Java 21 or higher
* Apache Maven (for source build)

## Option 1 — Build from Source

You can download the full project archive directly from the GitHub repository.

Repository: JavaGems3D GitHub repository

### Steps

1. Download the project archive from the repository
2. Extract it to your local machine
3. Open the root project directory
4. Build the root module using Maven:

```bash
mvn package
```

This will build all required modules.

## Option 2 — Add as Maven Dependency

You can use JavaGems3D directly as a Maven dependency through GitHub Packages.

Add the following dependencies to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.gltexture</groupId>
        <artifactId>syslogger</artifactId>
        <version>beta0.1.X</version> // CHOOSE THE LATEST VERSION!
    </dependency>

    <dependency>
        <groupId>io.github.gltexture</groupId>
        <artifactId>launcher</artifactId>
        <version>beta0.1.X</version> // CHOOSE THE LATEST VERSION!
    </dependency>

    <dependency>
        <groupId>io.github.gltexture</groupId>
        <artifactId>engine</artifactId>
        <version>beta0.1.X</version> // CHOOSE THE LATEST VERSION!
    </dependency>

    <dependency>
        <groupId>io.github.gltexture</groupId>
        <artifactId>workbench</artifactId>
        <version>beta0.1.X</version> // CHOOSE THE LATEST VERSION!
    </dependency>
</dependencies>
```

Also add the GitHub Maven repository:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub gltexture Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/gltexture/javagems3d</url>
    </repository>
</repositories>
```

## Minimal Application Launch

The minimal application startup requires only one line:

```java
public static void main(String[] args) {
    JavaGemsLauncher.launch(args, null);
}
```

### Parameters

### First argument

`args`

Standard program launch arguments.

### Second argument

`ApiClass.class`

Optional custom application extension class that works directly with the engine API.

Example:

```java
public static void main(String[] args) {
    JavaGemsLauncher.launch(args, ApiClass.class);
}
```

## Custom API Application Example

```java
@JGemsAppEntry(id = "Example")
public class ApiClass extends JGemsApplication {
    @JGemsAppInstance
    public static ApiClass appDefault;

    public ApiClass(JGemsLaunchArgsRegistry args) {
        super(args);
        JGems3D.DEBUG_MODE = true;
    }

    public @NotNull PanelUI getMainMenuPanel() {
        return new DefaultMainMenuPanel(null);
    }

    public void initScripts(@NotNull IAppScriptContextRegistry appScriptRegistry) {
    }

    public void initEvents(@NotNull IAppEventSubscriber appEventSubscriber) {
    }

    public void initResources(@NotNull IAppResources appResources) {
    }

    public @NotNull BindingManager getBindingManager() {
        return new DefaultBindings();
    }

    public Window.@NotNull WindowProperties getWindowProperties() {
        return new Window.WindowProperties("ExampleGame", null);
    }

    public void setupEditorResources(IAPIWBenchDataManager manager) {
        manager.SET_DEFAULTS();
    }
}
```

## IDE Run Configuration

To run the project inside an IDE:

1. Create a new Application Run Configuration
2. Set the Main Class
3. Configure program arguments if needed

To launch the game editor, use:

```text
workbench=true
```

## Available Launch Arguments

### Window Size

```text
win_size=1920;1080
```

Sets the application window size.

### Enable Workbench (Game Editor)

```text
workbench=true
```

Launches the editor instead of the game runtime.

### Disable Fullscreen

```text
no_full_screen=true
```

Starts the application in windowed mode.

### Disable Sound

```text
no_sound=true
```

Disables all sound systems.

### Enable Debug Mode

```text
debug=true
```

Enables additional debug output and development features.

### Launch Specific Test Map

```text
map_test=true
map_path=your_map_id
```

Runs a specific map directly for testing.

### External Game Definition

```text
external_def=path_to_definition
```

Loads an external game directory.

### API Application Classpath

```text
api_app_classpath=your.class.path
```

Used for external API application loading.

## Option 3 — Download Prebuilt Release

You can also download a fully built project archive from GitHub Releases.

This package already contains:

* compiled project files
* launch scripts
* Windows `.bat` files
* ready-to-run structure

This is the fastest way to start without building from source.
