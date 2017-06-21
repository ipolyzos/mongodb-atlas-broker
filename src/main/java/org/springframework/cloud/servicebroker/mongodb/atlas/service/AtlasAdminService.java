package org.springframework.cloud.servicebroker.mongodb.atlas.service;

import com.google.common.collect.Iterables;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.mongodb.atlas.exception.AtlasServiceException;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.base.Joiner.*;

/**
 * Utility class for manipulating Atlas Mongo database.
 * <p>
 * NOTE:
 *   Some operation apply through Atlas Rest API therefore both
 *   mongodb driver and restfull clients used.
 *
 * @author ipolyzos
 */
@Service
public class AtlasAdminService {

    private Logger logger = LoggerFactory.getLogger(AtlasAdminService.class);

    /**
     * Default database admin user
     */
    public static final String ADMIN_DB = "admin";

    /**
     * MongoDB client
     */
    private MongoClient mongoClient;

    /**
     * Jersey Rest Client
     */
    private Client restClient;

    @Autowired
    public AtlasAdminService(final MongoClient dbClient,
                             final Client restClient) {
        this.mongoClient = dbClient;
        this.restClient = restClient;
    }

    /**
     * Check if Database exists
     *
     * @param databaseName
     * @return
     * @throws AtlasServiceException
     */
    public boolean databaseExists(final String databaseName) throws AtlasServiceException {
        return Iterables.contains(mongoClient.listDatabaseNames(), databaseName);
    }

    /**
     * Drop MongoDB Database
     *
     * @param databaseName
     * @throws AtlasServiceException
     */
    public void deleteDatabase(final String databaseName) throws AtlasServiceException {
        mongoClient.getDatabase(databaseName).drop();
    }

    /**
     * Create a MongoDB Database
     *
     * @param databaseName
     * @return
     * @throws AtlasServiceException
     */
    public MongoDatabase createDatabase(final String databaseName) throws AtlasServiceException {
        final MongoDatabase db = mongoClient.getDatabase(databaseName);

        // retrieve collection if exist
        final MongoCollection<org.bson.Document> collection = db.getCollection("foobar");

        // write a document to force db and collection creation
        final Document document = new Document("foo", "bar");
        collection.insertOne(document);

        return db;
    }

    /**
     * Create a MongoDB user for database
     *
     * @param apiBase
     * @param groupId
     * @param database
     * @param username
     * @param password
     * @throws AtlasServiceException
     */
    public void createUser(final String apiBase,
                           final String groupId,
                           final String database,
                           final String username,
                           final String password) throws AtlasServiceException {
        // Construct and set target URI for the REST call
        final String target = String.format("%s/groups/%s/databaseUsers", apiBase, groupId);
        final WebTarget webTarget = restClient.target(target);

        // call create user API
        final Response response = webTarget.request(MediaType.APPLICATION_JSON)
                .post(Entity.json(createAddUserJsonRequest(database, username, password)));
    }

    /**
     * Delete MogoDB Database and User from MongoDB
     * <p>
     * NOTE:
     * This action delete the MongoDB database
     *
     * @param database
     * @param username
     * @throws AtlasServiceException
     */
    public void deleteDatabaseAndUser(final String apiBase,
                                      final String groupId,
                                      final String database,
                                      final String username) throws AtlasServiceException {
        /**
         * Delete USER
         */
        // Construct and set target URImin the REST call
        final String target = String.format("%s/groups/%s/databaseUsers/admin/%s",apiBase, groupId, username);
        final WebTarget webTarget = restClient.target(target);

        // call delete user API
        final Response response = webTarget.request(MediaType.APPLICATION_JSON).delete();

        /**
         *  Drop MongoDB DATABASE
         */
        mongoClient.getDatabase(database).drop();
    }

    /* ********************************************************************* Utility methods */

    /**
     * Simplistic method to build the Add User reques's JSON payload
     *
     * @param databaseName
     * @param username
     * @param password
     * @return
     */
    public static String createAddUserJsonRequest(final String databaseName,
                                                  final String username,
                                                  final String password) {
        return String.format("{ " +
                "  \"databaseName\" : \"admin\", \n" +
                "  \"roles\" : [ { \n" +
                "    \"databaseName\" : \"%s\", \n" +
                "    \"roleName\" : \"readWrite\"" +
                "  }],\n" +
                "  \"username\" : \"%s\",\n" +
                "  \"password\" : \"%s\"\n" +
                "}", databaseName, username, password);
    }


    /**
     * Generate a connection string for new bindings
     *
     * NOTE:
     *  ToDO: cleanup & proper check for null values
     *
     * @param endpoint
     * @param database
     * @param username
     * @param password
     * @return
     */
    public static String getConnectionString(final String endpoint,
                                      final String database,
                                      final String username,
                                      final String password) {
        //extract endpoint values  values
        final ConnectionString cs = new ConnectionString(endpoint);
        final String hosts = cs.getHosts() == null ? "" : on(",").join(cs.getHosts());
        final String ssl = cs.getSslEnabled() == null ? "ssl=false" : safeOptionToString("ssl",Boolean.toString(cs.getSslEnabled()));
        final String maxIdleTimeMS = cs.getMaxConnectionIdleTime() == null ? "" : safeOptionToString("&maxIdleTimeMS",Integer.toString(cs.getMaxConnectionIdleTime()));
        final String replicaSet = cs.getRequiredReplicaSetName() == null ? "" : safeOptionToString("&replicaSet",cs.getRequiredReplicaSetName());

        //costruct and return connection string
        return new StringBuilder()
                .append("mongodb://")
                .append(username)
                .append(":")
                .append(password)
                .append("@")
                .append(hosts)
                .append("/")
                .append(database)
                .append("?")
                .append(ssl)
                .append(maxIdleTimeMS)
                .append(replicaSet)
                .append("&authSource=admin")
                .toString();
    }

    /**
     * Safely return on key/value pair
     *
     * NOTE:
     *   Target options in the MongoDB URI
     *
     * @param key
     * @param value
     * @return
     */
    private static String safeOptionToString(final String key,
                                             final @Nullable String value) {
        return value == null ? "" : String.format("%s=%s", key, value);
    }
}
