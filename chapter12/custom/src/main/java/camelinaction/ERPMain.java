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
import org.apache.camel.impl.DefaultCamelContext;

/**
 * A main app to start this example.
 *
 * @version $Revision: 60 $
 */
public class ERPMain {

    public static void main(String[] args) throws Exception {
        ERPMain client = new ERPMain();
        System.out.println("Starting ERPMain... press ctrl + c to stop it");
        client.start();
        System.out.println("... started.");
        Thread.sleep(99999999);
    }

    private void start() throws Exception {
        CamelContext camel = new DefaultCamelContext();

        // add our custom component
        camel.addComponent("erp", new ERPComponent());

        // add the route
        camel.addRoutes(new ERPRoute());

        // and start Camel
        camel.start();
    }

}
