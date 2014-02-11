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


import org.appverse.web.framework.backend.api.model.business.AbstractBusinessBean;
import org.appverse.web.framework.backend.api.model.integration.AbstractIntegrationBean;
import org.appverse.web.framework.backend.api.model.presentation.AbstractPresentationBean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceCreatorHelper {
    private static final String DEFAULT_JPA = "@Column(nullable = false)";
    static Map<String, String> mappersP2B = new HashMap<String, String>(10);
    static Map<String, String> mappersI2JPA = new HashMap<String, String>(10);
    static Map<String, String> mappersP2BConverters = new HashMap<String, String>(10);
    static Map<String, String> mappersB2IConverters = new HashMap<String, String>(10);

    static{
        //Default mappings for Presentation to Business for appverse framework classes
        mappersP2B.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationDataFilter",
                "org.appverse.web.framework.backend.api.model.business.BusinessDataFilter");
        mappersP2B.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.model.business.BusinessPaginatedDataFilter");
        mappersP2B.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationDataFilter",
                "org.appverse.web.framework.backend.api.model.business.BusinessDataFilter");
        mappersP2B.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.model.business.BusinessPaginatedDataFilter");
        //that's a special case...
        mappersP2B.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationPaginatedResult",
                "java.util.List");

        mappersI2JPA.put("java.lang.String", "@Column(nullable = false, unique = true, length = 40)");
        mappersI2JPA.put("java.lang.Integer", DEFAULT_JPA);
        mappersI2JPA.put("boolean", DEFAULT_JPA);
        mappersI2JPA.put("long", DEFAULT_JPA);

        mappersP2BConverters.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationDataFilter",
                "org.appverse.web.framework.backend.api.converters.p2b.DataFilterP2BBeanConverter");
        mappersP2BConverters.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.converters.p2b.PaginatedDataFilterP2BBeanConverter");
        mappersP2BConverters.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationDataFilter",
                "org.appverse.web.framework.backend.frontfacade.gxt.converters.p2b.GWTDataFilterP2BBeanConverter");
        mappersP2BConverters.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.frontfacade.gxt.converters.p2b.GWTPaginatedDataFilterP2BBeanConverter");

        mappersB2IConverters.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationDataFilter",
                "org.appverse.web.framework.backend.api.converters.b2i.DataFilterB2IBeanConverter");
        /*mappersB2IConverters.put(
                "org.appverse.web.framework.backend.api.model.presentation.PresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.converters.b2i.PaginatedDataFilterB2IBeanConverter");*/
        mappersB2IConverters.put(
                "org.appverse.web.framework.backend.api.model.business.BusinessPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.converters.b2i.PaginatedDataFilterB2IBeanConverter");
        mappersB2IConverters.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationDataFilter",
                "org.appverse.web.framework.backend.api.converters.b2i.DataFilterB2IBeanConverter");
        mappersB2IConverters.put(
                "org.appverse.web.framework.backend.frontfacade.gxt.model.presentation.GWTPresentationPaginatedDataFilter",
                "org.appverse.web.framework.backend.api.converters.b2i.PaginatedDataFilterB2IBeanConverter");
    }

    public static String getDefaultJPAAnnotation(String stClass) {
        if( mappersI2JPA.containsKey(stClass)) {
            return mappersI2JPA.get(stClass);
        }
        return DEFAULT_JPA;
    }

    public static final String fromModelPresentationToBusiness(String className) {
        if( mappersP2B.containsKey(className)) {
            return mappersP2B.get(className);
        }
        return "";
    }

    public static final String getConverterP2BForBean(String beanClassName) {
        if( mappersP2BConverters.containsKey(beanClassName)) {
            return mappersP2BConverters.get(beanClassName);
        }
        return null;
    }

    public static final String getConverterB2IForBean(String beanClassName) {
        if( mappersB2IConverters.containsKey(beanClassName)) {
            return mappersB2IConverters.get(beanClassName);
        }
        return null;
    }

    /**
     * will return true if the className represents a class from the application, not from the Framework.
     * To check that it simple will check if it is not contained in the mappersP2B map.
     * @param className
     * @return
     */
    public static boolean isApplicationModel(String className) {
        return !mappersP2B.containsKey(className);
    }

    public static boolean isChildAppverseModel(Class clType) {
        boolean result = false;
        Class parentClass = clType;
        do {
            if( parentClass != null && (parentClass.isAssignableFrom(AbstractPresentationBean.class) ||
                    parentClass.isAssignableFrom(AbstractBusinessBean.class) ||
                    parentClass.isAssignableFrom(AbstractIntegrationBean.class) )) {
                result = true;
            } else {
                parentClass = parentClass.getSuperclass();
            }
        }while(!result && parentClass!= null &&
                (!parentClass.isAssignableFrom(Object.class)));
        return result;
    }
}
