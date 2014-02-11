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

public class RepositoryModelCreator extends AbstractModelCreator {
    static final String TEMPLATE_NAME = "velocity/modelRepository.vm";
    private static final String PROPERTY_MODEL_REPOSITORY_PACKAGE = "MODEL_REPOSITORY_PACKAGE";
    private static final String PROPERTY_MODEL_REPOSITORY_ENTITY_NAME = "MODEL_REPOSITORY_ENTITY_NAME";
    private static final String PROPERTY_MODEL_REPOSITORY_CLASS_NAME="MODEL_REPOSITORY_CLASS_NAME";
    private static final String PROPERTY_MODEL_REPOSITORY_FIELDS = "MODEL_REPOSITORY_FIELDS";
    private static final String PROPERTY_MODEL_REPOSITORY_METHODS = "MODEL_REPOSITORY_METHODS";
    //private static final String PROPERTY_

    public RepositoryModelCreator(ServiceCreatorContext serviceCreatorContext, Class clPresentationModel) {
        super(serviceCreatorContext,clPresentationModel);
    }

    @Override
    VelocityContext getVelocityContext() {
        VelocityContext vc = new VelocityContext();
        vc.put(PROPERTY_MODEL_REPOSITORY_PACKAGE, serviceCreatorContext.getIntegrationModelPackage());
        vc.put(PROPERTY_MODEL_REPOSITORY_ENTITY_NAME, serviceCreatorContext.getIntegrationEntityName(getClModel()));
        vc.put(PROPERTY_MODEL_REPOSITORY_CLASS_NAME, serviceCreatorContext.getIntegrationModelName(getClModel()));
        vc.put(PROPERTY_MODEL_REPOSITORY_FIELDS, getFields(true));
        //Note additional imports should be the last, as all options may need additional imports.
        vc.put(PROPERTY_MODEL_REPOSITORY_METHODS, getMethods(true, true));
        //ADDITIONAL_IMPORTS ?¿
        return vc;
    }

    @Override
    String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    public File getOutputFile() {
        String sDirs = serviceCreatorContext.getFileNameIntegrationModel(getClModel());
        sDirs = sDirs.substring(0, sDirs.lastIndexOf("/"));
        File fDirs = new File(sDirs);
        fDirs.mkdirs();

        File fNewClass = new File(serviceCreatorContext.getFileNameIntegrationModel(getClModel()));
        return fNewClass;
    }

}
