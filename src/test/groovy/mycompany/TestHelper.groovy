package mycompany

import org.hibernate.SessionFactory
import org.hibernate.cfg.AnnotationConfiguration
import domain.Fiddle

/**
 * @author berinle
 */
class TestHelper {

    private SessionFactory sessionFactory;

     void seedData(){
        def session = getSessionFactory().currentSession
        session.beginTransaction()
        10000.times {
            session.save(new Fiddle(name: "$it"))
            if(it % 100 == 0)
                session.flush()
        }
        session.flush()
        session.transaction.commit()
    }

    def getSessionFactory(){
        if(sessionFactory == null){
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory()
        }
        sessionFactory
    }
}
