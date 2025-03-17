plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

group = "com.zulfen.zulfbungee"
version = "0.9.9-pre6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

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

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.github.SkriptLang:Skript:2.10.1")
    implementation("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.github.waterfallmc:waterfall-api:1.21-R0.1-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.semver4j:semver4j:5.4.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    implementation("com.mysql:mysql-connector-j:9.2.0")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        val gitCommitHash: String = "git rev-parse --short HEAD".runCommand()?.trim() ?: "unknown"

        relocate("com.zaxxer", "com.zulfen.zulfbungee.libs.zaxxer")
        relocate("org.semver4j", "com.zulfen.zulfbungee.libs.semver4j")
        relocate("org.h2", "com.zulfen.zulfbungee.libs.h2")
        relocate("com.mysql", "com.zulfen.zulfbungee.libs.mysql")

        archiveFileName.set("ZulfBungee-$version-$gitCommitHash.jar")

        dependencies {
            include(dependency("com.zaxxer:HikariCP"))
            include(dependency("org.semver4j:semver4j"))
            include(dependency("com.h2database:h2"))
            include(dependency("com.mysql:mysql-connector-j"))
        }
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}


fun String.runCommand(): String? {
    return try {
        val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(file("."))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        process.inputStream.bufferedReader().readText()
    } catch (e: Exception) {
        null
    }
}
