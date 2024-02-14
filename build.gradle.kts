import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.jetbrains.kotlin.jvm") version "1.9.22"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.22"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.22"
}

group = "com.ymkwon"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.json:json:20230227")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
	testImplementation("io.kotest:kotest-property:5.5.5")
	testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets {
	getByName("main").java.srcDirs("main/kotlin")
	getByName("main").resources.srcDirs("main/resources")
	getByName("test").java.srcDirs("test/kotlin")
}
