package org.springframework.cloud.servicebroker.mongodb.atlas.service;

import com.google.common.base.Joiner;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Client;

import static org.junit.Assert.assertEquals;

public class AtlasAdminServiceUnitTest {

    private AtlasAdminService service;

    @Mock
    private MongoClient mongoClient;

    @Mock
    private Client restClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        service = new AtlasAdminService(mongoClient, restClient);
    }

    @Test
    public void uriInfroExtractionTest(){
        final String mongoUri = "mongodb://host0:27017,host1:27017,host2:27017/admin?ssl=true&maxIdleTimeMS=60000&replicaSet=Cluster0&authSource=admin";
        final ConnectionString cs = new ConnectionString(mongoUri);

        assertEquals("admin",cs.getDatabase());
        assertEquals("host0:27017,host1:27017,host2:27017",Joiner.on(",").join(cs.getHosts()));
        assertEquals("true",Boolean.toString(cs.getSslEnabled()));
        assertEquals("60000",Integer.toString(cs.getMaxConnectionIdleTime()));
        assertEquals("Cluster0",cs.getRequiredReplicaSetName());
    }

    @Test
    public void getExpectedUriStringTest() {
        final String mongoUri = "mongodb://host0:27017,host1:27017,host2:27017/admin?ssl=true&maxIdleTimeMS=60000&replicaSet=Cluster0&authSource=admin";
        final String generatedConnectionString = AtlasAdminService.getConnectionString(mongoUri,"testDB","userA","passA");
        assertEquals("mongodb://userA:passA@host0:27017,host1:27017,host2:27017/testDB?ssl=true&maxIdleTimeMS=60000&replicaSet=Cluster0&authSource=admin",generatedConnectionString);
    }

    @Test
    public void getConnectionStringMissingReplicasetTest() {
        final String mongoUri = "mongodb://host0:27017,host1:27017,host2:27017/admin?ssl=true&maxIdleTimeMS=60000&authSource=admin";
        final String generatedConnectionString = AtlasAdminService.getConnectionString(mongoUri,"testDB","userA","passA");
        assertEquals("mongodb://userA:passA@host0:27017,host1:27017,host2:27017/testDB?ssl=true&maxIdleTimeMS=60000&authSource=admin",generatedConnectionString);
    }

    @Test
    public void getExpectedUriStringMissingSSLTest() {
        final String mongoUri = "mongodb://host0:27017,host1:27017,host2:27017/admin?maxIdleTimeMS=60000&replicaSet=Cluster0&authSource=admin";
        final String generatedConnectionString = AtlasAdminService.getConnectionString(mongoUri,"testDB","userA","passA");
        assertEquals("mongodb://userA:passA@host0:27017,host1:27017,host2:27017/testDB?ssl=false&maxIdleTimeMS=60000&replicaSet=Cluster0&authSource=admin",generatedConnectionString);
    }

    @Test
    public void getExpectedUriStringMaxIdleTimeMSTest() {
        final String mongoUri = "mongodb://host0:27017,host1:27017,host2:27017/admin?ssl=true&replicaSet=Cluster0&authSource=admin";
        final String generatedConnectionString = AtlasAdminService.getConnectionString(mongoUri,"testDB","userA","passA");
        assertEquals("mongodb://userA:passA@host0:27017,host1:27017,host2:27017/testDB?ssl=true&replicaSet=Cluster0&authSource=admin",generatedConnectionString);
    }

}
