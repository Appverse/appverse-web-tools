package org.appverse.web.tools.codegenerator.util;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ServiceCreatorContextTest {

    ServiceCreatorContext scc =
            new ServiceCreatorContext("org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserServiceFacade","",
                    "ServiceFacade", "Service", "Repository");

    @Before
    public void initialize() {

    }

    @Test
    public void testServiceCreatorContext() {
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
        Assert.assertEquals("Error in getPresentationInterfaceClassName",
                scc.getPresentationInterfaceClassName(),"UserServiceFacade");

        Assert.assertEquals("Error in getPresentationImplClassName",
                scc.getPresentationImplClassName(),"UserServiceFacadeImpl");

        Assert.assertEquals("Error in getFullQualifiedPresentationImplName",
                scc.getFullQualifiedPresentationImplName(),"org.appverse.web.showcases.gwtshowcase.backend.services.presentation.impl.live.UserServiceFacadeImpl");

        Assert.assertEquals("Error in getFullQualifiedPresentationInterfaceName",
                scc.getFullQualifiedPresentationInterfaceName(),"org.appverse.web.showcases.gwtshowcase.backend.services.presentation.UserServiceFacade");

//Business
        Assert.assertEquals("Error in getBusinessImplClassName",
                scc.getBusinessImplClassName(),"UserServiceImpl");

        Assert.assertEquals("Error in getBusinessImplFullQfdClassName",
                scc.getBusinessImplFullQfdClassName(),"org.appverse.web.showcases.gwtshowcase.backend.services.business.impl.live.UserServiceImpl");

        Assert.assertEquals("Error in getBusinessInterfaceClassName",
                scc.getBusinessInterfaceClassName(),"UserService");

        Assert.assertEquals("Error in getBusinessInterfaceFullQfdClassName",
                scc.getBusinessInterfaceFullQfdClassName(),"org.appverse.web.showcases.gwtshowcase.backend.services.business.UserService");

//Repository / integration
        /*Class cl = null;
        try {
            cl = Class.forName("org.appverse.web.showcases.gwtshowcase.backend.model.presentation.UserVO");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Assert.assertNotNull(cl);
        Assert.assertEquals("Error in getBusinessImplClassName",
                scc.getIntegrationImplClassName(cl),"UserRepositoryImpl");

        Assert.assertEquals("Error in getBusinessImplFullQfdClassName",
                scc.getIntegrationImplFullQfdClassName(cl),"org.appverse.web.showcases.gwtshowcase.backend.services.integration.impl.live.UserRepositoryImpl");

        Assert.assertEquals("Error in getBusinessInterfaceClassName",
                scc.getIntegrationInterfaceClassName(cl),"UserRepository");

        Assert.assertEquals("Error in getBusinessInterfaceFullQfdClassName",
                scc.getIntegrationInterfaceFullQfdClassName(cl),"org.appverse.web.showcases.gwtshowcase.backend.services.integration.UserRepository");
          */
    }

    @Test
    public void testFiles() {
        File f = new File("c:/");
        Assert.assertEquals(
                scc.getFileNamePresentationImpl(),"/org/appverse/web/showcases/gwtshowcase/backend/services/presentation/impl/live/UserServiceFacadeImpl.java"
        );
    }
}
