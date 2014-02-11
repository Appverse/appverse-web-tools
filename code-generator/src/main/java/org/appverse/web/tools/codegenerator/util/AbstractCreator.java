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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class for Service and Model creators.
 * Holds the basics of code generation and provides methods used in both creator types.
 */
public abstract class AbstractCreator {

    ServiceCreatorContext serviceCreatorContext;
    Map<String,String> mImports = new HashMap<String,String>();
    private String fileContent;

    /**
     * Implement this method creating a VelocityContext with all properties your template is using.
     * @return
     */
    abstract VelocityContext getVelocityContext();

    /**
     * Velocity Template name
     * @return
     */
    abstract String getTemplateName();

    /**
     * The output File where the content will be flushed.
     * @return
     */
    public abstract File getOutputFile();

    public AbstractCreator(ServiceCreatorContext serviceCreatorContext) {
        this.serviceCreatorContext = serviceCreatorContext;
    }

    /**
     * Based on mImports filled up during the code generation process this method builds the import statements.
     * @return A string representing all imports statements.
     */
    public String getAdditionalImports() {
        //Look in mImports
        Iterator<String> itKeys = mImports.keySet().iterator();
        StringBuilder sbf = new StringBuilder("");
        while( itKeys.hasNext()) {
            String s = itKeys.next();
            sbf.append("import ").append(s).append(";").append("\n");
        }
        return sbf.toString();
    }


    /**
     * Effectively builds the files based on Velocity engine.
     */
    public void build() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();

        Template t = ve.getTemplate(getTemplateName());
        StringWriter writer = new StringWriter();
        t.merge(getVelocityContext(),writer);
        fileContent = writer.toString();
    }

    /**
     * Saves the file to disk.
     * @return boolean indicating if the save is succesfully
     * @throws Exception
     */
    public boolean saveFile() throws Exception {
        File f = getOutputFile();
        PrintWriter pw = new PrintWriter(f);
        pw.print(fileContent);
        pw.flush();
        pw.close();
        return true;
    }


    protected void processImports(Field field) {
        processImports(field.getType());
    }

    protected void processImports(Class cl) {
        if( !cl.isPrimitive() && !cl.getName().startsWith("java.lang")) {
            mImports.put(cl.getName(),"");
        }
    }

}
