plugins {
    id("java")
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("org.semver4j:semver4j:5.4.1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
}