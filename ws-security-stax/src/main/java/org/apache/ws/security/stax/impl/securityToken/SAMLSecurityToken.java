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
package org.apache.ws.security.stax.impl.securityToken;

import org.opensaml.common.SAMLVersion;
import org.apache.ws.security.common.crypto.Crypto;
import org.apache.ws.security.common.saml.SAMLKeyInfo;
import org.apache.ws.security.stax.ext.WSSConstants;
import org.apache.ws.security.stax.ext.WSSecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken;

import javax.security.auth.callback.CallbackHandler;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SAMLSecurityToken extends AbstractSecurityToken {

    private final SAMLVersion samlVersion;
    private final SAMLKeyInfo samlKeyInfo;
    private String issuer;
    private X509Certificate[] x509Certificate;
    private Crypto crypto;

    public SAMLSecurityToken(SAMLVersion samlVersion, SAMLKeyInfo samlKeyInfo, String issuer,
                             WSSecurityContext wsSecurityContext, Crypto crypto, CallbackHandler callbackHandler,
                             String id, WSSConstants.KeyIdentifierType keyIdentifierType) {
        super(wsSecurityContext, callbackHandler, id, keyIdentifierType);
        this.samlVersion = samlVersion;
        this.samlKeyInfo = samlKeyInfo;
        this.issuer = issuer;
        this.crypto = crypto;
    }

    public SAMLSecurityToken(SAMLVersion samlVersion, SAMLKeyInfo samlKeyInfo, WSSecurityContext wsSecurityContext,
                             Crypto crypto, CallbackHandler callbackHandler, String id) {
        super(wsSecurityContext, callbackHandler, id, null);
        this.samlVersion = samlVersion;
        this.samlKeyInfo = samlKeyInfo;
        this.crypto = crypto;
    }
    
    public Crypto getCrypto() {
        return crypto;
    }

    public boolean isAsymmetric() {
        return true;
    }

    protected Key getKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage,
                         String correlationID) throws XMLSecurityException {
        return samlKeyInfo.getPrivateKey();
    }

    protected PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage,
                                  String correlationID) throws XMLSecurityException {
        PublicKey publicKey = samlKeyInfo.getPublicKey();
        if (publicKey == null) {
            publicKey = getX509Certificates()[0].getPublicKey();
        }
        return publicKey;
    }

    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        if (this.x509Certificate == null) {
            this.x509Certificate = samlKeyInfo.getCerts();
        }
        return this.x509Certificate;
    }

    public void verify() throws XMLSecurityException {
        try {
            X509Certificate[] x509Certificates = getX509Certificates();
            if (x509Certificates != null && x509Certificates.length > 0) {
                x509Certificates[0].checkValidity();
                getCrypto().verifyTrust(x509Certificates);
            }
        } catch (CertificateExpiredException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
        } catch (CertificateNotYetValidException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
        }
    }

    //todo move to super class?
    public SecurityToken getKeyWrappingToken() {
        return null;
    }

    public XMLSecurityConstants.TokenType getTokenType() {
        if (samlVersion == SAMLVersion.VERSION_10) {
            return WSSConstants.Saml10Token;
        } else if (samlVersion == SAMLVersion.VERSION_11) {
            return WSSConstants.Saml11Token;
        }
        return WSSConstants.Saml20Token;
    }

    public SAMLKeyInfo getSamlKeyInfo() {
        //todo AlgoSecEvent?
        return samlKeyInfo;
    }

    public SAMLVersion getSamlVersion() {
        return samlVersion;
    }

    public String getIssuer() {
        return issuer;
    }
}