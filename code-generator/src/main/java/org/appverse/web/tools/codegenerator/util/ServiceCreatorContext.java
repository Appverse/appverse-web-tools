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


import java.lang.reflect.Method;
import java.util.*;

/**
 * A helper class for code generation.
 * It is able to derive all needed names from the Presentation interface and model beans.
 */
public class ServiceCreatorContext {
    public static final String P2B_SUFFIX = "P2BBeanConverter";
    public static final String B2I_SUFFIX = "B2IBeanConverter";

    private String fullQualifiedPresentationInterfaceName;

    private String presentationInterfaceClassName;

    private String fullQualifiedPresentationImplName;

    private String presentationImplClassName;

    private String businessInterfaceFullQfdClassName;

    private String businessInterfaceClassName;

    private String businessImplFullQfdClassName;

    private String businessImplClassName;

    private String integrationInterfaceFullQfdBaseName;

    private String integrationImplFullQfdClassBaseName;

    private String presentationServiceSuffix;
    private String businessServiceSuffix;
    private String integrationServiceSuffix;

    private String fileNamePresentationImpl;

    private String fileNameBusinessInterface;

    private String fileNameBusinessImpl;

    private String fileNameIntegrationInterface;

    private String srcOutputDir;

    private String packageBase;

    /**
     * Stores all methods in the Presentation interface.
     */
    private Method[] methods;

    /**
     * Contains all Application objects used in interface.
     * It is used to generate business/integration models as well as converters.
     */
    private Map<String,Class> classBusinessModel = new HashMap<String,Class>();

    /**
     * Contains all Application and Appverse objects used in interface.
     * Is is used to know which converters should be injected in Presentation and Business Impl services.
     */
    private Map<String,Class> classObjectsUsed = new HashMap<String, Class>();


    public ServiceCreatorContext(String fullQualifiedPresentationInterfaceName,
                                 String srcOutputDir,
                                 String presentationServiceSuffix,
                                 String businessServiceSuffix,
                                 String integrationServiceSuffix) {
        this.fullQualifiedPresentationInterfaceName = fullQualifiedPresentationInterfaceName;
        this.srcOutputDir =srcOutputDir;
        this.presentationServiceSuffix = presentationServiceSuffix;
        this.businessServiceSuffix = businessServiceSuffix;
        this.integrationServiceSuffix = integrationServiceSuffix;
        build();
    }

    public void setPresentationInterfaceMethods(Method[] methods) {
        this.methods = methods;
    }

    public Method[] getMethods() {
        return this.methods;
    }

    public String getPackageBase() {
        return packageBase;
    }


    private void build() {
        /**
         * from fullQualifiedPresentationInterfaceName to:
         * from org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserServiceFacade to:
         * - org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserService
         *
         * packages to obtain:
         * from org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserServiceFacade
         * to:
         * - org.appverse.web.showcases.gwtshowcase.backend.services.presentation.impl.live
         * - org.appverse.web.showcases.gwtshowcase.backend.services.business
         * - org.appverse.web.showcases.gwtshowcase.backend.services.business.impl.live
         * - org.appverse.web.showcases.gwtshowcase.backend.services.integration
         * - org.appverse.web.showcases.gwtshowcase.backend.services.integration.impl.live
         *
         * Classes:
         * from UserServiceFacade to:
         * - UserServiceFacadeImpl
         * - UserService
         * - UserServiceImpl
         * - UserRepository
         * - UserRepositoryImpl.
         */
        String baseName = fullQualifiedPresentationInterfaceName.substring(
                fullQualifiedPresentationInterfaceName.lastIndexOf(".") + 1,
                fullQualifiedPresentationInterfaceName.indexOf(presentationServiceSuffix)
        );
        packageBase = getFullQualifiedPresentationInterfaceName().substring(0,
                getFullQualifiedPresentationInterfaceName().indexOf("backend.services")-1);
        presentationInterfaceClassName  = baseName + presentationServiceSuffix;
        presentationImplClassName       = baseName + presentationServiceSuffix + "Impl";
        businessInterfaceClassName      = baseName + businessServiceSuffix;
        businessImplClassName           = businessInterfaceClassName + "Impl";

        fullQualifiedPresentationImplName       = packageBase + ".backend.services.presentation.impl.live." + getPresentationImplClassName();
        businessImplFullQfdClassName            = packageBase + ".backend.services.business.impl.live."     + getBusinessImplClassName();
        businessInterfaceFullQfdClassName       = packageBase + ".backend.services.business."               + getBusinessInterfaceClassName();
        integrationImplFullQfdClassBaseName     = packageBase + ".backend.services.integration.impl.live";//  + getIntegrationImplClassName();
        integrationInterfaceFullQfdBaseName     = packageBase + ".backend.services.integration";//            + getIntegrationInterfaceClassName();
    }

