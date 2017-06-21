package org.springframework.cloud.servicebroker.mongodb.atlas.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.UnknownHostException;

@Configuration
@EnableMongoRepositories(basePackages = "org.springframework.cloud.servicebroker.mongodb.atlas.repository")
public class BrokerConfig {

	@Value("${ATLAS_ENDPOINT}")
	private String endpoint;

	@Value("${ATLAS_GROUPID}")
	private String groupId;

	@Value("${ATLAS_API_USER}")
	private String apiUser;

	@Value("${ATLAS_API_KEY}")
	private String apiKey;

	@Value("${ATLAS_API_BASE}")
	private String apiBase;

	/**
	 * Build a MongoDB Client
	 *
	 * @return
	 * @throws UnknownHostException
	 */
	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		MongoClientURI uri = new MongoClientURI(endpoint);
		return new MongoClient(uri);
	}

	/**
	 * Build a jersey http client instance
	 *
	 * @return Client
	 */
	@Bean
	public Client restClient(){
		final HttpAuthenticationFeature digestFeature = HttpAuthenticationFeature.digest(apiUser, apiKey);
		final Client client = ClientBuilder.newClient();
		client.register(digestFeature);

		return client;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getApiUser() {
		return apiUser;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getApiBase() {
		return apiBase;
	}
}
