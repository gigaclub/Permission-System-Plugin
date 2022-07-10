import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

val GITHUB_PACKAGES_USERID: String by project
val GITHUB_PACKAGES_IMPORT_TOKEN: String by project

repositories {
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "cloudnet-releases"
        url = uri("https://repo.cloudnetservice.eu/repository/releases/")
    }
    maven {
        name = "translationapi"
        url = uri("https://maven.pkg.github.com/gigaclub/translationapi")
        metadataSources {
            mavenPom()
            artifact()
        }
        credentials {
            username = GITHUB_PACKAGES_USERID
            password = GITHUB_PACKAGES_IMPORT_TOKEN
        }
    }
    maven {
        name = "permissionsystemapi"
        url = uri("https://maven.pkg.github.com/gigaclub/permissionsystemapi")
        metadataSources {
            mavenPom()
            artifact()
        }
        credentials {
            username = GITHUB_PACKAGES_USERID
            password = GITHUB_PACKAGES_IMPORT_TOKEN
        }
    }
}

dependencies {
    implementation(project(":permissionsystem-common"))
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    implementation("de.dytanic.cloudnet:cloudnet-cloudperms:3.4.3-RELEASE")
    implementation("de.dytanic.cloudnet:cloudnet-driver:3.4.3-RELEASE")
}

tasks {
    // If you open resources/plugins.yml you will see "@version@" as the version this code replaces this
    processResources {
        from(sourceSets["main"].resources) {
            val tokens = mapOf("version" to version)
            filter(ReplaceTokens::class, mapOf("tokens" to tokens))
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
