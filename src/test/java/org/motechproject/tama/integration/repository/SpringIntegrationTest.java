package org.motechproject.tama.integration.repository;

import java.util.ArrayList;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
public abstract class SpringIntegrationTest {

	@Autowired
	protected CouchDbConnector couchDbConnector;
	protected ArrayList<BulkDeleteDocument> toDelete;

	@Before
	public void before() {
		toDelete = new ArrayList<BulkDeleteDocument>();
	}

	@After
	public void after() {
		couchDbConnector.executeBulk(toDelete);
	}
	
	protected void markForDeletion(Object document) {
		toDelete.add(BulkDeleteDocument.of(document));
	}
}
