import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:tap-api:4.1.9")
    compileOnly("io.github.monun:kommand-api:2.6.6")
}

tasks {
    val archive = project.properties["pluginName"].toString()

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    jar {
        archiveBaseName.set(archive)
        archiveClassifier.set("")
        archiveVersion.set("")
    }
    create<Copy>("paperJar") {
        from(jar)
        val plugins = File(rootDir, ".server/plugins/")
        copy {
            into(plugins)
            destinationDir = plugins
        }
        copy {
            into(File(plugins, "update"))
            destinationDir = File(plugins, "update")
        }
    }
}