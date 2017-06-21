# MongoDB Atlas Broker

## Overview

MongoDB Atlas Broker provides MongoDB databases as a Cloud Foundry service. The broker does not include a MongoDB server or cluster. Instead, it is meant to be deployed alongside a managed MongoDB cluster, which it manages, as part of MongoDB Atlas managed services offerings.
 
The MongoDB cluster management tasks that the broker performs are as follows:
    
- Provisioning of database instances (create)
- Creation of credentials (bind)
- Removal of credentials (unbind)
- Unprovisioning of database instances (delete)
 
## Installation

### 1. Compile source code and package 

- Checkout the MongoDB Atlas service broker from github and enter the project's directory.
```
$ git clone https://github.com/ipolyzos/mongodb-atlas-broker.git
$ cd mongodb-atlas-broker
```
- Clean compile and package the project.
```
$ > mvn clean
$ > mvn package
```

### 2. Edit the **manifest.yml** file.

The [sample manifest.yml](https://github.com/ipolyzos/mongodb-atlas-broker/blob/master/manifest-sample.yml) provided can be used as a reference. The specific
variables for the mongodv atlas broker are :

| Key |Description| 
| ------------- |:------------- |
| **ATLAS_GROUPID** | Generated from MongoDB Atlas. |
| **ATLAS_API_USER** | The username of your MongDB Atlas account.|
| **ATLAS_API_KEY** | API Key as generated from MongDB Atlas.|
| **ATLAS_API_BASE** | The default API base as provided by MongoDB Atlas documentation i.e. *https://cloud.mongodb.com/api/atlas/v1.0*|
| **ATLAS_ENDPOINT** | This information can be found on your MongoDB cluster connect information.|

### 3. Push the code and create register thr broker
1. Push the broker into CF i.e.  ``` cf push  ```
2. Register service broker in CF : 
```
cf create-service-broker mongodb-atlas admin P455w0rd https://mongodb-atlas-broker.{{ domain }}
```
### 3. Enable access to the broker 
 
 Finally you need to allow access to the broker e.g. ``` cf enable-service-access mongodb-atlas ```

## Developing
 
 See the [contribution guidelines](https://github.com/ipolyzos/mongodb-atlas-broker/tree/master/CONTRIBUTING.md).
 
## Disclaimer 

 The MongoDB Atlas service broker is an early work based on the sample work of [mongodb-broker](https://github.com/cf-platform-eng/mongodb-broker). 
 
 Please *NOTE* that is still under development and does not intend to be used in production.
 
## License

   Copyright 2017 Ioannis Polyzos

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.