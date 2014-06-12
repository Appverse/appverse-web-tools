package org.appverse.web.framework.tools.jaxwsclient.generator.plugin.test;

import static org.junit.Assert.assertEquals;

import org.appverse.web.framework.tools.jaxwsclient.generator.plugin.WSDLParser;
import org.junit.Test;

public class TestWsdlParser {
	@Test
	public void testParser () {
		String wsdlPath = "src/test/resources/wsdl/account.wsdl";
		WSDLParser wsdl = new WSDLParser(wsdlPath); 
		System.out.println ("Interface: " + wsdl.getInterface());
		System.out.println ("Service: " + wsdl.getService());
		assertEquals (wsdl.getInterface(),"AccountService");
		assertEquals (wsdl.getService(),"AccountServiceImplService");
	}
}
