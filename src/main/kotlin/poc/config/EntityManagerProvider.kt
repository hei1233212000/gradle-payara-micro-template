package poc.config

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@RequestScoped
class EntityManagerProvider {
    @PersistenceContext(unitName = "MyPU")
    private lateinit var entityManager: EntityManager

    @Produces
    @RequestScoped
    fun getEntityManager(): EntityManager {
        return entityManager
    }
}
