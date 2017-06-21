package org.springframework.cloud.servicebroker.mongodb.atlas.repository;

import org.springframework.cloud.servicebroker.mongodb.atlas.model.ServiceInstanceBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for ServiceInstanceBinding objects
 *
 * @author ipolyzos
 */
public interface AtlasServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

}