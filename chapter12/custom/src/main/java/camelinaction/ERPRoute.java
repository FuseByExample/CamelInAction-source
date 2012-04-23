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

import org.apache.camel.builder.RouteBuilder;

/**
 * A route which uses the custom ERP component we can manage from JConsole.
 *
 * @version $Revision: 60 $
 */
public class ERPRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=5000")
            .setBody().simple("Hello ERP calling at ${date:now:HH:mm:ss}")
            .to("erp:foo")
            .to("log:reply");
    }
}
