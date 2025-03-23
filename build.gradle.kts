plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "com.zulfen.zulfbungee"
project.version = "0.9.9-pre7"
project.description = "A Skript addon which adds proxy integration."
val gitCommitHash: String = "git rev-parse --short HEAD".runCommand()?.trim() ?: "unknown"
extra["fullVersion"] = "${project.version}-$gitCommitHash"


// TODO: Maybe at some point offer separate jars for each respective platform instead of bundling everything into one uberjar
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

sourceSets {
    create("bungeecord") {
        java.srcDir("bungeecord/src/java/")
    }
    create("paper") {
        java.srcDir("paper/src/java")
    }
    create("velocity") {
        java.srcDir("velocity/src/java")
    }
    create("core") {
        java.srcDir("core/src/java/")
        resources.srcDir("core/main/resources")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            name = "skript"
            url = uri("https://repo.skriptlang.org/releases")
        }
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "protocollib"
            url = uri("https://repo.dmulloy2.net/repository/public/")
        }
        maven {
            name = "velocity-proxy"
            url = uri("https://maven.elytrium.net/repo/")
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.mysql:mysql-connector-j:9.2.0")
    implementation("org.semver4j:semver4j:5.4.1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation(project("core"))
    implementation(project("bungeecord"))
    implementation(project("velocity"))
    implementation(project("paper"))
}


tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {

        relocate("com.zaxxer", "com.zulfen.zulfbungee.libs.zaxxer")
        relocate("org.semver4j", "com.zulfen.zulfbungee.libs.semver4j")
        relocate("org.h2", "com.zulfen.zulfbungee.libs.h2")
        relocate("com.mysql", "com.zulfen.zulfbungee.libs.mysql")

        archiveFileName.set("ZulfBungee-${ext["fullVersion"]}-all.jar")

        dependencies {
            include(project("bungeecord"))
            include(project("paper"))
            include(project("velocity"))
            include(project("core"))
            from(sourceSets["core"].resources)
            include(dependency("com.zaxxer:HikariCP"))
            include(dependency("org.semver4j:semver4j"))
            include(dependency("com.h2database:h2"))
            include(dependency("com.mysql:mysql-connector-j"))
        }
    }
}


// note, this won't work if there are any quotes
fun String.runCommand(timeOutSeconds: Long = 5): String? {
    return try {
        val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(file("."))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        if (!process.waitFor(timeOutSeconds, TimeUnit.SECONDS)) {
            process.destroy()
            return null
        }
        process.inputStream.bufferedReader().readText()
    } catch (e: Exception) {
        null
    }
}
