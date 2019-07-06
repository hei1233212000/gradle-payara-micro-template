package poc.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.enterprise.inject.spi.InjectionPoint

@Dependent
class LoggerProducer {
    @Produces
    fun produceLogger(injectionPoint: InjectionPoint): Logger = LoggerFactory.getLogger(injectionPoint.member.declaringClass)
}
