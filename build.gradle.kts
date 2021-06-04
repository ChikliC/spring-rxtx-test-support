import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    `java-library`
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"

    // Spring
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
