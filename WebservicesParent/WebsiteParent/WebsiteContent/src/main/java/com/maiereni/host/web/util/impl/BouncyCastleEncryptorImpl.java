/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
 * ================================================================
 *
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
 */
package com.maiereni.host.web.util.impl;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;

import com.maiereni.host.web.util.DataEncryptor;

/**
 * An implementation of the DataEncryptor that wraps the Bouncy Castle provider (see https://www.bouncycastle.org/java.html) 
 * @author Petre Maierean
 *
 */
public class BouncyCastleEncryptorImpl implements DataEncryptor {
	private X509Certificate certificate;
	private PrivateKey key;
	private Base64 base64;
	
	BouncyCastleEncryptorImpl(@Nonnull final X509Certificate certificate, @Nonnull final PrivateKey key) {
		this.certificate = certificate;
		this.key = key;
		this.base64 = new Base64();
		Security.addProvider(new BouncyCastleProvider());
	}
	
    public byte[] encryptData(@Nonnull final byte[] data) throws Exception {
        CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
        JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(certificate);
        cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
        CMSTypedData msg = new CMSProcessableByteArray(data);
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC").build();
        CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
        return cmsEnvelopedData.getEncoded();
    }
    
    public String encrypt(@Nonnull final String s) throws Exception {
    	byte[] buffer = s.getBytes();
    	byte[] enc = encryptData(buffer);
    	return base64.encodeAsString(enc);
    }

	public byte[] decryptData(@Nonnull final byte[] encryptedData) throws Exception {
        CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);
        Collection<RecipientInformation> recip = envelopedData.getRecipientInfos().getRecipients();
        KeyTransRecipientInformation recipientInfo = (KeyTransRecipientInformation) recip.iterator().next();
        JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(key);
        return recipientInfo.getContent(recipient);
    }
	
	public String decrypt(@Nonnull final String s) throws Exception {
		byte[] buffer = base64.decode(s);
		byte[] decrypt = decryptData(buffer);
		return new String(decrypt);
	}
}
