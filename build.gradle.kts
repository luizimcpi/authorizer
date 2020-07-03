val mainClass = "com.nu.authorizer.application.Main"
val ktlint by configurations.creating

plugins {
    kotlin("jvm") version "1.3.60"
    id("jacoco")
}

group = "com.nu.authorizer"
version = "1.0.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    ktlint("com.pinterest:ktlint:0.37.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.1")
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

tasks.jacocoTestReport {
    dependsOn(":test")
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(":test")
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude("com/nu/authorizer/application/Main.kt")
            exclude("com/nu/authorizer/domain/common/config/**")
            exclude("com/nu/authorizer/domain/common/constants/**")
            exclude("com/nu/authorizer/domain/exception/**")
            exclude("com/nu/authorizer/domain/model/**")
            exclude("com/nu/authorizer/domain/repositories/**")
            exclude("com/nu/authorizer/domain/services/GenericService.kt")
            exclude("com/nu/authorizer/domain/services/TransactionIntervalRules.kt")
        }
    )
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
    finalizedBy(":jacocoTestReport", ":jacocoTestCoverageVerification")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val jacocoTestCoverageVerification = tasks.findByName("jacocoTestCoverageVerification")
tasks.check { dependsOn(ktlintCheck, jacocoTestCoverageVerification) }
