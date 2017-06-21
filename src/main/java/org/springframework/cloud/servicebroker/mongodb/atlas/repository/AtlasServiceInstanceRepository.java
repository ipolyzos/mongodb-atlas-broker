package org.springframework.cloud.servicebroker.mongodb.atlas.repository;

import org.springframework.cloud.servicebroker.mongodb.atlas.model.ServiceInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for ServiceInstance objects
 * 
 * @author ipolyzos
 */
public interface AtlasServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {

}