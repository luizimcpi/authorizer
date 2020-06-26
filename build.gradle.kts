val mainClass = "com.nu.authorizer.application.Main"

plugins {
    kotlin("jvm") version "1.3.60"
}

group = "com.nu.authorizer"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.1")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to mainClass)
        attributes("Package-Version" to archiveVersion)
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } )
    from(sourceSets.main.get().output)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

