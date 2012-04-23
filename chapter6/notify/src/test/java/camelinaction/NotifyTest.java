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

import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Various unit tests which demonstrates using NotifyBuilder.
 * See also the <a href="http://camel.apache.org/notifybuilder.html">online documentation</a>
 *
 * @version $Revision: 306 $
 */
public class NotifyTest extends CamelTestSupport {

    @Test
    public void testNotifyFrom() throws Exception {
        // use from to indicate it should only be messages originating from the given endpoint
        NotifyBuilder notify = new NotifyBuilder(context)
                .from("seda:order").whenDone(1).create();

        template.sendBody("seda:quote", "Camel rocks");
        template.sendBody("seda:order", "123,2010-04-20'T'15:47:59,4444,5555");

        boolean matches = notify.matches(1, TimeUnit.SECONDS);
        assertTrue(matches);

        SedaEndpoint confirm = context.getEndpoint("seda:confirm", SedaEndpoint.class);
        assertEquals(1, confirm.getExchanges().size());
        assertEquals("OK,123,2010-04-20'T'15:47:59,4444,5555", confirm.getExchanges().get(0).getIn().getBody());
    }

    @Test
    public void testNotifyWhenAnyDoneMatches() throws Exception {
        // use a predicate to indicate when a certain message is done
        NotifyBuilder notify = new NotifyBuilder(context)
                .from("seda:order").whenAnyDoneMatches(body().isEqualTo("OK,123,2010-04-20'T'15:48:00,2222,3333")).create();

        // send in 2 messages. Its the 2nd message we want to test
        template.sendBody("seda:order", "123,2010-04-20'T'15:47:59,4444,5555");
        template.sendBody("seda:order", "123,2010-04-20'T'15:48:00,2222,3333");

        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        assertTrue(matches);

        SedaEndpoint confirm = context.getEndpoint("seda:confirm", SedaEndpoint.class);
        // there should be 2 messages on the confirm queue
        assertEquals(2, confirm.getExchanges().size());
        // and the 2nd message should be the message we wanted to test for
        assertEquals("OK,123,2010-04-20'T'15:48:00,2222,3333", confirm.getExchanges().get(1).getIn().getBody());
    }

    @Test
    public void testNotifyOr() throws Exception {
        // shows how to stack multiple expressions using binary operations (or)
        NotifyBuilder notify = new NotifyBuilder(context)
                .from("seda:quote").whenReceived(1).or().whenFailed(1).create();

        template.sendBody("seda:quote", "Camel rocks");
        template.sendBody("seda:order", "123,2010-04-20'T'15:48:00,2222,3333");

        boolean matches = notify.matches(5, TimeUnit.SECONDS);
        assertTrue(matches);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:order")
                    .choice()
                    .when().method(OrderService.class, "validateOrder")
                        .bean(OrderService.class, "processOrder").to("seda:confirm")
                    .otherwise()
                        .to("seda:invalid")
                    .end();

                from("seda:quote")
                    .delay(2000)
                    .to("log:quote");
            }
        };
    }

}
