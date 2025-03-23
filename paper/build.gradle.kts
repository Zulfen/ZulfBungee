plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    id("xyz.jpenilla.resource-factory") version "1.2.0"
}

dependencies {
    implementation("com.github.SkriptLang:Skript:2.10.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    implementation(project(":core"))
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}


sourceSets.main {
    resourceFactory {
        bukkitPluginYaml {
            main = "com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot"
            version = rootProject.extra["fullVersion"] as String
            name = "ZulfBungee"
            author = "zulfen"
            website = "https://github.com/Zulfen/ZulfBungee"
            apiVersion = "1.13"
            depend.addAll("Skript", "ProtocolLib")
        }
    }
}