import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.10"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	jacoco
	id("org.sonarqube") version "4.2.1.3168"
	id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.1"
}

sonarqube {
	properties {
		property ("sonar.projectKey", "MobileArts_ms-gci-storytaco")
		property ("sonar.organization", "mobilearts")
		property ("sonar.host.url", "https://sonarcloud.io")
		property ("sonar.coverage.exclusions", "src/main/kotlin/com/unipin/gci-parent-name/model/**/*,src/main/kotlin/com/unipin/gci-parent-name/exception/*,src/main/kotlin/com/unipin/gci-parent-name/configuration/**/*,src/main/kotlin/com/unipin/gci-parent-name/repository/*,src/main/kotlin/com/unipin/gci-parent-name/types/**/*")
	}
}

group = "com.unipin"
version = "0.0.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven("artifactregistry://asia-maven.pkg.dev/unipin-source/backend-libs")
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.cloud:spring-cloud-starter")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("com.unipin:logging-lib:0.1.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	implementation("commons-codec:commons-codec:1.5")
	implementation("com.github.f4b6a3:uuid-creator:5.3.3")
	implementation("org.springframework.boot:spring-boot-starter-aop:3.3.4")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
	maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).coerceAtLeast(1)
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.jacocoTestReport {
	// generate report file in xml for sonarqube test report
	reports {
		xml.required.set(true)
	}

	// exclude package from unit test converage
	classDirectories.setFrom(
			sourceSets.main.get().output.asFileTree.matching {
				exclude(
						"com/unipin/gci-parent-name/configuration/**",
						"com/unipin/gci-parent-name/model/**",
						"com/unipin/gci-parent-name/exception/**",
						"com/unipin/gci-parent-name/repository/**",
						"com/unipin/gci-parent-name/types/**"
				)
			}
	)
}
