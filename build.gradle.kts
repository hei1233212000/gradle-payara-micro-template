import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    val kotlinVersion = "1.3.70"
    idea
    kotlin("jvm") version kotlinVersion
    war
    id("fish.payara.micro-gradle-plugin") version "1.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    jacoco
}

group = "poc"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
    }
    maven {
        url = uri("https://raw.github.com/payara/Payara_PatchedProjects/master")
    }
}

val payaraMicroVersion = "5.2020.2"
val log4j2Version = "2.13.1"
val slf4jVersion = "1.8.0-beta4" // compatible to log4j2

val junitVersion = "5.6.0"
val spekVersion = "2.0.9"
val kluentVersion = "1.59"
val mockitoKotlinVersion = "2.2.0"
val arquillianVersion = "1.5.0.Final"
val shrinkwrapVersion = "3.1.4"
val restAssuredVersion = "4.2.0"

val payaraMicroJarDir = "$buildDir/payara-micro"
val payaraMicroJarName = "payara-micro.jar"
val payaraMicroJarPath = "$payaraMicroJarDir/$payaraMicroJarName"

val warTask = tasks["war"] as War
val explodedWarDir = "$buildDir/${project.name}"

val payaraMicroPostBootCommandScript = "$projectDir/config/post-boot-command.txt"

dependencyManagement {
    imports {
        mavenBom("fish.payara.api:payara-bom:$payaraMicroVersion")
        mavenBom("org.jboss.arquillian:arquillian-bom:$arquillianVersion")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:$log4j2Version")
    implementation("org.slf4j:osgi-over-slf4j:$slf4jVersion")
    implementation("org.slf4j:jul-to-slf4j:$slf4jVersion")
    implementation("org.slf4j:log4j-over-slf4j:$slf4jVersion")
    implementation("org.slf4j:jcl-over-slf4j:$slf4jVersion")

    providedCompile("jakarta.platform:jakarta.jakartaee-api")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")

    testImplementation("org.junit.vintage:junit-vintage-engine:$junitVersion")
    testImplementation("org.jboss.arquillian.junit:arquillian-junit-container")
    testImplementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-gradle:$shrinkwrapVersion") {
        exclude(module = "gradle-tooling-api")
    }
    testImplementation("org.gradle:gradle-tooling-api:${gradle.gradleVersion}")
    testRuntimeOnly("fish.payara.arquillian:arquillian-payara-micro-managed")
    testRuntime("fish.payara.extras:payara-micro")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion") {
        // suspend the warning of "'dependencyManagement.dependencies.dependency.systemPath' for com.sun:tools:jar must specify an absolute path but is ${tools.jar} in com.sun.xml.bind:jaxb-osgi:2.2.10"
        exclude(module = "jaxb-osgi")
    }
}

tasks.war {
    archiveFileName.set("${project.name}.war")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    dependsOn("copyPayaraMicro")
    environment("MICRO_JAR", "$payaraMicroJarDir/$payaraMicroJarName")
    environment("EXTRA_MICRO_OPTIONS", "--postbootcommandfile $projectDir/config/post-boot-command.txt")

    useJUnitPlatform {
        includeEngines("spek2", "junit-vintage")
    }

    finalizedBy("jacocoTestReport")
}

task<Copy>("copyPayaraMicro") {
    from(configurations.testRuntime.get().files { it.name == "payara-micro" })
    into(payaraMicroJarDir)
    rename { payaraMicroJarName }
}

/**
 * Use exploder War can make you update publish the update of JSF template immediately
 */
task<Copy>("explodedWar") {
    into(explodedWarDir)
    with(warTask)
}

/**
 * We create a custom task instead of using Payara Gradle Plugin because the plugin now is just providing very limited features to us
 *
 * It will use the Java version set by user
 */
task<Exec>("runApp") {
    executable("java")

    args(
        listOf(
            "-jar",
            payaraMicroJarPath,
            "--autoBindHttp",
            "--nocluster",
            "--postbootcommandfile",
            payaraMicroPostBootCommandScript,
            "--deploy",
            explodedWarDir
        )
    )
}.dependsOn("copyPayaraMicro", "explodedWar")

/**
 * Check the guide in https://docs.payara.fish/documentation/ecosystem/gradle-plugin.html
 */
payaraMicro {
    payaraVersion = payaraMicroVersion
    useUberJar = true
    commandLineOptions = mapOf(
        "postbootcommandfile" to payaraMicroPostBootCommandScript
    )
}

jacoco {
    toolVersion = "0.8.5"
}

configure<AllOpenExtension> {
    annotation("javax.persistence.Entity")
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
}

configure<NoArgExtension> {
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
}
