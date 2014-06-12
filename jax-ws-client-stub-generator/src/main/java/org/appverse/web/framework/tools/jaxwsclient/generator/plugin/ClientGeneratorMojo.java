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
		args.put(JavaCodeGenerator.CLASS_NAME, serviceClass + "Client");
		args.put(JavaCodeGenerator.SERVICE_NAME, serviceClass);
		args.put(JavaCodeGenerator.INTERFACE_NAME, interfaceClass);
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
}
