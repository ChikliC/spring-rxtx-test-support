import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"

    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"

    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "2.5.0" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    api("io.projectreactor:reactor-core")
    api("io.projectreactor:reactor-test")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("io.r2dbc:r2dbc-h2")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
}

val jvmVersion: JavaLanguageVersion = JavaLanguageVersion.of(11)
java {
    toolchain {
        languageVersion.set(jvmVersion)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = jvmVersion.toString()
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

tasks.withType<Detekt>().configureEach {
    this.jvmTarget = jvmVersion.toString()
}

java {
    withJavadocJar()
    withSourcesJar()
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Spring Reactive Transaction Test Support")
                description.set("A library to make database tests using Spring's Reactive stack easier by wrapping them in transactions that roll back.")
                url.set("https://github.com/ChikliC/spring-rxtx-test-support")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://chikli.mit-license.org/license.html")
                    }
                }
                developers {
                    developer {
                        id.set("nhajratw")
                        name.set("Nayan Hajratwala")
                        email.set("nayan@chikli.com")
                    }
                }
                scm {
                    connection.set("git@github.com:ChikliC/spring-rxtx-test-support.git")
                    developerConnection.set("git@github.com:ChikliC/spring-rxtx-test-support.git")
                    url.set("https://github.com/ChikliC/spring-rxtx-test-support")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
