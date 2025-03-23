plugins {
    id("java")
    id("xyz.jpenilla.resource-factory") version "1.2.0"
}

dependencies {
    implementation("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    implementation(project(":core"))
}

sourceSets.main {
    resourceFactory {
        velocityPluginJson {
            main = "com.zulfen.zulfbungee.velocity.ZulfVelocityMain"
            version = rootProject.extra["fullVersion"] as String
            id = "zulfbungee"
            name = "zulfbungee"
            url = "https://github.com/Zulfen/ZulfBungee"
            authors.add("zulfen")
        }
    }
}