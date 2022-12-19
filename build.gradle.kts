plugins {
  java
  id("org.springframework.boot") version "2.7.5"
  id("io.spring.dependency-management") version "1.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "2021.0.5"
extra["jacksonVersion"] = "2.14.1"
dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  implementation("org.springframework.boot:spring-boot-starter-integration")
  implementation("org.springframework.integration:spring-integration-jmx")
  implementation("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonVersion")}")
  implementation("com.fasterxml.jackson.core:jackson-databind:${property("jacksonVersion")}")
  implementation("org.jsoup:jsoup:1.15.3")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}