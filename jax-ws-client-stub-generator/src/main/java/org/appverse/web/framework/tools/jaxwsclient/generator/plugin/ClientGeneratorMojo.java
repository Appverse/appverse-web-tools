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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Generate client stubs. Live and Mock. 
 * @author MOCR
 *
 */
@Mojo(name="generate-jax-ws-client")
public class ClientGeneratorMojo extends AbstractMojo { 
	
	private final static String WSDL_DIRECTORY = "wsdlDirectory";
	private final static String WSDL_FILE = "wsdlFile";
	private final static String PACKAGE_NAME = "packageName";
	private final static String DEST_DIR = "sourceDestDir";
	private final static String WSDL_URL = "wsdlUrl";
	
	/**
	 * Remote WSDL location.
	 */
    @Parameter( property = "generate-jax-ws-client.remoteWsdl", defaultValue = "http://" )
    private String remoteWsdl; 
    
    /**
     * Annotate generated classes as Spring services.
     */
    @Parameter( property = "generate-jax-ws-client.springService", defaultValue = "false" )
    private boolean springService; 
        
    @Component
    private MavenProject project;  
    
    /**
     * Plugin execute method
     */
	public void execute() throws MojoExecutionException  {  
		getLog().info( "Generation..." );
		List<Plugin> plugins = project.getBuildPlugins(); 
		boolean found=false;
		for (Plugin plugin:plugins) {
			if (plugin.getArtifactId().equals("jaxws-maven-plugin")) {
				found=true;				
				if (!found) {
					getLog().error( "jaxws-maven-plugin not found." );
					getLog().error( "Skipping generation." );
				} else {
					Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
					execution (config);				 
					List<PluginExecution> execs = plugin.getExecutions();
					for(PluginExecution execution : execs){
						execution ((Xpp3Dom) execution.getConfiguration()); 
					}
				} 
			}
		} 
	} 
	
	/**
	 * Execution for each configuration found at the jaxws-maven-plugin
	 * @param conf
	 */
	private void execution (Xpp3Dom conf) {
    	Map<String,String> config = new HashMap<String,String>();
    	config.put(WSDL_DIRECTORY, extractNestedStrings (WSDL_DIRECTORY, (Xpp3Dom) conf));
    	config.put(WSDL_FILE, extractNestedStrings (WSDL_FILE, (Xpp3Dom) conf));
    	config.put(PACKAGE_NAME, extractNestedStrings (PACKAGE_NAME, (Xpp3Dom) conf));
    	config.put(DEST_DIR, extractNestedStrings (DEST_DIR, (Xpp3Dom) conf));
    	config.put(WSDL_URL, extractNestedStrings (WSDL_URL, (Xpp3Dom) conf));
    	getLog().info("Using configuration from --> jaxws-maven-plugin");
		getLog().info( " - wsdlPath: " + config.get(WSDL_DIRECTORY));
		getLog().info( " - wsdlFile: " + config.get(WSDL_FILE)); 
		getLog().info( " - packageName: " + config.get(PACKAGE_NAME));
		getLog().info( " - sourceDestDir: " + config.get(DEST_DIR));  
		getLog().info( " - wsdlUrl: " + config.get(WSDL_URL));
		WSDLParser wsdl;
		if (config.get(WSDL_DIRECTORY).length() > 1) {
			wsdl = new WSDLParser(config.get(WSDL_DIRECTORY) + "/" + config.get(WSDL_FILE));
		} else {
			wsdl = new WSDLParser (config.get(WSDL_URL));
		}
		String interfaceClass = wsdl.getInterface();
		String serviceClass = wsdl.getService();
		getLog().info( "Interface: " + interfaceClass);
		getLog().info( "Service: " + serviceClass); 
		Map<String,String> args = new HashMap <String, String>();
		args.put(JavaCodeGenerator.PACKAGE_NAME, config.get(PACKAGE_NAME));
		args.put(JavaCodeGenerator.CLASS_NAME, capitalize(serviceClass + "Client"));
		args.put(JavaCodeGenerator.SERVICE_NAME, capitalize(serviceClass));
		args.put(JavaCodeGenerator.INTERFACE_NAME, capitalize(interfaceClass));
		args.put(JavaCodeGenerator.REMOTEWSDL, remoteWsdl); 
		args.put(JavaCodeGenerator.SPRING, String.valueOf(springService));
		args.put(JavaCodeGenerator.DESTDIR,config.get(DEST_DIR));  
		JavaCodeGenerator javaCode = new JavaCodeGenerator ();
		javaCode.generateClass(args);
    }
	
	 /**
     * Extracts nested values from the given config object into a List.
     * 
     * @param childname the name of the first subelement that contains the list
     * @param config the actual config object
     */
    private String extractNestedStrings(String childname, Xpp3Dom config) {
    	String value="";
    	final Xpp3Dom[] children = config.getChildren();
    	for (int i = 0; i < children.length; i++) {
    		final Xpp3Dom child = children[i]; 
    		if (child.getName().equals(childname)) {
    			value = child.getValue();
    			break;
    		}
    		if (child.getChildren().length > 0) {
    			value = extractNestedStrings(childname, child);
    		}
    	} 
    	return value; 
    }
    
	private String capitalize(String stringToCapitazile){
		if (stringToCapitazile.isEmpty()) return stringToCapitazile;
		return stringToCapitazile.substring(0, 1).toUpperCase() + stringToCapitazile.substring(1);
	}
}
