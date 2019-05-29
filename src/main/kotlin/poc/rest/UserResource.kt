package poc.rest

import poc.service.UserService
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@RequestScoped
@Path("users/")
class UserResource {
    @Inject
    private lateinit var userService: UserService

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(@PathParam("id") id: Long) = userService.findById(id)
}
