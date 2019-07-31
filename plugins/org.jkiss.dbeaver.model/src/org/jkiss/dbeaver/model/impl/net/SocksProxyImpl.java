/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 *
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
package org.jkiss.dbeaver.model.impl.net;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.app.DBPPlatform;
import org.jkiss.dbeaver.model.connection.DBPConnectionConfiguration;
import org.jkiss.dbeaver.model.net.DBWForwarder;
import org.jkiss.dbeaver.model.net.DBWHandlerConfiguration;
import org.jkiss.dbeaver.model.net.DBWNetworkHandler;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

import java.io.IOException;

/**
 * SOCKS proxy
 */
public class SocksProxyImpl implements DBWNetworkHandler, DBWForwarder {

    private DBWHandlerConfiguration configuration;

    @Override
    public DBPConnectionConfiguration initializeHandler(DBRProgressMonitor monitor, DBPPlatform platform, DBWHandlerConfiguration configuration, DBPConnectionConfiguration connectionInfo) throws DBException, IOException {
        this.configuration = configuration;
        return null;
    }

    @Override
    public void invalidateHandler(DBRProgressMonitor monitor, DBPDataSource dataSource) throws DBException, IOException {

    }

    @Override
    public boolean matchesParameters(String host, int port) {
        if (host.equals(configuration.getProperties().get(SocksConstants.PROP_HOST))) {
            int socksPort = CommonUtils.toInt(configuration.getProperties().get(SocksConstants.PROP_PORT));
            return socksPort == port;
        }
        return false;
    }

}
