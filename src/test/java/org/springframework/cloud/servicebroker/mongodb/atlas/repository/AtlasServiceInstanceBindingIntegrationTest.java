package org.springframework.cloud.servicebroker.mongodb.atlas.repository;

import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.mongodb.atlas.IntegrationTestBase;
import org.springframework.cloud.servicebroker.mongodb.atlas.fixture.ServiceInstanceBindingFixture;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.junit.Assert.assertEquals;

public class AtlasServiceInstanceBindingIntegrationTest extends IntegrationTestBase {

    private static final String COLLECTION = "serviceInstanceBinding";

    @Autowired
    private MongoClient client;

    @Autowired
    private AtlasServiceInstanceBindingRepository repository;

    @Autowired
    private MongoOperations mongo;

    @Before
    public void setup() throws Exception {
        mongo.dropCollection(COLLECTION);
    }

    @After
    public void teardown() {
        mongo.dropCollection(COLLECTION);
        client.dropDatabase(DB_NAME);
    }

    @Test
    public void bindingInsertedSuccessfully() throws Exception {
        assertEquals(0, mongo.getCollection(COLLECTION).count());
        repository.save(ServiceInstanceBindingFixture.getServiceInstanceBinding());
        assertEquals(1, mongo.getCollection(COLLECTION).count());
    }

    @Test
    public void bindingDeletedSuccessfully() throws Exception {
        assertEquals(0, mongo.getCollection(COLLECTION).count());
        repository.save(ServiceInstanceBindingFixture.getServiceInstanceBinding());
        assertEquals(1, mongo.getCollection(COLLECTION).count());
        repository.delete(ServiceInstanceBindingFixture.getServiceInstanceBinding().getId());
        assertEquals(0, mongo.getCollection(COLLECTION).count());
    }
}