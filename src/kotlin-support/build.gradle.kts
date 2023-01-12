import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.8.0"
    `java-library`
    idea
    signing
    `maven-publish`
}

val projGroupId: String by rootProject
val projArtifactId: String by rootProject
val ktArtifactId = "$projArtifactId-kotlin-support"
val projName: String by rootProject
val projVersion: String by rootProject
val projDesc: String by rootProject
val projVcs: String by rootProject
val projBranch: String by rootProject
val orgName: String by rootProject
val orgUrl: String by rootProject
val developers: String by rootProject

val lwjglDepends: ArrayList<String> by rootProject.extra
val lwjglNatives: Array<String> =
    if (System.getProperty("project.bindAllNative").toBoolean()) {
        arrayOf(
            "linux-arm32", "linux-arm64", "linux",
            "macos-arm64", "macos",
            "windows-arm64", "windows-x86", "windows"
        )
    } else {
        val osArch = System.getProperty("os.arch")
        @Suppress("INACCESSIBLE_TYPE")
        arrayOf(
            when (OperatingSystem.current()) {
                OperatingSystem.LINUX ->
                    if (osArch.startsWith("arm") || osArch.startsWith("aarch64"))
                        "linux-${if (osArch.contains("64") || osArch.startsWith("armv8")) "arm64" else "arm32"}"
                    else "linux"

                OperatingSystem.MAC_OS ->
                    if (osArch.startsWith("aarch64")) "macos-arm64" else "macos"

                OperatingSystem.WINDOWS ->
                    if (osArch.contains("64"))
                        "windows${if (osArch.startsWith("aarch64")) "-arm64" else ""}"
                    else "windows-x86"


                else -> throw IllegalStateException("not supporting system ${OperatingSystem.current()}")
            }
        )
    }

group = projGroupId
version = projVersion

repositories {
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    // temporary maven repositories
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    api(rootProject)

    "org.jetbrains:annotations:23.1.0".also {
        compileOnly(it)
        testCompileOnly(it)
    }

    testCompileOnly("org.slf4j:slf4j-simple:2.0.6")
    for (depend in lwjglDepends) {
        for (platform in lwjglNatives) {
            testRuntimeOnly("org.lwjgl:lwjgl$depend::natives-$platform")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    manifestContentCharset = "utf-8"
    setMetadataCharset("utf-8")
    from("LICENSE")
    manifest.attributes(
        "Specification-Title" to archiveBaseName,
        "Specification-Vendor" to orgName,
        "Specification-Version" to "0",
        "Implementation-Title" to archiveBaseName,
        "Implementation-Vendor" to orgName,
        "Implementation-Version" to archiveVersion
    )
}

tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.named("classes"))
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource, "LICENSE")
}

tasks.register<Jar>("javadocJar") {
    val javadoc by tasks
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc, "LICENSE")
}

tasks.withType<Jar> {
    archiveBaseName.set(ktArtifactId)
}

artifacts {
    archives(tasks.named("javadocJar"))
    archives(tasks.named("sourcesJar"))
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.named<Javadoc>("javadoc") {
    isFailOnError = false
    options {
        encoding = "UTF-8"
        locale = "en_US"
        windowTitle = "$projName-kotlin-support $projVersion Javadoc"
        if (this is StandardJavadocDocletOptions) {
            charSet = "UTF-8"
            isAuthor = true
            links("https://docs.oracle.com/en/java/javase/17/docs/api/")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        register<MavenPublication>("mavenKotlin") {
            groupId = projGroupId
            artifactId = ktArtifactId
            version = projVersion
            from(components["kotlin"])
            pom {
                name.set(projName)
                description.set(projDesc)
                url.set("https://github.com/$projVcs")
                licenses {
                    name.set("MIT")
                    url.set("https://raw.githubusercontent.com/$projVcs/$projBranch/LICENSE")
                }
                organization {
                    name.set(orgName)
                    url.set(orgUrl)
                }
                developers {
                    developers.split(',').forEach {
                        it.split(':', limit = 3).also { dev ->
                            developer {
                                id.set(dev[0])
                                name.set(dev[1])
                                email.set(dev[2])
                            }
                        }
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/${projVcs}.git")
                    developerConnection.set("scm:git:https://github.com/${projVcs}.git")
                    url.set("https://github.com/${projVcs}.git")
                }
            }
        }
    }

    // You have to add 'OSSRH_USERNAME', 'OSSRH_PASSWORD', 'signing.keyId',
    // 'signing.password' and 'signing.secretKeyRingFile' to
    // GRADLE_USER_HOME/gradle.properties
    repositories {
        maven {
            name = "OSSRH"
            credentials {
                username = project.findProperty("OSSRH_USERNAME").toString()
                password = project.findProperty("OSSRH_PASSWORD").toString()
            }
            url = uri(
                if (version.toString().endsWith("-SNAPSHOT"))
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                else "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            )
        }
    }
}

signing {
    if (!version.toString().endsWith("-SNAPSHOT") && System.getProperty("gpg.signing", "true").toBoolean()) {
        sign(publishing.publications["mavenKotlin"])
    }
}

idea.module.inheritOutputDirs = true
