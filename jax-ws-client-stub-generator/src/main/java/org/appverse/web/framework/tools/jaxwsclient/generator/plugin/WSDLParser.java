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
