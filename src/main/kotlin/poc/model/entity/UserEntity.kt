package poc.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "USER")
data class UserEntity(
    @Id @GeneratedValue var id: Long? = null,
    val name: String
)
