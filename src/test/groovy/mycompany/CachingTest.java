package mycompany;

import domain.Fiddle;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.spockframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author berinle
 */
public class CachingTest {

    private SessionFactory sessionFactory;
    private TestHelper helper = new TestHelper();

    @BeforeClass
    public void init(){
        helper.seedData();
        sessionFactory = (SessionFactory) helper.getSessionFactory();
    }
    
    @AfterClass
    public void destroy(){
        Cache cache = CacheManager.getInstance().getCache("domain.Fiddle");
        if(cache != null){
            System.out.println(cache.getStatistics());
        } else {
            System.out.println("no stats to report -- cache is null");
        }

        for(CacheManager cm: CacheManager.ALL_CACHE_MANAGERS){
            cm.shutdown();
        }
    }

    @Test(threadPoolSize = 5, invocationCount = 25)
    public void testSingleInstanceRetrieval() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        TestTimer timer = new TestTimer("iteration");
        Fiddle fiddle = (Fiddle) session.get(Fiddle.class, 20L);
        timer.done();
        Assert.notNull(fiddle);
        session.getTransaction().commit();
        session.close();
    }

    @Test(threadPoolSize = 5, invocationCount = 25)
    public void testCollectionRetrieval() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        TestTimer timer = new TestTimer("iteration");
        List list = session.createQuery("from Fiddle").list();
        timer.done();
        Assert.that(list.size() == 10000);
        session.getTransaction().commit();
        session.close();
    }
}
