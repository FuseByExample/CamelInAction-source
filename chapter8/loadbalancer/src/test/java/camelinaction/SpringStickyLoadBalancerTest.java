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

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates how to use the Load Balancer EIP pattern.
 * <p/>
 * Using the sticky strategy.
 *
 * @version $Revision: 138 $
 */
public class SpringStickyLoadBalancerTest extends CamelSpringTestSupport {

    @Test
    public void testLoadBalancer() throws Exception {
        // A should get the 1st and 4th message
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedBodiesReceived("Hello", "Bye");

        // B should get the 2nd and 3rd message
        MockEndpoint b = getMockEndpoint("mock:b");
        b.expectedBodiesReceived("Camel rocks", "Cool");

        // send in 4 messages with id as correlation key
        // notice that the ids is not an exact number to pick the processor in order.
        // Camel will use the key to generate a hash value which is used for choosing the processor.
        // gold will pick A because its the first message
        // then silver will be bound to pick B as its the next
        template.sendBodyAndHeader("direct:start", "Hello", "type", "gold");
        template.sendBodyAndHeader("direct:start", "Camel rocks", "type", "silver");
        template.sendBodyAndHeader("direct:start", "Cool", "type", "silver");
        template.sendBodyAndHeader("direct:start", "Bye", "type", "gold");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/sticky-loadbalancer.xml");
    }
}