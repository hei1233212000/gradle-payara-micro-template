import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    val kotlinVersion = "1.3.41"
    idea
    kotlin("jvm") version kotlinVersion
    war
    id("fish.payara.micro-gradle-plugin") version "1.0.1"
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    jacoco
}

group = "poc"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

val jeeVersion = "8.0.1"
val junitVersion = "5.5.0-RC1"
val spekVersion = "2.0.5"
val kluentVersion = "1.49"
val mockitoKotlinVersion = "2.1.0"
val arquillianVersion = "1.4.1.Final"
val arquillianPayaraMicroContainerVersion = "1.0.Beta3"
val restAssuredVersion = "4.0.0"
val payaraMicroVersion = "5.192"

val payaraMicroJarDir = "$buildDir/payara-micro"
val payaraMicroJarName = "payara-micro.jar"
dependencyManagement {
    imports {
        mavenBom("org.jboss.arquillian:arquillian-bom:$arquillianVersion")
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    providedCompile("javax:javaee-web-api:$jeeVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")

    testImplementation("org.junit.vintage:junit-vintage-engine:$junitVersion")
    testImplementation("org.jboss.arquillian.junit:arquillian-junit-container")
    testRuntimeOnly("fish.payara.arquillian:arquillian-payara-micro-5-managed:$arquillianPayaraMicroContainerVersion")
    testRuntime("fish.payara.extras:payara-micro:$payaraMicroVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    dependsOn("copyPayaraMicro")
    environment("MICRO_JAR", "$payaraMicroJarDir/$payaraMicroJarName")

    useJUnitPlatform {
        includeEngines("spek2", "junit-vintage")
    }

    finalizedBy("jacocoTestReport")
}

task<Copy>("copyPayaraMicro") {
    from(configurations.testRuntime.get().files({ it.name == "payara-micro" }))
    into(payaraMicroJarDir)
    rename { payaraMicroJarName }
}

payaraMicro {
    payaraVersion = payaraMicroVersion
    deployWar = false
    useUberJar = true
    /*javaCommandLineOptions = mapOf(
        "add-modules" to "java.xml.bind",
        "add-opens" to "java.base/jdk.internal.loader=ALL-UNNAMED"
    )*/
}

jacoco {
    toolVersion = "0.8.4"
}

configure<AllOpenExtension> {
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
}

configure<NoArgExtension> {
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
}
