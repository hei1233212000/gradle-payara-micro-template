package poc.rest

import poc.model.dto.User
import poc.service.UserService
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@RequestScoped
@Path("users/")
class UserResource @Inject constructor(
    private val userService: UserService,
    private val uriInfo: UriInfo
) {
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(@PathParam("id") id: Long): User? = userService.findById(id)

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createUser(newUser: User): Response {
        val persistedUser = userService.create(newUser.name!!)
        val uriBuilder = uriInfo.absolutePathBuilder.path(persistedUser.id.toString())

        return Response.created(uriBuilder.build())
            .entity(persistedUser)
            .build()
    }
}
