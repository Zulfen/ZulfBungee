plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
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
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.github.SkriptLang:Skript:2.9.5")
    implementation("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.github.waterfallmc:waterfall-api:1.21-R0.1-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.semver4j:semver4j:5.4.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {

        // Relocate dependencies to avoid conflicts
        relocate("com.zaxxer", "com.zulfen.zulfbungee.libs.zaxxer")
        relocate("org.semver4j", "com.zulfen.zulfbungee.libs.semver4j")
        relocate("org.h2", "com.zulfen.zulfbungee.libs.h2")

        // Set the archive file name
        archiveFileName.set("ZulfBungee-$version-Paper.jar")

        // (Optional) Only include the specified dependencies if needed
        dependencies {
            include(dependency("com.zaxxer:HikariCP"))
            include(dependency("org.semver4j:semver4j"))
            include(dependency("com.h2database:h2"))
        }

    }
}


// Make the `shadowJar` task the default jar task (thanks chatgpt)
tasks.assemble {
    dependsOn(tasks.shadowJar)
}