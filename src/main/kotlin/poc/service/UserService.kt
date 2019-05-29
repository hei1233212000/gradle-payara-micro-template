package poc.service

import poc.model.User
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserService {
    fun findById(id: Long) = User(
        id = id,
        name = "Peter"
    )
}
