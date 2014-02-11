/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
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
package org.appverse.web.tools.codegenerator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.appverse.web.tools.codegenerator.util.*;


import java.lang.reflect.Method;
import java.util.List;

@Mojo(name="generate-service")
public class ServiceCodeMojo extends AbstractMojo {

	@Parameter(property="presentationServiceInterface",required =true)
	private String presentationServiceName;
    @Parameter(property="srcOutputDir",defaultValue="target/appverse_generatedsources", required = true)
    private String srcOutputDir;


    @Parameter(property="genPresentationService",defaultValue="true", required = true)
    private Boolean genPresentationService;

    @Parameter(property="genBusinessService",defaultValue="false")
    private Boolean genBusinessService;

    @Parameter(property="genBusinessModel",defaultValue="false")
    private Boolean genBusinessModel;

    @Parameter(property="genIntegrationService",defaultValue="false")
    private Boolean genIntegrationService;

    @Parameter(property="genIntegrationModel",defaultValue="false")
    private Boolean genIntegrationModel;

    @Parameter(property="genP2BConfig",defaultValue="false")
    private Boolean genP2BConfig;

    @Parameter(property="genB2IConfig",defaultValue="false")
    private Boolean genB2IConfig;

    @Parameter(property="presentationServiceSuffix",defaultValue="ServiceFacade")
    public static String presentationSuffix;

    @Parameter(property="businessServiceSuffix",defaultValue="Service")
    public static String businessSuffix;

    @Parameter(property="integrationServiceSuffix",defaultValue="Repository")
    public static String integrationSuffix;

	@Parameter(readonly=true,property="project")
	private MavenProject project; 
	
	@Override
	public void execute() throws MojoExecutionException {
		Log logger = getLog();
		try {
            logger.info("generate-service for ["+presentationServiceName+"]");
            logger.info("adding to classpath :"+project.getBuild().getOutputDirectory());
            ServiceCreatorContext serviceContext = new ServiceCreatorContext(
                    presentationServiceName, srcOutputDir, presentationSuffix, businessSuffix, integrationSuffix
            );
            ServiceClassLoader classLoader = ServiceClassLoader.create(this.getClass().getClassLoader(), project, logger);
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);
                Class cl = classLoader.loadClass(presentationServiceName);
                Method[] methodList = cl.getMethods();
                serviceContext.setPresentationInterfaceMethods(methodList);
                AbstractServiceFactory factory = AbstractServiceFactory.getFactory(
                        genPresentationService,
                        genBusinessService, genIntegrationService,
                        genBusinessModel, genIntegrationModel,
                        genP2BConfig, genB2IConfig, serviceContext);

                List<AbstractServiceCreator> creatorList = factory.getServiceCreators();
                for( AbstractServiceCreator creator: creatorList) {
                    creator.build();
                    creator.saveFile();
                }
                System.out.println("Service Context mod");
                System.out.println(serviceContext);

                //Integration are obtained from different sources...

                List<AbstractServiceCreator> integrationList = factory.getIntegrationCreatorsBasedOnModels(serviceContext);
                for( AbstractServiceCreator creator: integrationList) {
                    creator.build();
                    creator.saveFile();
                }

                //serviceContext has a Map with all Presentation beans that needs to be created
                //Input: Class [org.appverse.web.showcases.gwtshowcase.backend.model.presentation.UserVO]
                //Output: class User
                List<AbstractModelCreator> creatorModelList = factory.getModelCreators(
                        serviceContext
                );
                for( AbstractModelCreator creator: creatorModelList) {
                    creator.build();
                    creator.saveFile();
                }

            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }

            //classpath!

		} catch (Throwable e) {
            e.printStackTrace();
			throw new MojoExecutionException("Error while generating code", e);
		}
	}

}