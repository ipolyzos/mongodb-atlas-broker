package org.springframework.cloud.servicebroker.mongodb.atlas.service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.mongodb.atlas.config.BrokerConfig;
import org.springframework.cloud.servicebroker.mongodb.atlas.model.ServiceInstanceBinding;
import org.springframework.cloud.servicebroker.mongodb.atlas.repository.AtlasServiceInstanceBindingRepository;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service instance binding service.
 * <p>
 * NOTE:
 *   Binding a service does the following:
 *     1. Creates a database
 *     2. Creates a new user in the database with default pwd of "P@55w0rd"
 *     3. Saves the ServiceInstanceBinding info to the Mongo "admin" repository
 *
 *  @author ipolyzos
 */
@Service
public class AtlasServiceInstanceBindingService implements ServiceInstanceBindingService {

    private AtlasAdminService atlasAdminService;

    private BrokerConfig brokerConfig;

    private AtlasServiceInstanceBindingRepository bindingRepository;

    @Autowired
    public AtlasServiceInstanceBindingService(final AtlasAdminService atlasAdminService,
                                              final BrokerConfig brokerConfig,
                                              final AtlasServiceInstanceBindingRepository bindingRepository) {
        this.atlasAdminService = atlasAdminService;
        this.brokerConfig = brokerConfig;
        this.bindingRepository = bindingRepository;
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(final CreateServiceInstanceBindingRequest request) {
        final String bindingId = request.getBindingId();
        final String serviceInstanceId = request.getServiceInstanceId();

        ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
        if (binding != null) {
            throw new ServiceInstanceBindingExistsException(serviceInstanceId, bindingId);
        }

        // early attempt toward secure password generation
        final String password =  UUID.randomUUID().toString();
        atlasAdminService.createUser(brokerConfig.getApiBase(), brokerConfig.getGroupId(),serviceInstanceId, bindingId, password);

        final Map<String, Object> credentials = Collections.singletonMap("uri", (Object) atlasAdminService.getConnectionString(brokerConfig.getEndpoint(), serviceInstanceId, bindingId, password));

        binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials, null, request.getBoundAppGuid());
        bindingRepository.save(binding);

        return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);
    }

    @Override
    public void deleteServiceInstanceBinding(final DeleteServiceInstanceBindingRequest request) {
        final String bindingId = request.getBindingId();
        final String serviceInstanceId = request.getServiceInstanceId();

        final ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);
        if (binding == null) {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }

        atlasAdminService.deleteDatabaseAndUser(brokerConfig.getApiBase(), brokerConfig.getGroupId(), serviceInstanceId, bindingId);
        bindingRepository.delete(bindingId);
    }

    protected ServiceInstanceBinding getServiceInstanceBinding(final String bindingId) {
        return bindingRepository.findOne(bindingId);
    }
}