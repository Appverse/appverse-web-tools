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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Factory class to obtain 'creators' based on plugin parameters.
 */
public class AbstractServiceFactory {

    private static AbstractServiceFactory instance;
    private boolean genPresentationImpl=false;
    private boolean genBusiness=false;
    private boolean genIntegration=false;
    private boolean genModelBusiness=false;
    private boolean genModelIntegration=false;
    private boolean genP2BConfig=false;
    private boolean genB2IConfig=false;
    private ServiceCreatorContext serviceContext=null;

    private AbstractServiceFactory(boolean genPresentationImpl,
                                   boolean genBusiness,
                                   boolean genIntegration,
                                   boolean genModelBusiness,
                                   boolean genModelIntegration,
                                   boolean genP2BConfig,
                                   boolean genB2IConfig,
                                   ServiceCreatorContext serviceContext) {
        this.genPresentationImpl=genPresentationImpl;
        this.genBusiness=genBusiness;
        this.genIntegration=genIntegration;
        this.genModelBusiness=genModelBusiness;
        this.genModelIntegration=genModelIntegration;
        this.genP2BConfig=genP2BConfig;
        this.genB2IConfig=genB2IConfig;
        this.serviceContext=serviceContext;
    }

    public static AbstractServiceFactory getFactory(boolean genPresentationImpl,
                                                    boolean genBusiness,
                                                    boolean genIntegration,
                                                    boolean genModelBusiness,
                                                    boolean genModelIntegration,
                                                    boolean genP2BConfig,
                                                    boolean genB2IConfig,
                                                    ServiceCreatorContext serviceContext) {
        if( instance == null ) {
            instance = new AbstractServiceFactory(
                    genPresentationImpl,
                    genBusiness,
                    genIntegration,
                    genModelBusiness,
                    genModelIntegration,
                    genP2BConfig,
                    genB2IConfig,
                    serviceContext);
        }
        return instance;
    }

    public List<AbstractServiceCreator> getServiceCreators() {
        List<AbstractServiceCreator> lCreators = new ArrayList<AbstractServiceCreator>();
        if( genPresentationImpl ) {
            lCreators.add(new PresentationServiceCreator(serviceContext));
        }
        if( genBusiness ) {
            lCreators.add( new BusinessInterfaceServiceCreator(serviceContext));
            lCreators.add( new BusinessImplServiceCreator(serviceContext));
        }
        return lCreators;
    }

    public List<AbstractServiceCreator> getIntegrationCreatorsBasedOnModels(ServiceCreatorContext serviceCreatorContext) {
        List<AbstractServiceCreator> lCreators = new ArrayList<AbstractServiceCreator>();
        if( genIntegration ) {
            Map<String, Class> modelBeans = serviceCreatorContext.getBusinessModelBeans();
            Iterator<String> itNames = modelBeans.keySet().iterator();
            while( itNames.hasNext() ){
                String current = itNames.next();
                //for each VO bean used on the presentation interface (the ones stored in modelBeans), create:
                lCreators.add( new RepositoryInterfaceServiceCreator(serviceCreatorContext, modelBeans.get(current)));
                lCreators.add( new RepositoryImplServiceCreator(serviceCreatorContext, modelBeans.get(current)));
            }
        }
        return lCreators;
    }

    public List<AbstractModelCreator> getModelCreators(ServiceCreatorContext serviceContext) {
        List<AbstractModelCreator> lModels = new ArrayList<AbstractModelCreator>();
        Map<String, Class> modelBeans = serviceContext.getBusinessModelBeans();
        Iterator<String> itNames = modelBeans.keySet().iterator();
        while( itNames.hasNext() ){
            String current = itNames.next();
            //for each VO bean used on the presentation interface (the ones stored in modelBeans), create:
            if( genModelBusiness) {
                lModels.add( new BusinessModelCreator(serviceContext, modelBeans.get(current)));
            }
            if( genModelIntegration ) {
                lModels.add( new RepositoryModelCreator(serviceContext, modelBeans.get(current)));
            }
            if( genP2BConfig ) {
                lModels.add( new P2BConverterCreator(serviceContext, modelBeans.get(current)));
            }
            if( genB2IConfig) {
                lModels.add( new B2IConverterCreator(serviceContext, modelBeans.get(current)));
            }
        }
        return lModels;
    }

}
