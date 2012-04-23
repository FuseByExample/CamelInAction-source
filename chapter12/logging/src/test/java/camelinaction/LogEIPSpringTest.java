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

import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test to demonstrate using the Log EIP for custom logging using Spring XML
 *
 * @version $Revision: 52 $
 */
public class LogEIPSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/LogEIPSpringTest.xml");
    }

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/rider/orders");
        super.setUp();
    }

    @Test
    public void testLogEIP() throws Exception {
        template.sendBodyAndHeader("file://target/rider/orders", "123,4444,20100110,222,1", Exchange.FILE_NAME, "someorder.csv");

        String xml = consumer.receiveBody("seda:queue:orders", 5000, String.class);
        assertEquals("<order><id>123/id><customerId>4444/customerId><date>20100110</date>"
                + "<item><id>222</id><amount>1</amount></itemn></order>", xml);
    }

}