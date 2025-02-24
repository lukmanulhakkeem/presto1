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

import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.facebook.presto.plugin.jdbc.ConnectionFactory;
import com.facebook.presto.plugin.jdbc.DriverConnectionFactory;
import com.facebook.presto.plugin.jdbc.JdbcClient;
import com.facebook.presto.spi.PrestoException;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.airlift.configuration.ConfigBinder.configBinder;
import static com.facebook.presto.spi.StandardErrorCode.NOT_SUPPORTED;
import static java.util.Objects.requireNonNull;

public class OracleClientModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(JdbcClient.class).to(OracleClient.class)
                .in(Scopes.SINGLETON);
        configBinder(binder).bindConfig(BaseJdbcConfig.class);
        configBinder(binder).bindConfig(OracleConfig.class);
    }

    @Provides
    @Singleton
    public static ConnectionFactory connectionFactory(BaseJdbcConfig config, OracleConfig oracleConfig)
            throws SQLException
    {
        requireNonNull(config, "BaseJdbc config is null");

        boolean iSCredentialsInUrl = JDBCUrlValidator.checkCredentialsInJdbcUrl(config.getConnectionUrl());
        //Throws an exception when username and password present in both connection url and configs
        if (iSCredentialsInUrl && (config.getConnectionUser() != null || config.getConnectionPassword() != null)) {
            System.out.println("both configs and url has the credentials");
            throw new PrestoException(NOT_SUPPORTED, "User credentials in both configs and url");
        }
        Properties connectionProperties = new Properties();
        if (config.getConnectionUser() != null && config.getConnectionPassword() != null) {
            connectionProperties.setProperty("user", config.getConnectionUser());
            connectionProperties.setProperty("password", config.getConnectionPassword());
        }
        connectionProperties.setProperty(OracleConnection.CONNECTION_PROPERTY_INCLUDE_SYNONYMS, String.valueOf(oracleConfig.isSynonymsEnabled()));

        return new DriverConnectionFactory(
                new OracleDriver(),
                config.getConnectionUrl(),
                Optional.empty(),
                Optional.empty(),
                connectionProperties);
    }
    public static class JDBCUrlValidator
    {
        //To check the presence of username and password in the connection url
        public static boolean checkCredentialsInJdbcUrl(String jdbcUrl)
        {
            String regex = "jdbc:oracle:thin:([^/]+?)/([^@]+?)@";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jdbcUrl);

            if (matcher.find()) {
                System.out.println("user and password present in the connection url");
                return true;
            }
            else {
                return false;
            }
        }
    }
}
