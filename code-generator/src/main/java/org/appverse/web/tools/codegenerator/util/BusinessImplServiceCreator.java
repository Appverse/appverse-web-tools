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

public class BusinessImplServiceCreator extends AbstractServiceCreator {
    static final String TEMPLATE_NAME = "velocity/businessImpl.vm";
    static final String PROPERTY_BUSINESS_IMPL_PACKAGE = "BUSINESS_IMPL_PACKAGE";
    static final String PROPERTY_BUSINESS_CONVERTERS_INJECTION = "BUSINESS_CONVERTERS_INJECTION";
    static final String PROPERTY_ADDITIONAL_IMPORTS = "ADDITIONAL_IMPORTS";
    static final String PROPERTY_BUSINESS_SERVICE_NAME = "BUSINESS_SERVICE_NAME";
    static final String PROPERTY_BUSINESS_SERVICE_CLASS_NAME = "BUSINESS_SERVICE_CLASS_NAME";
    static final String PROPERTY_BUSINESS_INTERFACE_NAME = "BUSINESS_INTERFACE_NAME";
    static final String PROPERTY_BUSINESS_SERVICE_INJECTION = "BUSINESS_SERVICE_INJECTION";
    static final String PROPERTY_BUSINESS_IMPL_BODY_CONTENT = "BUSINESS_IMPL_BODY_CONTENT";

    public BusinessImplServiceCreator(ServiceCreatorContext serviceCreatorContext) {
        super(serviceCreatorContext);
    }

    @Override
    VelocityContext getVelocityContext() {
        VelocityContext vc = new VelocityContext();
        vc.put(PROPERTY_BUSINESS_IMPL_PACKAGE, serviceCreatorContext.getBusinessImplPackage());
        vc.put(PROPERTY_BUSINESS_SERVICE_NAME, serviceCreatorContext.firstLetterToLowercase(
                serviceCreatorContext.getBusinessInterfaceClassName()
        ));
        vc.put(PROPERTY_BUSINESS_SERVICE_CLASS_NAME, serviceCreatorContext.getBusinessImplClassName());
        mImports.put(serviceCreatorContext.getBusinessInterfaceFullQfdClassName(),"");
        vc.put(PROPERTY_BUSINESS_INTERFACE_NAME, serviceCreatorContext.getBusinessInterfaceClassName());
        vc.put(PROPERTY_BUSINESS_SERVICE_INJECTION, getBusinessServiceInjection());
        vc.put(PROPERTY_BUSINESS_IMPL_BODY_CONTENT, getData());
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
            sbf.append("\t").append("public ");

            //Deal with Return Type
            Type returnType = m.getGenericReturnType();
            sbf.append(processType(returnType, false, true, true));
            sbf.append(" ");

            sbf.append(m.getName()).append("(");
            Type [] types = m.getGenericParameterTypes();
            if( types != null && types.length>0) {
                for( Type type: types) {
                    sbf.append(processType(type, true, true, false));
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
        String sDirs = serviceCreatorContext.getFileNameBusinessImpl();
        sDirs = sDirs.substring(0, sDirs.lastIndexOf("/"));
        File fDirs = new File(sDirs);
        fDirs.mkdirs();

        File fNewClass = new File(serviceCreatorContext.getFileNameBusinessImpl());
        return fNewClass;
    }

    public String getBusinessServiceInjection() {
        if( mImports == null ) {
            mImports = new HashMap();
        }
        StringBuilder sbf = new StringBuilder();
        Map<String,Class> models = serviceCreatorContext.getBusinessModelBeans();
        Iterator<String> itKeys = models.keySet().iterator();
        while( itKeys.hasNext()) {
            String current = itKeys.next();
            mImports.put(serviceCreatorContext.getIntegrationInterfaceFullQfdClassName(models.get(current)),"");
            mImports.put(IMPORT_AUTOWIRED,"");
            sbf.append("\t@Autowired\n");
            sbf.append("\tprivate ").append(serviceCreatorContext.getIntegrationInterfaceClassName(models.get(current)));
            sbf.append(" ").append(serviceCreatorContext.firstLetterToLowercase(serviceCreatorContext.getIntegrationInterfaceClassName(models.get(current)))).append(";").append("\n");
        }
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
            sbf.append("\tprivate ");

            String test = serviceCreatorContext.fromModelPresentationToBusiness(clModel.getName());
            String test2 = ServiceCreatorHelper.getConverterB2IForBean(test);
            if( test2 == null ) {
                test2 = serviceCreatorContext.getBusinessModelName(clModel)+ServiceCreatorContext.B2I_SUFFIX;
            } else {
                test2 = test2.substring(test2.lastIndexOf(".")+1);
            }
            sbf.append(test2).append(" ");
            sbf.append(serviceCreatorContext.firstLetterToLowercase(test2));
            sbf.append(";\n");
            String stImport = ServiceCreatorHelper.getConverterB2IForBean(current);
            if( stImport != null ) {
                mImports.put(stImport,"");
            } else {
                mImports.put(
                        serviceCreatorContext.getB2IConverterPackage()+"."+
                                serviceCreatorContext.getBusinessModelName(clModel)+ServiceCreatorContext.B2I_SUFFIX,""
                );
            }
        }
        return sbf.toString();
    }
}
