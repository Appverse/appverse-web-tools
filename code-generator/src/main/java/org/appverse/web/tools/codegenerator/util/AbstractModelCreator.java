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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Specializing AbstractCreator for Models.
 */
public abstract class AbstractModelCreator extends AbstractCreator{

    private Class clModel;

    public AbstractModelCreator(ServiceCreatorContext serviceCreatorContext, Class clModel) {
        super(serviceCreatorContext);
        this.clModel = clModel;
    }

    /**
     * @return the Class object from the Presentation Interface.
     */
    public Class getClModel() {
        return clModel;
    }


    private String convertToGet(String typeName) {
        String s = typeName.substring(0,1).toUpperCase();
        return s+typeName.substring(1);
    }

    /**
     * Builds a list of Fields for a specific model layer based on the @see getClModel().
     * @param integration Indicates if generating for Integration layer (true) or Business layer (false).
     * @return A string representing all fields for the target model.
     */
    protected String getFields(boolean integration) {
        Class cl = getClModel();
        Field[] clFields = cl.getDeclaredFields();
        StringBuilder sbf = new StringBuilder();
        for( Field field : clFields) {
            int mods = field.getModifiers();
            if( !Modifier.isStatic(mods) ) { //discard serialVersionUID
                sbf.append("\tprivate ");
                //if field.getType() is from Appverse or App
                if( ServiceCreatorHelper.isChildAppverseModel(field.getType())) {
                    String sType = serviceCreatorContext.fromModelPresentationToBusiness(field.getType().getName());
                    sbf.append(sType.substring(sType.lastIndexOf(".")+1));
                    mImports.put(sType,"");
                    if( integration ) {
                        sbf.append("DTO");
                    }
                } else {
                    sbf.append(field.getType().getSimpleName());
                }
                sbf.append(" ");
                //processImports(field);
                sbf.append(field.getName()).append(";\n");
            }
        }
        return sbf.toString();
    }

    /**
     * Builds all methods (getters and setters) for a specific model layer based on the @see getClModel().
     * @param integration Indicates if generating for Integration layer (true) or Business layer (false).
     * @param includeAnnotations Indicates if default JPA annotations should be included..
     * @return A string representing all methods for fields for the target model.
     */
    protected String getMethods(boolean includeAnnotations, boolean integration) {
        Class cl = getClModel();
        Field[] clFields = cl.getDeclaredFields();
        StringBuilder sbf = new StringBuilder();
        StringBuilder sbfSetters = new StringBuilder();
        for( Field field : clFields) {
            int mods = field.getModifiers();
            if( !Modifier.isStatic(mods) ) { //discard serialVersionUID
                if( includeAnnotations ) {
                    sbf.append("\t").append(ServiceCreatorHelper.getDefaultJPAAnnotation(field.getType().getName()));
                    sbf.append("\n");
                }
                sbf.append("\tpublic ");
                if( ServiceCreatorHelper.isChildAppverseModel(field.getType())) {
                    String sType = serviceCreatorContext.fromModelPresentationToBusiness(field.getType().getName());
                    sbf.append(sType.substring(sType.lastIndexOf(".")+1));
                    mImports.put(sType,"");
                    if( integration ) {
                        sbf.append("DTO");
                    }
                } else {
                    sbf.append(field.getType().getSimpleName()); //TODO
                }

                sbf.append(" ");
                if( field.getType().equals(Boolean.TYPE)) {
                    sbf.append("is");
                } else {
                    sbf.append("get");
                }
                sbf.append(convertToGet(field.getName())).append("() {\n");
                sbf.append("\t\treturn ").append(field.getName()).append(";");
                sbf.append("\n\t}\n\n");

                sbfSetters.append("\tpublic void ");
                sbfSetters.append("set").append(convertToGet(field.getName())).append("(");
                if( ServiceCreatorHelper.isChildAppverseModel(field.getType())) {
                    String sType = serviceCreatorContext.fromModelPresentationToBusiness(field.getType().getName());
                    sbfSetters.append(sType.substring(sType.lastIndexOf(".")+1));
                    mImports.put(sType,"");
                    if( integration ) {
                        sbfSetters.append("DTO");
                    }
                } else {
                    sbfSetters.append(field.getType().getSimpleName());//TODO
                }

                sbfSetters.append(" ").append(field.getName()).append(") {\n");
                sbfSetters.append("\t\tthis.").append(field.getName()).append(" = ").append(field.getName()).append(";\n");
                sbfSetters.append("\t}\n\n");
            }
        }
        sbf.append(sbfSetters);
        return sbf.toString();
    }

}
