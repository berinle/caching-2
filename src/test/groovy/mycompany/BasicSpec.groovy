package mycompany

import spock.lang.*
import org.hibernate.SessionFactory
import org.hibernate.cfg.AnnotationConfiguration
import domain.Fiddle
import groovy.util.logging.Slf4j
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element

@Slf4j
class BasicSpec extends Specification {
    SessionFactory sessionFactory

    def setup(){
        sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory()
    }

    def "test eviction policy"(){
        def cache = CacheManager.instance.getCache("timedCache")

        when:
        10.times {
            cache.put(new Element(it, "$it"))
        }

        then:
        cache.size == 10
        cache.get(5)

        when:
        Thread.sleep(4000)
        cache.getKeys().each{ cache.get(it) }

        then:
        cache.size == 0

    }
}