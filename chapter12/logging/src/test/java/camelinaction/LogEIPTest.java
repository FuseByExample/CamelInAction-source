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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Unit test to demonstrate using the Log EIP for custom logging
 *
 * @version $Revision: 52 $
 */
public class LogEIPTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/rider/orders");
        super.setUp();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        // simulate JMS with the SEDA component
        context.addComponent("jms", context.getComponent("seda"));
        return context;
    }

    @Test
    public void testLogEIP() throws Exception {
        template.sendBodyAndHeader("file://target/rider/orders", "123,4444,20100110,222,1", Exchange.FILE_NAME, "someorder.csv");

        String xml = consumer.receiveBody("jms:queue:orders", 5000, String.class);
        assertEquals("<order><id>123/id><customerId>4444/customerId><date>20100110</date>"
                + "<item><id>222</id><amount>1</amount></itemn></order>", xml);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://target/rider/orders")
                        .log("We got incoming file ${file:name} containing: ${body}")
                        .log(LoggingLevel.DEBUG, "Incoming", "We got incoming file ${file:name} containing: ${body}")
                        .bean(OrderCsvToXmlBean.class)
                        .to("jms:queue:orders");
            }
        };
    }
}