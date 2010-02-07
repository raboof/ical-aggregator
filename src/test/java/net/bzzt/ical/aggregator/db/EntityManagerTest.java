package net.bzzt.ical.aggregator.db;import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;
import net.bzzt.ical.aggregator.model.Feed;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
 
public class EntityManagerTest extends TestCase {
    private EntityManagerFactory emf;
 
    private EntityManager em;
 
    @Override
	protected void setUp() throws Exception {
        BasicConfigurator.configure();
        Logger.getLogger("org").setLevel(Level.ERROR);
 
        emf = Persistence.createEntityManagerFactory("aggregatorPersistenceUnit");
        em = emf.createEntityManager();
    }
    
    @SuppressWarnings("unchecked")
    public void testInsertAndRetrieve() {
        em.getTransaction().begin();
        em.persist(new Feed("/test", "asdf"));
        em.persist(new Feed("/test2", "AFSD"));
        em.getTransaction().commit();
 
        final List<Feed> list = em.createQuery("select f from Feed f")
                .getResultList();
 
        assertTrue(2 <= list.size());
    }

 
    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
    	if (em != null)
    		em.close();
    }
}