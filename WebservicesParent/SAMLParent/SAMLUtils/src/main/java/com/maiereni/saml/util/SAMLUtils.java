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
package com.maiereni.saml.util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;

/**
 * Utility class to simplify the creating of openSAML 3 objects and to marshal/unmarshal them to/from String
 * @author Petre Maierean
 *
 */
public class SAMLUtils {
	private static final String DEFAULT_ELEMENT_NAME = "DEFAULT_ELEMENT_NAME";
    private static RandomIdentifierGenerationStrategy secureRandomIdGenerator;
    private static TransformerFactory transformerFactory;
    private static DocumentBuilderFactory documentBuilderFactory;
    static {
    	try {
	    	InitializationService.initialize();
	        secureRandomIdGenerator = new RandomIdentifierGenerationStrategy();
	        transformerFactory = TransformerFactory.newInstance();
	    	documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    	documentBuilderFactory.setNamespaceAware(true);
    	}
    	catch(Exception e) {
    		throw new java.lang.ExceptionInInitializerError(e);
    	}
    }
    
    private XMLObjectBuilderFactory builderFactory;
    private MarshallerFactory marshallerFactory;
    private UnmarshallerFactory unmarshallerFactory;
    
    public SAMLUtils() {
    	builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
    	marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
    	unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
    }

    /**
     * Generate a secure random Id
     * @return
     */
    public String generateSecureRandomId() {
        return secureRandomIdGenerator.generateIdentifier();
    }

    
    /**
     * Builds a SAML object
     * @param qName
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> T newXMLObject(final QName qName, @Nonnull final Class<? extends XMLObject> clazz) throws IllegalAccessException, NoSuchFieldException {
    	QName q = qName;
    	if (qName == null) {
    		q = getQName(clazz);
    	}
    	Object o = builderFactory.getBuilder(q).buildObject(q);
   		return (T)clazz.cast(o);
    }

    /**
     * Builds a SAML object from XML Element
     * @param clazz
     * @param el
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T extends XMLObject> T fromElement(@Nonnull final Class<? extends XMLObject> clazz, @Nonnull final Element el) throws UnmarshallingException {
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(el);
		XMLObject xmlObject = unmarshaller.unmarshall(el);
    	return (T) clazz.cast(xmlObject);
    }
    
    /**
     * Builds a SAML object from a string 
     * @param clazz
     * @param s
     * @return
     * @throws UnmarshallingException
     */
    public <T extends XMLObject> T fromString(@Nonnull final Class<? extends XMLObject> clazz, @Nonnull final String s) 
    	throws UnmarshallingException, ParserConfigurationException, IOException, SAXException {
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	try (ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes())) {
    		Document document = documentBuilder.parse(is);
    		return fromElement(clazz, document.getDocumentElement());
    	}
    }
    
    /**
     * Marshals an XMLObject to string
     * @param o
     * @return
     * 
     */
    public String toString(@Nonnull final XMLObject o) throws TransformerConfigurationException, TransformerException, MarshallingException, IOException {
        try (StringWriter sw = new StringWriter()) {
        	Element el = toElement(o);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(el), new StreamResult(sw));
            return sw.toString();
        }
    }
    /**
     * Marshals the argument to an XML element
     * @param o
     * @return
     * @throws MarshallingException
     */
    public Element toElement(@Nonnull final XMLObject o) throws MarshallingException {
       	Marshaller marshaller = marshallerFactory.getMarshaller(o);
      	return marshaller.marshall(o);
    }
    
    private <T> QName getQName(final Class<T> clazz) throws IllegalAccessException, NoSuchFieldException {
   		Field f = clazz.getField(DEFAULT_ELEMENT_NAME);
    	return (QName)f.get(null);
    }
    
}
