dependencies {
    // In case there's something missing, feel free to uncomment the follow dependency and see what's missing.
    // api(group = "io.netty", name = "netty-all", version = properties["netty.version"] as String)
    api(group = "io.netty", name = "netty-handler", version = properties["netty_version"].toString())
    api(group = "io.netty", name = "netty-transport-native-epoll", version = properties["netty_version"].toString())
    api(group = "io.netty", name = "netty-transport-native-kqueue", version = properties["netty_version"].toString())
    api(group = "io.netty", name = "netty-codec", version = properties["netty_version"].toString())
}