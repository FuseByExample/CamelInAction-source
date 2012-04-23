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

import java.io.IOException;
import java.net.ConnectException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Unit test simulating an error using a mock
 *
 * @version $Revision: 201 $
 */
public class SimulateErrorUsingMockTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);

                errorHandler(defaultErrorHandler()
                        .maximumRedeliveries(5).redeliveryDelay(1000));

                onException(IOException.class).maximumRedeliveries(3)
                        .handled(true)
                        .to("mock:ftp");

                from("direct:file")
                        .to("mock:http");
            }
        };
    }

    @Test
    public void testSimulateErrorUsingMock() throws Exception {
        getMockEndpoint("mock:ftp").expectedBodiesReceived("Camel rocks");

        MockEndpoint http = getMockEndpoint("mock:http");
        http.whenAnyExchangeReceived(new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new ConnectException("Simulated connection error"));
            }
        });

        template.sendBody("direct:file", "Camel rocks");

        assertMockEndpointsSatisfied();
    }

}