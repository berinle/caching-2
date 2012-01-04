import spock.lang.*
import org.hibernate.SessionFactory
import org.hibernate.cfg.AnnotationConfiguration
import domain.Fiddle

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
        
        when:
        s = sessionFactory.getCurrentSession()
        s.beginTransaction()
        def someFiddle = s.get(Fiddle.class, 50L)
        s.getTransaction().commit()

        then:
        list.size() > 50
        someFiddle.name.contains("fiddle")
    }
}