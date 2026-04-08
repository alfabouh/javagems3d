@echo off
                                    java -Dpolyglotimpl.DisableMultiReleaseCheck=true --enable-native-access=ALL-UNNAMED -XX:+UseG1GC -Xms512m -Xmx4G -cp "./core/jgems3d-core.jar;./core/jgems3d-launcher.jar" launcher.bootstrap.Bootstrap workbench=false
                                    pause