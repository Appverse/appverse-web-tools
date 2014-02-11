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
import java.lang.reflect.*;
import java.util.HashMap;

public class BusinessInterfaceServiceCreator extends AbstractServiceCreator {
    static final String TEMPLATE_NAME = "velocity/businessInterface.vm";
    static final String PROPERTY_BUSINESS_INTERFACE_NAME = "BUSINESS_INTERFACE_NAME";
    static final String PROPERTY_BUSINESS_INTERFACE_BODY_CONTENT = "BUSINESS_INTERFACE_BODY_CONTENT";
    static final String PROPERTY_ADDITIONAL_IMPORTS = "ADDITIONAL_IMPORTS";
    static final String PROPERTY_BUSINESS_INTERFACE_PACKAGE = "BUSINESS_INTERFACE_PACKAGE";

    public BusinessInterfaceServiceCreator( ServiceCreatorContext serviceCreatorContext) {
        super( serviceCreatorContext);
    }

    @Override
    VelocityContext getVelocityContext() {
        VelocityContext vc = new VelocityContext();
        vc.put(PROPERTY_BUSINESS_INTERFACE_NAME, serviceCreatorContext.getBusinessInterfaceClassName());
        vc.put(PROPERTY_BUSINESS_INTERFACE_PACKAGE, serviceCreatorContext.getBusinessInterfacePackage());
        vc.put(PROPERTY_BUSINESS_INTERFACE_BODY_CONTENT, getData());
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
        for( Method m : methodList) {
            /** FOR EACH METHOD IN INTERFACE, GENERATE AS FOLLOWS
             @Override
             public UserVO loadUser(final long userId) throws Exception {
             }
             */
            sbf.append("\t").append("public ");

            //Deal with Return Type
            Type returnType = m.getGenericReturnType();
            sbf.append(processType(returnType, false, true, true));
            sbf.append(" ");

            sbf.append(m.getName()).append("(");
            Type [] types = m.getGenericParameterTypes();
            if( types != null && types.length>0) {
                sbf.append(" ");
                for( Type type: types) {
                    sbf.append(processType(type, true, true, false));
                    sbf.append(", ");
                }
                sbf.delete(sbf.length() - 2, sbf.length());
            }
            sbf.append(") throws Exception").append(";");

            sbf.append("\n\n");
        }
        return sbf.toString();
    }

    @Override
    String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    public File getOutputFile() {
        String sDirs = serviceCreatorContext.getFileNameBusinessInterface();
        sDirs = sDirs.substring(0, sDirs.lastIndexOf("/"));
        File fDirs = new File(sDirs);
        fDirs.mkdirs();

        File fNewClass = new File(serviceCreatorContext.getFileNameBusinessInterface());
        return fNewClass;
    }
}
