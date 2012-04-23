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

import java.net.ConnectException;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test simulating errors using interceptors.
 *
 * @version $Revision: 131 $
 */
public class SimulateErrorUsingInterceptorTest extends CamelSpringTestSupport {

    // inject the file endpoint which we need to use for starting the test
    @EndpointInject(ref = "fileEndpoint")
    private Endpoint file;

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        // load the spring XML file from this classpath
        return new ClassPathXmlApplicationContext("camelinaction/usecase.xml");
    }

    @Test
    public void testSimulateErrorUsingInterceptors() throws Exception {
        // first find the route we need to advice. Since we only have one route
        // then just grab the first from the list
        RouteDefinition route = context.getRouteDefinitions().get(0);

        // advice the route by enriching it with the route builder where
        // we add a couple of interceptors to help simulate the error
        route.adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to http and detour to our processor instead
                interceptSendToEndpoint("http://*")
                    // skip sending to the real http when the detour ends
                    .skipSendToOriginalEndpoint()
                    .process(new SimulateHttpErrorProcessor());

                // intercept sending to ftp and detour to the mock instead
                interceptSendToEndpoint("ftp://*")
                    // skip sending to the real ftp endpoint
                    .skipSendToOriginalEndpoint()
                    .to("mock:ftp");
            }
        });

        // our mock should receive the message
        MockEndpoint mock = getMockEndpoint("mock:ftp");
        mock.expectedBodiesReceived("Camel rocks");

        // start the test by creating a file that gets picked up by the route
        template.sendBodyAndHeader(file, "Camel rocks", Exchange.FILE_NAME, "hello.txt");

        // assert our test passes
        assertMockEndpointsSatisfied();
    }

    private class SimulateHttpErrorProcessor implements Processor {

        public void process(Exchange exchange) throws Exception {
            // simulate the error by thrown the exception
            throw new ConnectException("Simulated connection error");
        }

    }

}
