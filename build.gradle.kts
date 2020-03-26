import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.serialization") version "1.3.71"
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
        implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-runtime", version = "0.20.0")

        testImplementation(group = "junit", name = "junit", version = "4.12")
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