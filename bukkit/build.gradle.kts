repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":vanilla"))
    api(group = "org.spigotmc", name = "spigot-api", version = "1.15.2-R0.1-SNAPSHOT")
}