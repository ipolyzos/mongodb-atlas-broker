package org.springframework.cloud.servicebroker.mongodb.atlas.config;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class CatalogConfig {

    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        "mongodb-atlas-broker",
                        "mongodb-atlas",
                        "MongoDB Atlas Service Broker",
                        true,
                        true,
                        Collections.singletonList(
                                new Plan("default-mongodb-atlas-plan",
                                        "default",
                                        "This is a default mongo plan. All services are created equally.",
                                        getPlanMetadata())),
                        Arrays.asList("mongodb-atlas", "document"),
                        getServiceDefinitionMetadata(),
                        null,
                        null)));
    }

    /* Used by CF Console */
	
    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<>();
        sdMetadata.put("displayName", "MongoDB");
        sdMetadata.put("imageUrl", "http://info.mongodb.com/rs/mongodb/images/MongoDB_Logo_Full.png");
        sdMetadata.put("longDescription", "MongoDB Altas Service");
        sdMetadata.put("providerDisplayName", "MongoDB");
        sdMetadata.put("documentationUrl", "https://docs.atlas.mongodb.com");
        sdMetadata.put("supportUrl", "https://www.mongodb.com/contact?jmp=footer");
        return sdMetadata;
    }

    private Map<String, Object> getPlanMetadata() {
        Map<String, Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());

        return planMetadata;
    }

    private List<Map<String, Object>> getCosts() {
        Map<String, Object> costsMap = new HashMap<>();

        Map<String, Object> amount = new HashMap<>();
        amount.put("USD", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        List costMapList = Collections.singletonList(costsMap);
        return costMapList;
    }

    private List<String> getBullets() {
        return Arrays.asList("Shared MongoDB Cluster",
                "100 MB Storage (not enforced)",
                "50 concurrent connections (not enforced)");
    }

}
