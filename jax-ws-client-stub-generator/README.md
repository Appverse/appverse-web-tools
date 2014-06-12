Appverse Web Tools
======================
This maven plugin will generate JAX-WS code for Web Service client.

##  Appverse WS
The Appverse WS module provides a class to extend that will perform all the necessary code to connect to the web service.
This maven plugin will generate the live and mock implementations for the JAX-WS extending the according to the Appverse WS module. 

## Configuration
The plugin depends on the jaxws-maven-plugin configuration maven plugin. 
    <groupId>org.jvnet.jax-ws-commons</groupId>
	<artifactId>jaxws-maven-plugin</artifactId>
	
It will read the jaxws-maven-plugin configuration:

* wsdlPath - WSDL directory in the case of a local WSDL copy is used.
* wsdlFile - WSDL file in the case of a local WSDL copy is used.
* packageName - The target package name.
* sourceDestDir - The target directory.
* wsdlUrl - The WSDL URL in case of a remote WSDL is used. 

     <plugin>
		<groupId>org.appverse.web.tools.jaxws.client</groupId>
		<artifactId>jax-ws-client-stub-generator</artifactId>
		<configuration>	 
			<remoteWsdl>http://localhost:8090/appverse-web-showcases-ws-service/AccountService?wsdl</remoteWsdl> 
			<springService>true</springService>
			</configuration>
     </plugin>
     
* remoteWsdl - The WSDL URL that will be used by the JAX-WS client. 
* sprinService - If the generated stub will be annotated as a Spring component. 

## Execution
The plugin needs to be executed after the jaxws:wsimport maven goal since it uses the generated code by the wsimport execution.
 
    mvn clean jaxws:wsimport org.appverse.web.tools.jaxws.client:jax-ws-client-stub-generator:generate-jax-ws-client


See the Appverse Web Services Showcase <https://github.com/Appverse/appverse-web-showcases> for a working demo. 
    
## More Information
* **SOAP**: <http://www.w3.org/TR/soap/>
* **JAX-WS**: <http://searchsoa.techtarget.com/definition/JAX-WS> 
* **Maven Plug-in**: <https://jax-ws-commons.java.net/jaxws-maven-plugin/>
* **About this project**: <http://appverse.github.com/appverse-web>
* **About licenses**: <http://www.mozilla.org/MPL/>
* **About The Appverse Project**: <http://appverse.org>

## License

    Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

     This Source Code Form is subject to the terms of the Mozilla Public
     License, v. 2.0. If a copy of the MPL was not distributed with this
     file, You can obtain one at http://mozilla.org/MPL/2.0/.

     Redistribution and use in  source and binary forms, with or without modification, 
     are permitted provided that the  conditions  of the  Mozilla Public License v2.0 
     are met.

     THIS SOFTWARE IS PROVIDED BY THE  COPYRIGHT HOLDERS  AND CONTRIBUTORS "AS IS" AND
     ANY EXPRESS  OR IMPLIED WARRANTIES, INCLUDING, BUT  NOT LIMITED TO,   THE IMPLIED
     WARRANTIES   OF  MERCHANTABILITY   AND   FITNESS   FOR A PARTICULAR  PURPOSE  ARE
     DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
     SHALL THE  COPYRIGHT OWNER  OR  CONTRIBUTORS  BE LIABLE FOR ANY DIRECT, INDIRECT,
     INCIDENTAL,  SPECIAL,   EXEMPLARY,  OR CONSEQUENTIAL DAMAGES  (INCLUDING, BUT NOT
     LIMITED TO,  PROCUREMENT OF SUBSTITUTE  GOODS OR SERVICES;  LOSS OF USE, DATA, OR
     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
     WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
     ARISING  IN  ANY WAY OUT  OF THE USE  OF THIS  SOFTWARE,  EVEN  IF ADVISED OF THE 
     POSSIBILITY OF SUCH DAMAGE.
