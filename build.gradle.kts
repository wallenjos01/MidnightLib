import java.net.URI

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "org.wallentines"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

java.withSourcesJar()

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api("org.wallentines:midnightcfg:1.0.0")
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("com.google.code.gson:gson:2.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
    workingDir("run/test")
    maxHeapSize = "1G"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            version = version as String
            from(components["java"])
        }
    }
    repositories {
        if (project.hasProperty("pubUrl")) {
            maven {
                name = "pub"
                url = URI.create(project.properties["pubUrl"] as String)
                credentials(PasswordCredentials::class.java)
            }
        }
    }
}