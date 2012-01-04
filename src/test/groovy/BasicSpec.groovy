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
}