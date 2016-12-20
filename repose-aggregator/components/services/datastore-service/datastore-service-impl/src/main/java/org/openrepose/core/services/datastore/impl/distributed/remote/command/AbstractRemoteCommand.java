/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * Repose
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2010 - 2015 Rackspace US, Inc.
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package org.openrepose.core.services.datastore.impl.distributed.remote.command;

import org.openrepose.commons.utils.http.CommonHttpHeader;
import org.openrepose.core.services.datastore.distributed.RemoteBehavior;
import org.openrepose.core.services.datastore.impl.distributed.CacheRequest;
import org.openrepose.core.services.datastore.impl.distributed.DatastoreHeader;
import org.openrepose.core.services.datastore.impl.distributed.remote.RemoteCommand;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRemoteCommand implements RemoteCommand {

    private final InetSocketAddress remoteEndpoint;
    private final String cacheObjectKey;
    private String hostKey;
    private String tracingHeader;
    private String connPoolId;
    private boolean useHttps;

    public AbstractRemoteCommand(String cacheObjectKey, InetSocketAddress remoteEndpoint, String connPoolId, boolean useHttps) {
        this.cacheObjectKey = cacheObjectKey;
        this.remoteEndpoint = remoteEndpoint;
        this.connPoolId = connPoolId;
        this.useHttps = useHttps;
    }

    public String getUrl() {
        return CacheRequest.urlFor(getRemoteEndpoint(), getCacheObjectKey(), useHttps);
    }

    public String getBaseUrl() {
        return CacheRequest.urlFor(remoteEndpoint, useHttps);
    }

    public String getConnectionPoolId() {
        return connPoolId;
    }

    protected byte[] getBody() {
        return new byte[0];
    }

    protected Map<String, String> getHeaders(RemoteBehavior remoteBehavior) {
        Map<String, String> headers = new HashMap<>();
        headers.put(DatastoreHeader.HOST_KEY.toString(), hostKey);
        headers.put(CommonHttpHeader.TRACE_GUID.toString(), tracingHeader);
        headers.put(DatastoreHeader.REMOTE_BEHAVIOR.toString(), remoteBehavior.name());

        return headers;
    }

    protected InetSocketAddress getRemoteEndpoint() {
        return remoteEndpoint;
    }

    protected String getCacheObjectKey() {
        return cacheObjectKey;
    }

    @Override
    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }

    @Override
    public void setTracingHeader(String tracingHeader) {
        this.tracingHeader = tracingHeader;
    }
}
