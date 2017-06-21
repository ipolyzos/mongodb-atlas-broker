package org.springframework.cloud.servicebroker.mongodb.atlas;

import org.junit.runner.RunWith;
import org.springframework.cloud.servicebroker.mongodb.atlas.config.BrokerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class IntegrationTestBase {
    public static final String DB_NAME = "test-mongodb-atlas";
}
