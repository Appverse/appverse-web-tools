/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this
 file, You can obtain one at http://mozilla.org/MPL/2.0/.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the conditions of the Mozilla Public License v2.0
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
package org.appverse.web.tools.codegenerator.util;


import org.apache.velocity.VelocityContext;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PresentationServiceCreator extends AbstractServiceCreator {
    static final String TEMPLATE_NAME = "velocity/presentationImpl.vm";
    static final String PROPERTY_PRESENTATION_INTERFACE = "PRESENTATION_INTERFACE";
    static final String PROPERTY_BUSINESS_CONVERTERS_INJECTION = "BUSINESS_CONVERTERS_INJECTION";
    static final String PROPERTY_PRESENTATION_IMPL_NAME = "PRESENTATION_IMPL_NAME";
    static final String PROPERTY_PRESENTATION_IMPL_PACKAGE = "PRESENTATION_IMPL_PACKAGE";
    static final String PROPERTY_PRESENTATION_INTERFACE_FQFD = "PRESENTATION_INTERFACE_FQFD";
    static final String PROPERTY_PRESENTATION_BODY_CONTENT = "PRESENTATION_BODY_CONTENT";
    static final String PROPERTY_ADDITIONAL_IMPORTS = "ADDITIONAL_IMPORTS";
    static final String PROPERTY_SERVICE_NAME = "SERVICE_NAME";
    static final String PROPERTY_BUSINESS_SERVICE_INJECTION = "BUSINESS_SERVICE_INJECTION";


    public PresentationServiceCreator(ServiceCreatorContext serviceCreatorContext) {
        super(serviceCreatorContext);

    }


    @Override
    VelocityContext getVelocityContext()  {
        VelocityContext vc = new VelocityContext();
        vc.put(PROPERTY_PRESENTATION_INTERFACE, serviceCreatorContext.getPresentationInterfaceClassName());
        vc.put(PROPERTY_PRESENTATION_IMPL_NAME, serviceCreatorContext.getPresentationImplClassName());
        vc.put(PROPERTY_PRESENTATION_IMPL_PACKAGE, serviceCreatorContext.getPresentationImplPackage());
        vc.put(PROPERTY_PRESENTATION_INTERFACE_FQFD, serviceCreatorContext.getFullQualifiedPresentationInterfaceName());
        vc.put(PROPERTY_PRESENTATION_BODY_CONTENT, getData());
        vc.put(PROPERTY_SERVICE_NAME, serviceCreatorContext.firstLetterToLowercase(
                serviceCreatorContext.getPresentationInterfaceClassName()));
        vc.put(PROPERTY_BUSINESS_SERVICE_INJECTION, getBusinessServiceInjection());
        vc.put(PROPERTY_BUSINESS_CONVERTERS_INJECTION, getConvertersInjection());
        //Note additional imports should be the last, as all options may need additional imports.
        vc.put(PROPERTY_ADDITIONAL_IMPORTS, getAdditionalImports());
        return vc;
    }

    private String getData() {
        if( mImports == null ) {
            mImports = new HashMap();
        }
        StringBuilder sbf = new StringBuilder();
        Method[] methodList = serviceCreatorContext.getMethods();
        for( int i=0;i<methodList.length;i++) {
            /** FOR EACH METHOD IN INTERFACE, GENERATE AS FOLLOWS
             @Override
             public UserVO loadUser(final long userId) throws Exception {
             }
             */
            Method m = methodList[i];
            sbf.append("\t").append("@Override").append("\n");
            sbf.append("\t").append("public ");

            //Deal with Return Type
            Type returnType = m.getGenericReturnType();
            sbf.append(processType(returnType, false, false, true));
            sbf.append(" ");

            sbf.append(m.getName()).append("(");
            Type [] types = m.getGenericParameterTypes();
            if( types != null && types.length>0) {
                //sbf.append(" ");
                for( Type type: types) {
                    sbf.append(processType(type, true, false, false));
                    sbf.append(", ");
                }
                sbf.delete(sbf.length() - 2, sbf.length());
            }
            sbf.append(") throws Exception {\n");
            sbf.append("\t\t//TODO code your implementation here\n");
            sbf.append("\t\t");
            sbf.append(getReturnStatementForType(returnType));
            sbf.append("\n\t}").append("\n");
            sbf.append("\n");
        }
        return sbf.toString();
    }


    @Override
    String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    public File getOutputFile() {
        String sDirs = serviceCreatorContext.getFileNamePresentationImpl();
        sDirs = sDirs.substring(0, sDirs.lastIndexOf("/"));
        File fDirs = new File(sDirs);
        fDirs.mkdirs();
        File fNewClass = new File(serviceCreatorContext.getFileNamePresentationImpl());
        return fNewClass;
    }



    public String getBusinessServiceInjection() {
        /**
         @Autowired
         private UserService userService;
         */
        StringBuilder sbf = new StringBuilder();

        mImports.put(serviceCreatorContext.getBusinessInterfaceFullQfdClassName(),"");
        mImports.put(IMPORT_AUTOWIRED,"");
        sbf.append("\t@Autowired\n");
        sbf.append("\tprivate ").append(serviceCreatorContext.getBusinessInterfaceClassName());
        sbf.append(" ").append(serviceCreatorContext.firstLetterToLowercase(serviceCreatorContext.getBusinessInterfaceClassName())).append(";").append("\n");
        return sbf.toString();
    }

    public String getConvertersInjection() {
        Map<String, Class> classObjectsUsed = serviceCreatorContext.getClassObjectsUsed();
        Iterator<String> itKeys = classObjectsUsed.keySet().iterator();
        StringBuilder sbf = new StringBuilder();
        while( itKeys.hasNext() ) {
            String current = itKeys.next();
            Class clModel = classObjectsUsed.get(current);
            sbf.append("\t@Autowired\n");
            String converterClassName = ServiceCreatorHelper.getConverterP2BForBean(current);
            if( converterClassName == null ) {
                converterClassName =
                    serviceCreatorContext.getBusinessModelName(clModel)+ServiceCreatorContext.P2B_SUFFIX;
            } else {
                converterClassName = converterClassName.substring(converterClassName.lastIndexOf(".")+1);
            }
            sbf.append("\tprivate ").append(converterClassName).append(" ");
            sbf.append(serviceCreatorContext.firstLetterToLowercase(converterClassName));
            sbf.append(";\n");
            String stImport = ServiceCreatorHelper.getConverterP2BForBean(current);
            if( stImport != null ) {
                mImports.put(stImport,"");
            } else {
                mImports.put(
                        serviceCreatorContext.getP2BConverterPackage()+"."+
                                serviceCreatorContext.getBusinessModelName(clModel)+ServiceCreatorContext.P2B_SUFFIX,""
                );
            }
        }
        return sbf.toString();
    }
}
