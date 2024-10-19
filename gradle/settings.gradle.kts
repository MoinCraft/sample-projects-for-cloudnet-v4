rootProject.name = "gradle"
include("bukkit")
include("bukkit:plugin")
include("bungeecord")
include("bungeecord:plugin")
include("cloudnet")
include("cloudnet:rpc-module")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

