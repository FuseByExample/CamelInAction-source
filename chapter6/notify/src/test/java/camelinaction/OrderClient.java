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

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * This is a client which can send an order to a JMS queue.
 * <p/>
 * This client DOES not use Camel at all. Its in fact using pure JMS and a bit "ugly".
 * But hey a lot of code is like this out there.
 *
 * @version $Revision: 154 $
 */
public class OrderClient {

    private final ActiveMQConnectionFactory fac;

    public OrderClient(String url) {
        this.fac = new ActiveMQConnectionFactory(url);
    }

    public void sendOrder(int customerId, Date date, String... itemIds) throws Exception {
        // format the JMS message from the input parameters
        String d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
        String body = customerId + "," + d;
        for (String id : itemIds) {
            body += "," + id;
        }

        // use JMS code to send the message (a bit ugly code but it works)
        Connection con = fac.createConnection();
        con.start();
        Session ses = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = ses.createQueue("order");
        MessageProducer prod = ses.createProducer(dest);
        prod.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        Message msg = ses.createTextMessage(body);
        prod.send(msg);
        prod.close();
        ses.close();
        con.close();
    }

}
