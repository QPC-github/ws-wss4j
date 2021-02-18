/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.wss4j.dom.common;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Callback Handler implementation for the case of processing a Username Token.
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {

    private Map<String, String> users = new HashMap<>();

    public UsernamePasswordCallbackHandler() {
        users.put("wernerd", "verySecret");
        users.put("bob", "security");
        users.put("alice", "securityPassword");
    }

    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
                    pc.setPassword(users.get(pc.getIdentifier()));
                    break;
                }
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }
        }
    }
}
