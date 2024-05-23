plugins {
    id("java")
    id("java-library")
    id("org.wallentines.gradle-multi-version")
    id("org.wallentines.gradle-patch")
    id("maven-publish")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

multiVersion {
    defaultVersion(21)
    additionalVersions(17, 8)
}

patch {
    patchSet("java8", sourceSets["main"], sourceSets["main"].java, multiVersion.getCompileTask(8))
    patchSet("java17", sourceSets["main"], sourceSets["main"].java, multiVersion.getCompileTask(17))
}

repositories {
    mavenCentral()
    maven("https://maven.wallentines.org/releases")

    if(GradleVersion.version(version as String).isSnapshot) {
        maven("https://maven.wallentines.org/snapshots")
    }
}

tasks.test {
    useJUnitPlatform()
    workingDir("run/test")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = if(rootProject == project) {
            project.name
        } else {
            rootProject.name + "-" + project.name
        }
        from(components["java"])
    }

    if (project.hasProperty("pubUrl")) {

        var url: String = project.properties["pubUrl"] as String
        url += if(GradleVersion.version(version as String).isSnapshot) {
            "snapshots"
        } else {
            "releases"
        }

        repositories.maven(url) {
            name = "pub"
            credentials(PasswordCredentials::class.java)
        }
    }
}