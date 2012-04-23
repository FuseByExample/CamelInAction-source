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

import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A basic example how to use Routing Slip EIP.
 * <p/>
 * This example is a simple example how to route a message using the routing slip EIP
 * based on an existing header which dictates the sequence steps.
 *
 * @version $Revision: 175 $
 */
public class SpringRoutingSlipSimpleTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/routingslip-simple.xml");
    }

    @Test
    public void testRoutingSlip() throws Exception {
        // setup expectations that only A and C will receive the message
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(0);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        // send the incoming message with the attached slip
        template.sendBodyAndHeader("direct:start", "Hello World", "mySlip", "mock:a,mock:c");

        assertMockEndpointsSatisfied();
    }

}