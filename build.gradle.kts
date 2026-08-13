plugins {
    java
}

group = "com.nosalr.antibot"
version = "0.1.0"

// Java 21 porque e o exigido pelo Paper 1.21.11 (ver README).
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // versao fixa de proposito, pra nao mudar sozinha
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

// Pasta do servidor de teste.
// Da pra trocar com: ./gradlew build -PpastaServidor=/outro/caminho
val pastaServidor = (findProperty("pastaServidor") as String?)
    ?: "${System.getProperty("user.home")}/serverteste3s"

// Copia o jar pronto pro servidor de teste em todo build.
tasks.register<Copy>("instalarNoServidor") {
    dependsOn(tasks.jar)
    from(tasks.jar)
    into("$pastaServidor/plugins")
}

tasks.build {
    finalizedBy("instalarNoServidor")
}
