plugins {
    id("build.library")
    id("build.multiversion")
    id("build.publish")
}

dependencies {

    api("org.wallentines:midnightcfg-api:3.3.0")
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation("org.slf4j:slf4j-simple:2.0.16")
}
