import spock.lang.*
import org.hibernate.SessionFactory
import org.hibernate.cfg.AnnotationConfiguration

class BasicSpec extends Specification {
    SessionFactory sessionFactory

    def setup(){
        sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory()
    }

	def "can do stuff"(){
		expect: true
	}
}