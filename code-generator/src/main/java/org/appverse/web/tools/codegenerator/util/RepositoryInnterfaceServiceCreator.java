package org.appverse.web.tools.codegenerator.util;


import org.apache.velocity.VelocityContext;

import java.io.File;
import java.lang.reflect.*;
import java.util.HashMap;

public class RepositoryInnterfaceServiceCreator extends AbstractServiceCreator {
    static final String TEMPLATE_NAME = "velocity/businessInterface.vm";
    static final String PROPERTY_BUSINESS_INTERFACE_NAME = "BUSINESS_INTERFACE_NAME";
    static final String PROPERTY_BUSINESS_INTERFACE_BODY_CONTENT = "BUSINESS_INTERFACE_BODY_CONTENT";
    static final String PROPERTY_ADDITIONAL_IMPORTS = "ADDITIONAL_IMPORTS";
    static final String PROPERTY_BUSINESS_INTERFACE_PACKAGE = "BUSINESS_INTERFACE_PACKAGE";

    public RepositoryInnterfaceServiceCreator(ClassLoader classLoader, ServiceCreatorContext serviceCreatorContext, String srcOutputDir) {
        super(classLoader, serviceCreatorContext, srcOutputDir);
    }

    @Override
    VelocityContext getVelocityContext() throws ClassNotFoundException {
        VelocityContext vc = new VelocityContext();
        vc.put(PROPERTY_BUSINESS_INTERFACE_NAME, serviceCreatorContext.getBusinessInterfaceClassName());
        vc.put(PROPERTY_BUSINESS_INTERFACE_PACKAGE, serviceCreatorContext.getBusinessInterfacePackage());
        vc.put(PROPERTY_BUSINESS_INTERFACE_BODY_CONTENT, getData());
        //Note additional imports should be the last, as all options may need additional imports.
        vc.put(PROPERTY_ADDITIONAL_IMPORTS, getAdditionalImports());
        return vc;
    }

    private String processType(Type type, boolean addName) {
        StringBuffer sb = new StringBuffer();
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            //Class c =
            Class clReturnType = (Class) ptype.getRawType();// m.getReturnType();
            Type[] typeArguments = ptype.getActualTypeArguments();
            if (clReturnType.getPackage().getName().startsWith("org.appverse")) {
                //only convert to business if it is a framework bean.
                //TODO find another way for this if, since apps won't be org.appverse package (only showcase is)...
                //clReturnType.isAssignableFrom(AbstractBean.class);
                //should be clReturnType.isAssignableFor(AbstractBean or Abstrac
                sb.append(ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName()));
            } else {
                sb.append(clReturnType.getSimpleName());
            }
            sb.append("<");
            for (Type typeArgument : typeArguments) {
                Class typeArgClass = (Class) typeArgument;
                mImports.put(typeArgClass.getName(), "");
                //sbf.append(typeArgClass.getSimpleName());
                if (typeArgClass.getPackage().getName().startsWith("org.appverse")) {
                    //only convert to business if it is a framework bean.
                    //TODO find another way for this if, since apps won't be org.appverse package (only showcase is)...
                    //clReturnType.isAssignableFrom(AbstractBean.class);
                    //should be clReturnType.isAssignableFor(AbstractBean or Abstrac
                    sb.append(ServiceCreatorHelper.fromModelPresentationToBusiness(typeArgClass.getName()));
                } else {
                    sb.append(typeArgClass.getSimpleName());
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(">");
            if( addName) {
                sb.append(" ");
                sb.append(clReturnType.getSimpleName().toLowerCase());
                //sb.append(clParam.getSimpleName().toLowerCase());
            }
        } else {
            Class clReturnType = (Class) type;
            if (clReturnType.isPrimitive()) {
                sb.append(clReturnType.getSimpleName());
                if( addName ) {
                    sb.append(" ").append(clReturnType.getSimpleName().toLowerCase().substring(0,1));
                }
            } else if (clReturnType.getPackage().getName().startsWith("org.appverse")) {
                //sb.append(ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName()));
                //mImports.put(serviceCreatorContext.getBusinessModelPackage() + "." + ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName()), "");

                String sTmp = ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName());
                //mImports.put(sTmp, "");//wrong adding USer instead of fully qualified class name.
                String sTmpSimpleName = sTmp.substring(sTmp.lastIndexOf(".")+1);
                sb.append(sTmpSimpleName);
                if( addName) {
                    sb.append(" ");
                    String sTmp2 = sTmpSimpleName.substring(0,1).toLowerCase()+sTmpSimpleName.substring(1);
                    sb.append(sTmp2);
                }
                mImports.put(serviceCreatorContext.getBusinessModelPackage()+"."+ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName()), "");
            } else {
                sb.append(clReturnType.getSimpleName());
                mImports.put(clReturnType.getName(), "");
                if( addName ) {
                    sb.append(" ").append(clReturnType.getSimpleName().toLowerCase());
                }
            }

        }
        return sb.toString();
    }

    private String getData() {
        if( mImports == null ) {
            mImports = new HashMap();
        }
        StringBuffer sbf = new StringBuffer();
        Method[] methodList = serviceCreatorContext.getMethods();
        for( int i=0;i<methodList.length;i++) {
            /** FOR EACH METHOD IN INTERFACE, GENERATE AS FOLLOWS
             @Override
             public UserVO loadUser(final long userId) throws Exception {
             }
             */
            Method m = methodList[i];
            sbf.append("\t").append("public ");

            //Deal with Return Type
            Type returnType = m.getGenericReturnType();
            sbf.append(processType(returnType, false));
            sbf.append(" ");

            sbf.append(m.getName()).append("(");
            Type [] types = m.getGenericParameterTypes();
            if( types != null && types.length>0) {
                sbf.append(" ");
                for( Type type: types) {
                    sbf.append(processType( type, true ));
                    sbf.append(", ");
                }
                sbf.delete(sbf.length() - 2, sbf.length());
            }
            sbf.append(") throws Exception").append(";");

            sbf.append("\n");
        }
        return sbf.toString();
    }

    @Override
    String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    public File getOutputFile() throws ClassNotFoundException {
        String sDirs = serviceCreatorContext.getFileNameBusinessInterface();
        sDirs = sDirs.substring(0, sDirs.lastIndexOf("/"));
        File fDirs = new File(sDirs);
        fDirs.mkdirs();

        File fNewClass = new File(serviceCreatorContext.getFileNameBusinessInterface());
        return fNewClass;
    }
}
