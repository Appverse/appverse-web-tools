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


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractServiceCreator extends AbstractCreator {
    static final String IMPORT_AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired";
    private Class clModel;

    public AbstractServiceCreator(ServiceCreatorContext serviceCreatorContext, Class clModel) {
        this(serviceCreatorContext);
        this.clModel = clModel;
    }

    public AbstractServiceCreator(ServiceCreatorContext serviceCreatorContext) {
        super(serviceCreatorContext);
    }

    public Class getClModel() {
        return clModel;
    }

    protected String getClassString(Class cl, boolean toBusiness) {
        String stReturn = "";
        if( toBusiness) {
            stReturn = serviceCreatorContext.fromModelPresentationToBusiness(cl.getName());
            mImports.put(stReturn,"");
            stReturn = stReturn.substring(stReturn.lastIndexOf(".")+1);
        } else {
            stReturn = cl.getSimpleName();
            mImports.put(cl.getName(),"");

        }
        return stReturn;
    }

    protected String getReturnStatementForType(Type type) {
        StringBuilder sbf = new StringBuilder();
        if( type instanceof ParameterizedType ) {
            sbf.append("return null;");
        } else {
            Class cl = (Class) type;
            if( cl.isPrimitive()) {
                //void not considered!
                String typeName = cl.getName();
                if (typeName.equals("byte"))
                    sbf.append("return 1;");
                else if (typeName.equals("short"))
                    sbf.append("return 1;");
                else if (typeName.equals("int"))
                    sbf.append("return 1;");
                else if (typeName.equals("long"))
                    sbf.append("return 1;");
                else if (typeName.equals("char"))
                    sbf.append("return 1;");
                else if (typeName.equals("float"))
                    sbf.append("return 1f;");
                else if (typeName.equals("double"))
                    sbf.append("return 1d;");
                else if (typeName.equals("boolean"))
                    sbf.append("return false;");
                else if (typeName.equals("void"))
                    sbf.append("return;");

            } else {
                sbf.append("return null;");
            }
        }
        return sbf.toString();
    }

    protected String processType(Type type, boolean addName, boolean toBusiness, boolean returnType) {
        StringBuilder sb = new StringBuilder();
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            Class clReturnType = (Class) ptype.getRawType();
            Type[] typeArguments = ptype.getActualTypeArguments();
            if (ServiceCreatorHelper.isChildAppverseModel(clReturnType)) {
                String stReturn = "";
                sb.append(getClassString(clReturnType,toBusiness));
                /*if( toBusiness) {
                    stReturn = serviceCreatorContext.fromModelPresentationToBusiness(clReturnType.getName());
                    sb.append(stReturn.substring(stReturn.lastIndexOf(".")+1));
                    mImports.put(stReturn,"");
                } else {
                    sb.append(getClassString())
                } */
                serviceCreatorContext.addBusinessModelBean(clReturnType, returnType);
            } else {
                sb.append(clReturnType.getSimpleName());
                if( !clReturnType.isPrimitive() ) {
                     mImports.put(clReturnType.getName(),"");
                }
                /*if( this instanceof PresentationServiceCreator) {
                    System.out.println("   test ["+clReturnType.getSimpleName()+"]");
                } */
            }
            sb.append("<");
            for (Type typeArgument : typeArguments) {
                Class typeArgClass = (Class) typeArgument;
                if (ServiceCreatorHelper.isChildAppverseModel(typeArgClass)) {                 /*
                    String st = serviceCreatorContext.fromModelPresentationToBusiness(typeArgClass.getName());
                    sb.append(st.substring(st.lastIndexOf(".")+1));
                    mImports.put(st,"");                                */
                    sb.append(getClassString(typeArgClass, toBusiness));
                    serviceCreatorContext.addBusinessModelBean(typeArgClass, returnType);
                } else {
                    processImports(typeArgClass);
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
            } else if (ServiceCreatorHelper.isChildAppverseModel(clReturnType)) {
                serviceCreatorContext.addBusinessModelBean(clReturnType, returnType);
                                                                      /*
                String sTmp = serviceCreatorContext.fromModelPresentationToBusiness(clReturnType.getName());
                mImports.put(sTmp, "");//wrong adding USer instead of fully qualified class name.
                String sTmpSimpleName = sTmp.substring(sTmp.lastIndexOf(".")+1);
                sb.append(sTmpSimpleName);                          */
                String sTmpSimpleName = getClassString(clReturnType, toBusiness);

                sb.append(sTmpSimpleName);
                if( addName) {
                    sb.append(" ");
                    String sTmp2 = sTmpSimpleName.substring(0,1).toLowerCase()+sTmpSimpleName.substring(1);
                    sb.append(sTmp2);
                }
                //mImports.put(serviceCreatorContext.getBusinessModelPackage()+"."+ServiceCreatorHelper.fromModelPresentationToBusiness(clReturnType.getName()), "");
                //processImports(clReturnType);
            } else {
                sb.append(clReturnType.getSimpleName());
                processImports(clReturnType);
                //mImports.put(clReturnType.getName(), "");
                if( addName ) {
                    sb.append(" ").append(clReturnType.getSimpleName().toLowerCase());
                }
            }

        }
        return sb.toString();
    }

}
