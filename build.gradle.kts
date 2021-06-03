import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    `java-library`
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.projectreactor:reactor-core:3.4.6")

    implementation("org.springframework:spring-context:5.3.7")
    implementation("org.springframework:spring-tx:5.3.7")
    implementation("io.projectreactor:reactor-test:3.4.6")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.3")

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
