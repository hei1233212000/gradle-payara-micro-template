package poc.rest

import io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.amshove.kluent.`should not be`
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual.equalTo
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.test.api.ArquillianResource
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.net.URI
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.shrinkwrap.resolver.impl.gradle.Gradle

@RunWith(Arquillian::class)
class UserEndPointTest {
    @ArquillianResource
    lateinit var url: URI

    companion object {
        private val thirdPartyLibraries = Gradle.resolver()
            .forProjectDirectory(".")
            .importCompileAndRuntime()
            .resolve()
            .asList(JavaArchive::class.java)

        @JvmStatic
        @Deployment
        fun createDeployment() = ShrinkWrap.create(WebArchive::class.java)
            .addPackages(true, "poc")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/init.sql", "META-INF/init.sql")
            .addAsResource("log4j2.xml", "log4j2.xml")
            .addAsWebInfResource(File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
            .addAsLibraries(thirdPartyLibraries)

    }

    @Before
    fun before() {
        enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    @RunAsClient
    fun `Find user by id through the RESTful end point`() {
        // Given
        val userId = 0L
        val userNAme = "Peter"

        `verify if user exist`(userId, userNAme)
    }

    @Test
    @RunAsClient
    fun `Create user through the RESTful end point`() {
        // Given
        val userName = "David"
        val payload = "{\"name\": \"$userName\"}"

        val response = given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(payload)
        .`when`()
            .post(userResourceUrl())

        val newResourceLocation: String = response.header("Location")
        newResourceLocation `should not be` null
        val newResourceId: Long = newResourceLocation.replace(userResourceUrl(), "").toLong()

        response
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", `is`(newResourceId.toInt()))
                .body("name", equalTo(userName))

        `verify if user exist`(newResourceId, userName)
    }

    private fun `verify if user exist`(userId: Long, userName: String) {
        given()
            .accept(ContentType.JSON)
        .`when`()
            .get("${userResourceUrl()}$userId")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", `is`(userId.toInt()))
            .body("name", equalTo(userName))
    }

    private fun userResourceUrl() = "${url}api/users/"
}