    public String firstLetterToLowercase(String sName) {
        String s = sName.substring(0,1).toLowerCase();
        s = s + sName.substring(1);
        return s;
    }

    /**
     * Fully Qualified clsas name of the Presentation service interface (passed as parameter to the plugin.
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserServiceFacade
     */
    public String getFullQualifiedPresentationInterfaceName() {
        return fullQualifiedPresentationInterfaceName;
    }

    /**
     * Class name of the interface.
     * i.e. UserServiceFacade
     */
    public String getPresentationInterfaceClassName() {
        return presentationInterfaceClassName;
    }

    /**
     * Fully Qualified class name of the Presentation service implementation (derived).
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.presentation.impl.live.UserServiceFacadeImpl
     */
    public String getFullQualifiedPresentationImplName() {
        return fullQualifiedPresentationImplName;
    }

    /**
     * Class name of the presentation service implementation.
     * i.e. UserServiceFacadeImpl
     */
    public String getPresentationImplClassName() {
        return presentationImplClassName;
    }

    /**
     * Fully Qualified class name of the Business service interface (derived)
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.business.UserService
     */
    public String getBusinessInterfaceFullQfdClassName() {
        return businessInterfaceFullQfdClassName;
    }

    /**
     * Class name of the Business service interface (derived)
     * i.e. UserService
     */
    public String getBusinessInterfaceClassName() {
        return businessInterfaceClassName;
    }

    /**
     * Fully Qualified class name of the Business service implementation (derived)
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.business.impl.live.UserServiceImpl
     */
    public String getBusinessImplFullQfdClassName() {
        return businessImplFullQfdClassName;
    }

    /**
     * Class name of the Business service interface (derived)
     * i.e. UserServiceImpl
     */
    public String getBusinessImplClassName() {
        return businessImplClassName;
    }

    /**
     * Fully Qualified class name of the Integration service interface (derived)
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.integration.UserServiceRepository
     */
    public String getIntegrationInterfaceFullQfdClassName(Class clModel) {
        return integrationInterfaceFullQfdBaseName+"."+getBusinessModelName(clModel)+integrationServiceSuffix;
    }

    /**
     * Class name of the Integration service interface (derived)
     * i.e. UserRepository
     */
    public String getIntegrationInterfaceClassName(Class clModel) {
        return getBusinessModelName(clModel)+integrationServiceSuffix;
    }

    /**
     * Fully Qualified class name of the Integration service implementation (derived)
     * i.e. org.appverse.web.showcases.gwtshowcase.backend.services.integration.impl.live.UserServiceRepository
     */
    public String getIntegrationImplFullQfdClassName(Class clModel) {
        return integrationImplFullQfdClassBaseName+"."+getBusinessModelName(clModel)+integrationServiceSuffix+"Impl";
    }

    /**
     * Class name of the Integration service implementation (derived)
     * i.e. UserServiceRepositoryImpl
     */
    public String getIntegrationImplClassName(Class clModel) {
        return getBusinessModelName(clModel)+integrationServiceSuffix+"Impl";
    }

    /**
      UTIL GETTERS to retrieve FileNames
     */

    public String getFileNamePresentationImpl() {
        fileNamePresentationImpl = fullQualifiedPresentationImplName.replace(".","/");
        return getSrcOutputDir() +"/"+fileNamePresentationImpl+".java";
    }

    public String getFileNameBusinessInterface() {
        fileNameBusinessInterface = businessInterfaceFullQfdClassName.replace(".","/");
        return getSrcOutputDir() +"/"+fileNameBusinessInterface+".java";
    }

    public String getFileNameBusinessImpl() {
        fileNameBusinessImpl = businessImplFullQfdClassName.replace(".","/");
        return getSrcOutputDir() +"/"+fileNameBusinessImpl+".java";
    }

    public String getFileNameIntegrationInterface(Class clModel) {
        fileNameIntegrationInterface = getIntegrationInterfaceFullQfdClassName(clModel).replace(".","/");
        return getSrcOutputDir() +"/"+fileNameIntegrationInterface+".java";
    }

    public String getFileNameIntegrationImpl(Class clModel) {
        fileNameIntegrationInterface = getIntegrationImplFullQfdClassName(clModel).replace(".","/");
        return getSrcOutputDir() +"/"+fileNameIntegrationInterface+".java";
    }


