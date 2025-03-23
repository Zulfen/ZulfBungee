plugins {
    id("java")
    id("xyz.jpenilla.resource-factory") version "1.2.0"
}

dependencies {
    implementation(project(":core"))
    implementation("io.github.waterfallmc:waterfall-api:1.21-R0.1-SNAPSHOT")
}

sourceSets.main {
    resourceFactory {
        bungeePluginYaml {
            main = "com.zulfen.zulfbungee.bungeecord.ZulfBungeecordMain"
            author = "zulfen"
        }
    }
}