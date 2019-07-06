package poc.service

import poc.model.dto.User
import poc.model.entity.UserEntity
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.transaction.Transactional

@ApplicationScoped
@Transactional
class UserService @Inject constructor(
    val entityManager: EntityManager
){
    fun create(userName: String): User {
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
