package poc.config

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo

@RequestScoped
class JaxrsConfig {
    @Context
    @Produces
    @RequestScoped
    private lateinit var uriInfo: UriInfo
}