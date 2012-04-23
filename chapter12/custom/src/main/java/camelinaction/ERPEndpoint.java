/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package camelinaction;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.ManagementAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * A custom endpoint which is using Spring JMX to easily let it be managed from JMX.
 * <p/>
 * By using @ManagedResource it will be turned into a MBean which can be managed from JMX.
 * By using @ManagedAttribute we can expose the attributes we want to be managed.
 * There is also a @ManagedOperation you can use for operations.
 *
 * @version $Revision: 60 $
 */
@ManagedResource(description = "Managed ERPEndpoint")
public class ERPEndpoint extends DefaultEndpoint implements ManagementAware<ERPEndpoint> {

    private boolean verbose;

    public ERPEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    public Producer createProducer() throws Exception {
        return new ERPProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported");
    }

    public boolean isSingleton() {
        return true;
    }

    @ManagedAttribute
    public boolean isVerbose() {
        return verbose;
    }

    @ManagedAttribute
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Object getManagedObject(ERPEndpoint object) {
        // just return this as Spring JMX have turned this into a MBean
        return this;
    }
}
