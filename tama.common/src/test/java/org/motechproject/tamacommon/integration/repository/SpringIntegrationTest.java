package org.motechproject.tamacommon.integration.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-TAMACommon.xml")
public abstract class SpringIntegrationTest {
    @Qualifier("tamaDbConnector")
    @Autowired
	protected CouchDbConnector tamaDbConnector;

	protected ArrayList<BulkDeleteDocument> toDelete;

	@Before
	public void before() {
		toDelete = new ArrayList<BulkDeleteDocument>();
	}

	@After
	public void after() {
        deleteAll();
	}

    protected void deleteAll() {
        tamaDbConnector.executeBulk(toDelete);
        toDelete.clear();
    }

    protected void markForDeletion(Object ... documents) {
        for (Object document : documents)
		    markForDeletion(document);
	}

	protected void markForDeletion(Object document) {
		toDelete.add(BulkDeleteDocument.of(document));
	}

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis();
    }
}