    public String getFileNameP2BConverter(String converterClassName) {
        String s = getP2BConverterPackage().replace(".","/");
        return getSrcOutputDir() +"/"+s+"/"+converterClassName+".java";
    }

    public String getFileNameB2IConverter(String converterClassName) {
        String s = getB2IConverterPackage().replace(".","/");
        return getSrcOutputDir() +"/"+s+"/"+converterClassName+".java";
    }

    public String getPresentationImplPackage() {
        return getFullQualifiedPresentationImplName().substring(0,
                getFullQualifiedPresentationImplName().lastIndexOf("."));
    }

    public String getBusinessInterfacePackage() {
        return getBusinessInterfaceFullQfdClassName().substring(0,
                getBusinessInterfaceFullQfdClassName().lastIndexOf("."));
    }

    public String getBusinessImplPackage() {
        return getBusinessImplFullQfdClassName().substring(0,
                getBusinessImplFullQfdClassName().lastIndexOf("."));
    }

    public String getIntegrationInterfacePackage() {
        return integrationInterfaceFullQfdBaseName;
    }

    public String getIntegrationImplPackage() {
        return integrationImplFullQfdClassBaseName;
    }

    public String getBusinessModelPackage() {
        return packageBase+".backend.model.business";
    }

    public String getIntegrationModelPackage() {
        return packageBase+".backend.model.integration";
    }

    public String getP2BConverterPackage() {
        return packageBase+".backend.converters.p2b";
    }

    public String getB2IConverterPackage() {
        return packageBase+".backend.converters.b2i";
    }


    public String getSrcOutputDir() {
        return srcOutputDir;
    }

    public void addBusinessModelBean(Class clReturnType, boolean returnType) {
        if( ServiceCreatorHelper.isApplicationModel(clReturnType.getName())) {
            classBusinessModel.put(clReturnType.getName(), clReturnType);
        }
        //add all Application and Appverse objects
        if( !returnType && ServiceCreatorHelper.isChildAppverseModel(clReturnType)) {
            classObjectsUsed.put(clReturnType.getName(), clReturnType);
        }
    }

    public Map<String, Class> getBusinessModelBeans() {
        return classBusinessModel;
    }

    public Map<String, Class> getClassObjectsUsed() {
        return classObjectsUsed;
    }

    //Following methods may need a refactoring when completing integration and business helpers methods, in order to make them more fancy
    public String getFileNameBusinessModel(Class clModel) {
        String fileNameBusinessModel = getBusinessModelPackage().replace(".","/");
        return getSrcOutputDir() +"/"+fileNameBusinessModel+"/"+getBusinessModelName(clModel)+".java";
    }

    /**
     * Obtain the Business Bean model name based on the Presentation model CLASS.
     * It is also used as part of the B2I and P2B converters, since they are the same.
     * @param clPresentationModel
     * @return
     */
    public String getBusinessModelName(Class clPresentationModel) {
        String sBeanName = clPresentationModel.getSimpleName(); //UserVO
        sBeanName = sBeanName.replace("VO","");
        return sBeanName;
    }

    public String getBeanNameForConverter(Class clPresentationModel) {
        String sBeanName = clPresentationModel.getSimpleName(); //UserVO
        sBeanName = sBeanName.replace("VO","");
        return firstLetterToLowercase(sBeanName);
    }

    public String getFileNameIntegrationModel(Class clModel) {
        String fileNameIntegrationModel = getIntegrationModelPackage().replace(".","/");
        return getSrcOutputDir() +"/"+fileNameIntegrationModel+"/"+getIntegrationModelName(clModel)+".java";
    }

    public String getIntegrationEntityName(Class clModel) {
        String sBeanName = clModel.getSimpleName(); //UserVO
        sBeanName = sBeanName.replace("VO","");
        return sBeanName.toUpperCase();
    }

    public String getIntegrationModelName(Class clModel) {
        String sBeanName = clModel.getSimpleName(); //UserVO
        sBeanName = sBeanName.replace("VO","DTO");
        return sBeanName;
    }

    public String fromModelPresentationToBusiness(String className) {
        String s = ServiceCreatorHelper.fromModelPresentationToBusiness(className);
        if( s != null && s.length()==0) {
            //it is a VO!, else is a Framework bean (paginated...)
            //className is fully qualified, and should be a *VO
            String singleClassName = className.substring(className.lastIndexOf(".")+1);
            String businessClassName = singleClassName.substring(0, singleClassName.lastIndexOf("VO"));

            s = getBusinessModelPackage()+"."+businessClassName;
        }
        return s;
    }

}
