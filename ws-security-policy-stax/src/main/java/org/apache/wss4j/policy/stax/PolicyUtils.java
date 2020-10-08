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
package org.apache.wss4j.policy.stax;

import org.apache.wss4j.policy.model.XPath;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public final class PolicyUtils {

    private PolicyUtils() {
        // complete
    }

    public static List<QName> getElementPath(XPath xPath) {
        List<QName> elements = new ArrayList<>();
        String[] xPathElements = xPath.getXPath().split("/");
        for (int j = 0; j < xPathElements.length; j++) {
            String xPathElement = xPathElements[j];
            if (xPathElement == null || xPathElement.length() == 0) {
                continue;
            }
            String[] elementParts = xPathElement.split(":");
            if (elementParts.length == 2) {
                String ns = xPath.getPrefixNamespaceMap().get(elementParts[0]);
                if (ns == null) {
                    throw new IllegalArgumentException("Namespace not declared");
                }
                elements.add(new QName(ns, elementParts[1]));
            } else {
                elements.add(new QName(elementParts[0]));
            }
        }
        return elements;
    }
}
