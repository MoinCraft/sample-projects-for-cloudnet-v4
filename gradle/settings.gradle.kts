rootProject.name = "gradle"
include("bukkit")
include("bukkit:plugin")
include("bungeecord")
include("bungeecord:plugin")
include("cloudnet")
include("cloudnet:rpc-module")
include("cloudnet:command-scheduler")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

