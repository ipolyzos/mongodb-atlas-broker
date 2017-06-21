package org.springframework.cloud.servicebroker.mongodb.atlas.service;

import com.mongodb.client.MongoDatabase;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.mongodb.atlas.exception.AtlasServiceException;
import org.springframework.cloud.servicebroker.mongodb.atlas.repository.AtlasServiceInstanceRepository;
import org.springframework.cloud.servicebroker.mongodb.atlas.model.ServiceInstance;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Instance Service implementation to manage service instances.
 * <p>
 * NOTE:
 *   Creating a service does the following:
 *
 * 		1. Creates a new database
 * 		2. Saves the ServiceInstance info to the Mongo repository.
 *  
 * @author ipolyzos
 */
@Service
public class AtlasServiceInstanceService implements ServiceInstanceService {

	private AtlasAdminService atlasAdminService;
	
	private AtlasServiceInstanceRepository repository;

	@Autowired
	public AtlasServiceInstanceService(AtlasAdminService mongo, AtlasServiceInstanceRepository repository) {
		this.atlasAdminService = mongo;
		this.repository = repository;
	}
	
	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		ServiceInstance instance = repository.findOne(request.getServiceInstanceId());
		if (instance != null) {
			throw new ServiceInstanceExistsException(request.getServiceInstanceId(), request.getServiceDefinitionId());
		}

		instance = new ServiceInstance(request);

		if (atlasAdminService.databaseExists(instance.getServiceInstanceId())) {
			// ensure the instance is empty
			atlasAdminService.deleteDatabase(instance.getServiceInstanceId());
		}

		final MongoDatabase db = atlasAdminService.createDatabase(instance.getServiceInstanceId());
		if (db == null) {
			throw new ServiceBrokerException("Failed to create new DB instance: " + instance.getServiceInstanceId());
		}
		repository.save(instance);

		return new CreateServiceInstanceResponse();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
	}

	public ServiceInstance getServiceInstance(String id) {
		return repository.findOne(id);
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) throws AtlasServiceException {
		final String instanceId = request.getServiceInstanceId();
		final ServiceInstance instance = repository.findOne(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		atlasAdminService.deleteDatabase(instanceId);
		repository.delete(instanceId);
		return new DeleteServiceInstanceResponse();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
		final String instanceId = request.getServiceInstanceId();
		final ServiceInstance instance = repository.findOne(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		repository.delete(instanceId);
		final ServiceInstance updatedInstance = new ServiceInstance(request);
		repository.save(updatedInstance);
		return new UpdateServiceInstanceResponse();
	}
}