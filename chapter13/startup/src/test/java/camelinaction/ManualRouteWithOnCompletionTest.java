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

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Testing how to stop a route when its done using on completion.
 *
 * @version $Revision: 333 $
 */
public class ManualRouteWithOnCompletionTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/inventory");
        super.setUp();
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        // showing a trick how you can append the two routes together
        RouteBuilder[] answer = new RouteBuilder[]{new ManualRouteWithOnCompletion(), new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:update").to("log:update", "mock:update");
            }
        }};
        return answer;
    }

    @Test
    public void testManualRouteWithOnCompletion() throws Exception {
        // notify us when the exchange is done
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        // we actually do not need to use a mock but since we wanted to show the trick
        // in the createRouteBuilders we do both. We could just have relied on using
        // the NotifyBuilder to signal when the exchange is done
        getMockEndpoint("mock:update").expectedMessageCount(2);

        // route should be stopped at startup
        assertTrue("Route should be stopped at startup", context.getRouteStatus("manual").isStopped());

        // then start the route
        context.startRoute("manual");

        // send a file which is picked up and processed
        String input = "4444,57123,Bumper,50\n4444,57124,Fender,87";
        template.sendBodyAndHeader("file:target/inventory/manual", input, Exchange.FILE_NAME, "manual.csv");

        // assert we got the message
        assertMockEndpointsSatisfied();

        // wait for the route to be done
        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        assertTrue("Should match notifier", matches);

        // we gotta wait just a little extra to stop, before querying status
        Thread.sleep(1000);
        // it should have stopped itself
        assertTrue("Route should be stopped", context.getRouteStatus("manual").isStopped());
    }

}