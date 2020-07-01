package poc.rest

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import poc.model.dto.User
import poc.service.UserService
import javax.ws.rs.core.UriInfo

object FindUserFeature : Spek({
    lateinit var userResource: UserResource
    lateinit var userService: UserService
    lateinit var uriInfo: UriInfo

    Feature("Find user") {
        Scenario("Find user by id") {
            val userId = 1L
            val user = User(
                id = userId,
                name = "Tom"
            )
            var result: User? = null

            When("User with id = $userId exist in DB") {
                userService = mockk()
                every { userService.findById(userId) } returns user
                uriInfo = mockk()
                userResource = UserResource(userService, uriInfo)
            }

            When("Use the UserResource to retrieve user by id") {
                result = userResource.findById(userId)
            }

            Then("The result should NOT be NULL") {
                result `should not be` null
            }

            Then("The id of the result should be $userId") {
                result?.id `should be` userId
            }

            Then("The name of the result should be ${user.name}") {
                result?.name `should be` user.name
            }
        }
    }
})
