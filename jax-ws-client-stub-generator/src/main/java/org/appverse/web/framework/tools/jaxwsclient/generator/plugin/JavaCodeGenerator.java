package org.appverse.web.framework.tools.jaxwsclient.generator.plugin;

import java.io.File;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; 

import org.appverse.web.framework.backend.ws.handlers.ClientPerformanceMonitorLogicalHandler;
import org.appverse.web.framework.backend.ws.handlers.EnvelopeLoggingSOAPHandler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class JavaCodeGenerator {

	public static final String PACKAGE_NAME="package";
	public static final String CLASS_NAME="class";
	public static final String SERVICE_NAME="service";
	public static final String INTERFACE_NAME="interface";
	public static final String REMOTEWSDL="remotewsdl";
	public static final String DESTDIR="destdir";
	public static final String SPRING = "spring";
 
	/**
	 * Generate live and mock classes
	 * @param args
	 */
	public void generateClass (Map<String,String> args) {
		try {
			String packageName = args.get(PACKAGE_NAME);
			String interfaceName= args.get(INTERFACE_NAME);
			String serviceName =  args.get(SERVICE_NAME);
			String interfaceFullName = packageName + "." + interfaceName;
			String serviceFullName = packageName + "." + serviceName;
			String remoteWSDL = args.get(REMOTEWSDL);
			String target = args.get(DESTDIR);			
			boolean spring = Boolean.valueOf(args.get(SPRING)); 
			
			/* Creating java code model classes */
			JCodeModel jCodeModel = new JCodeModel();
			/* Adding packages here */
			JPackage jp = jCodeModel._package(args.get(PACKAGE_NAME) + ".stub.live");
			JPackage jpMock = jCodeModel._package(args.get(PACKAGE_NAME) + ".stub.mock");
			/* Giving Class Name to Generate */
			JClass abstractClass = jCodeModel.ref("org.appverse.web.framework.backend.ws.client.AbstractWSClient")
					.narrow(jCodeModel.ref(serviceFullName))
					.narrow(jCodeModel.ref(interfaceFullName));
						
			JDefinedClass jc = jp._class(args.get(CLASS_NAME));
			JDefinedClass jcMock = jpMock._class(args.get(CLASS_NAME) + "Mock");
			jc._extends(abstractClass);
			
			if (spring) {
				String compName = args.get(CLASS_NAME).substring(0, 1).toLowerCase() 
						+ args.get(CLASS_NAME).substring(1, args.get(CLASS_NAME).length()) + "Stub";
				jc.annotate(jCodeModel.ref("org.springframework.stereotype.Component")).param("value", compName);
				jcMock.annotate(jCodeModel.ref("org.springframework.stereotype.Component")).param("value", compName);
			}
			 
			//Constructor
			JMethod constructor = jc.constructor(JMod.PUBLIC);
			JBlock constructorBlock = constructor.body(); 
			constructorBlock.directStatement ("this.setBeanClasses(" + serviceName + ".class," + interfaceName + ".class);");			 
			JClass perf = jCodeModel.ref (ClientPerformanceMonitorLogicalHandler.class);		
			JClass log = jCodeModel.ref (EnvelopeLoggingSOAPHandler.class);					 
			constructorBlock.invoke("registerHandler").arg(JExpr._new(perf));
			constructorBlock.invoke("registerHandler").arg(JExpr._new(log)); 
			
			//Get Remote WSDL
			JMethod getUrl = jc.method(JMod.PUBLIC, String.class, "getRemoteWSDLURL");
			getUrl.annotate(jCodeModel.ref(Override.class));
			JBlock block = getUrl.body();	      
			block.directStatement("return \"" + remoteWSDL + "\";");  
		    
			//Methods
			JavaDocBuilder parser = new JavaDocBuilder();
			String path = target + "/" + packageName.replace(".", "/") + "/" + interfaceName + ".java";
			File sourceFolder = new File(path);
			parser.addSource(sourceFolder);
			JavaSource src = parser.getSources()[0];
			JavaPackage pkg      = src.getPackage(); 
			JavaClass[] classes = pkg.getClasses();
			JavaMethod[] methods = classes[0].getMethods();
			for(JavaMethod method :methods){ 
				// do what you have to do with the method  
				JMethod jmCreate;
				JMethod jmCreateMock;
				if (!method.getReturns().getFullQualifiedName().contains(".")) { 
					JClass reference = jCodeModel.ref(pkg.getName() + "." + method.getReturns().getFullQualifiedName());  
					jmCreate = jc.method(JMod.PUBLIC, reference, method.getName());  
					jmCreateMock = jcMock.method(JMod.PUBLIC, reference, method.getName());  
				} else { 	    	
					JClass reference2 = jCodeModel.ref( method.getReturns().getFullQualifiedName());	    	 
					jmCreate = jc.method(JMod.PUBLIC, reference2, method.getName()); 
					jmCreateMock = jcMock.method(JMod.PUBLIC, reference2, method.getName());  
				}

				JavaParameter[] params = method.getParameters();
				List<String> plist = new ArrayList<String>();
				for (JavaParameter p : params) { 
					plist.add(p.getName());
					jmCreate.param(jCodeModel.ref(p.getType().getFullQualifiedName()), p.getName());	
					jmCreateMock.param(jCodeModel.ref(p.getType().getFullQualifiedName()), p.getName());
				} 		    
				jmCreate._throws(Exception.class);  
				jmCreateMock._throws(Exception.class);
				/* Adding method body */
				JBlock jBlock = jmCreate.body();	        
				String argsMethod = "";
				for ( int i = 0; i< plist.size(); i++){
					argsMethod = argsMethod + plist.get(i);
					if ( i != plist.size()-1){
						argsMethod = argsMethod + ",";
					}
				}
				jBlock.directStatement("return getService()." + method.getName() + "(" + argsMethod + ");"); 
				JBlock jBlockMock = jmCreateMock.body();
				jBlockMock.directStatement("//TODO ADD MOCK Code here ");
				jBlockMock.directStatement("return null; ");
			}  
			/* Building class at given location */
			jCodeModel.build(new File(target)); 

		} catch (Exception e) {
			e.printStackTrace();
		} 
	} 
}
