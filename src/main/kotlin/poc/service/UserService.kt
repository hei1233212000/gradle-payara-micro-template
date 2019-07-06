package poc.service

import org.slf4j.Logger
import poc.model.dto.User
import poc.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.transaction.Transactional

@ApplicationScoped
@Transactional
class UserService @Inject constructor(
    val entityManager: EntityManager,
    val logger: Logger
){
    fun create(userName: String): User {
        logger.info("userName: {}", userName)
        val userEntity = UserEntity(name = userName)
        entityManager.persist(userEntity)
        return User(
            id = userEntity.id,
            name = userEntity.name
        )
    }

    fun findById(id: Long): User? {
        val user = entityManager.find(UserEntity::class.java, id)
        return user?.let { User(
            id = it.id,
            name = it.name
        ) }
    }
}
