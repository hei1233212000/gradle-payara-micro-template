package poc.rest

import io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual.equalTo
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.test.api.ArquillianResource
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.net.URI
import kotlin.reflect.KClass

@RunWith(Arquillian::class)
class UserEndPointTest {
    @ArquillianResource
    lateinit var url: URI

    companion object {
        @JvmStatic
        @Deployment
        fun createDeployment() = ShrinkWrap.create(WebArchive::class.java)
            .addPackages(true, "poc")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(File(KClass::class.java.protectionDomain.codeSource.location.file))

    }

    @Before
    fun before() {
        enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    @RunAsClient
    fun `Find user by id through the RESTful end point`() {
        // Given
        val userId = 1

        given()
            .accept(ContentType.JSON)
        //`when`()
            .get("${url}api/users/$userId")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", `is`(userId))
            .body("name", equalTo("Peter"))
    }

    @Test
    @RunAsClient
    fun `Create user through the RESTful end point`() {
        // Given
        val expectedUserId = 99
        val userName = "David"
        val payload = "{\"name\": \"$userName\"}"

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(payload)
        //`when`()
            .post("${url}api/users/")
        .then()
            .statusCode(201)
            .header("Location", "${url}api/users/$expectedUserId")
            .contentType(ContentType.JSON)
            .body("id", `is`(expectedUserId))
            .body("name", equalTo(userName))
    }
}
