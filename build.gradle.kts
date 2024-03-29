plugins {
    id("midnight-build")
    id("org.wallentines.gradle-multi-version") version "0.2.1"
    id("org.wallentines.gradle-patch") version "0.2.0"
}

multiVersion {
    defaultVersion(17)
    additionalVersions(11, 8)
}

patch {
    patchSet("java8", sourceSets["main"], sourceSets["main"].java, multiVersion.getCompileTask(8))
    patchSet("java11", sourceSets["main"], sourceSets["main"].java, multiVersion.getCompileTask(11))
}

repositories {
    mavenCentral()
    maven("https://maven.wallentines.org/")
    mavenLocal()
}

dependencies {

    api("org.wallentines:midnightcfg-api:2.1.0")
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("org.slf4j:slf4j-api:2.0.7")

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
}
