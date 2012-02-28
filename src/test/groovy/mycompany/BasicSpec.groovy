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

	def "test basic insert"(){
        setup:
        def f = new Fiddle(name: 'tommy')

        def s = sessionFactory.getCurrentSession()
        
        when:
        s.beginTransaction()
        s.save(f)
        def list = s.createCriteria(Fiddle.class).list()
        s.getTransaction().commit()
        
		then:
        list.size() > 0
	}
    
    def "test cache"(){
        setup:
        def s = sessionFactory.getCurrentSession()
        s.beginTransaction()
        100.times {
            def f = new Fiddle(name: "fiddle" + System.currentTimeMillis())
            s.save(f)
            if(it % 20 == 0){
                s.flush()
            }
        }
        def list = s.createCriteria(Fiddle.class).list()
        s.getTransaction().commit()

        def someFiddle

        when:
        s = sessionFactory.getCurrentSession()
        s.beginTransaction()
        3.times {
            someFiddle = s.get(Fiddle.class, 50L)
            log.debug(someFiddle.toString())
            Thread.sleep(1000)
        }
        s.getTransaction().commit()

        then:
        list.size() > 50
        someFiddle?.name.contains("fiddle")
    }
    
    def "test manual entries"(){
        //see http://www.whatsabyte.com/P1/byteconverter.htm
        setup:
        def cache = CacheManager.instance.getCache("domain.Fiddle")
        
        when:
        def numOfElems = 10000
        numOfElems.times {
            cache.put(new Element(it, "some value"+System.currentTimeMillis()))
        }
        Thread.sleep(200)
        def firstelem = cache.get(1)
        def lastelem = cache.get(numOfElems-1)
        log.info "${cache.calculateInMemorySize()} b"
        log.info "${(cache.calculateInMemorySize() / 1024)} kb"
        log.info "${((cache.calculateInMemorySize()/(1024 * 1024)))} mb"

        assert cache.isStatisticsEnabled()

        Thread.sleep(3000)
        CacheManager.instance.shutdown()
        
        then:
        firstelem
        lastelem
        
    }
}