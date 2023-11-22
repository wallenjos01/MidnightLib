plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("org.wallentines.gradle-multi-version") version "0.2.1-SNAPSHOT"
    id("org.wallentines.gradle-patch") version "0.1.1-SNAPSHOT"
}

group = "org.wallentines"
version = "1.4.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
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

    api("org.wallentines:midnightcfg-api:2.0.0-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.1")

    implementation("org.slf4j:slf4j-api:2.0.7")

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
    workingDir("run/test")
    maxHeapSize = "1G"
}

publishing {

    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    if (project.hasProperty("pubUrl")) {
        repositories.maven(project.properties["pubUrl"] as String) {
            name = "pub"
            credentials(PasswordCredentials::class.java)
        }
    }
}