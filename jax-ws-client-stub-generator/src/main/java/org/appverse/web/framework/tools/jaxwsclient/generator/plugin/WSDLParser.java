/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the conditions of the AppVerse Public License v2.0
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.tools.jaxwsclient.generator.plugin;

import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
 
/**
 * WSDL Parser to get the port and the service
 * @author MOCR
 *
 */
public class WSDLParser {
	Definition wsdlInstance; 
	Map bindings;
	Map services;  
	String serviceName;
	String interfaceName;
    /**
     * Constructor 
     * @param baseURI wsdl path
     */
	public WSDLParser (String baseURI) {
		try{
			WSDLFactory factory = WSDLFactory.newInstance();		
			WSDLReader reader = factory.newWSDLReader();
			// pass the URL to the reader for parsing and get back a WSDL definiton
			wsdlInstance = reader.readWSDL(baseURI);
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true); 
			bindings = wsdlInstance.getBindings(); 
			services = wsdlInstance.getServices();	 
			Iterator servTypeIt = services.entrySet().iterator();
			while (servTypeIt.hasNext()) {
				Map.Entry entry = (Map.Entry) servTypeIt.next();
				Service service = (Service) entry.getValue();
				serviceName = service.getQName().getLocalPart(); 
				break;
			}  
			Map allPortTypes = wsdlInstance.getPortTypes();
			Iterator portTypeIt = allPortTypes.entrySet().iterator();
			while (portTypeIt.hasNext()) {
				Map.Entry entry = (Map.Entry) portTypeIt.next();
				PortType portType = (PortType) entry.getValue(); 
				interfaceName = portType.getQName().getLocalPart(); 
				break;
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	/**
	 * Get interface name
	 * @return The interface name as String 
	 */
	public String getInterface() {
		return interfaceName;
	}
	/**
	 * Get service name
	 * @return The service name as String
	 */
	public String getService () {
		return serviceName;
	} 
}
