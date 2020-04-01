import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Cannot move to settings.gradle.kts, will break the project immediately.
    val kotlin_version = System.getProperty("kotlin_version")
    java
    kotlin("jvm") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "ai.haruhi"
    version = "0.0.0"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-runtime", version = properties["kotlinx_serialization_version"].toString())
        implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = properties["kotlinx_coroutines_version"].toString())

        testImplementation(group = "junit", name = "junit", version = properties["junit_version"].toString())
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_13
        targetCompatibility = JavaVersion.VERSION_13
    }

    tasks {
        withType<JavaCompile> {
            options.isDeprecation = true
            options.isFork = true
            options.encoding = "UTF-8"
            options.isVerbose = true
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "13"
        }
        /*val shadowJar = getByName<ShadowJar>("shadowJar") {
            archiveVersion.set(project.version.toString())
            manifest.attributes["Implementation-Version"] = project.version.toString()
        }
        "build" {
            dependsOn(shadowJar)
        }*/
    }
}