/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.plugin.oracle;

import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.connector.ConnectorFactory;
import com.facebook.presto.testing.TestingConnectorContext;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.getOnlyElement;

public class TestOraclePlugin
{
    @Test
    public void testCreateConnector()
    {
        Plugin plugin = new OraclePlugin();
        ConnectorFactory factory = getOnlyElement(plugin.getConnectorFactories());
        factory.create("test", ImmutableMap.of("connection-url", "jdbc:oracle:thin//test", "connection-user", "admin", "connection-password", "password"), new TestingConnectorContext());
    }
}
