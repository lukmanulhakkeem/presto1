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
package com.facebook.presto.plugin.jdbc;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public class JdbcMetadataFactory
{
    private final JdbcMetadataCache jdbcMetadataCache;
    private final JdbcClient jdbcClient;
    private final boolean allowDropTable;
    private final BaseJdbcConfig baseJdbcConfig;

    @Inject
    public JdbcMetadataFactory(JdbcMetadataCache jdbcMetadataCache, JdbcClient jdbcClient, JdbcMetadataConfig config, BaseJdbcConfig baseJdbcConfig)
    {
        this.jdbcMetadataCache = requireNonNull(jdbcMetadataCache, "jdbcMetadataCache is null");
        this.jdbcClient = requireNonNull(jdbcClient, "jdbcClient is null");
        requireNonNull(config, "config is null");
        this.allowDropTable = config.isAllowDropTable();
        this.baseJdbcConfig = requireNonNull(baseJdbcConfig, "baseJdbcConfig is null");
    }

    public JdbcMetadata create()
    {
        return new JdbcMetadata(jdbcMetadataCache, jdbcClient, allowDropTable, baseJdbcConfig);
    }
}
