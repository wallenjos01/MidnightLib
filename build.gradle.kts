plugins {
    id("midnight-build")
}

dependencies {

    api("org.wallentines:midnightcfg-api:2.5.0-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("org.slf4j:slf4j-api:2.0.11")

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation("org.slf4j:slf4j-simple:2.0.11")
}
