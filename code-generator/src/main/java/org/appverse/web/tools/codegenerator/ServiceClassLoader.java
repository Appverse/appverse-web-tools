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

package org.appverse.web.tools.codegenerator;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;

public class ServiceClassLoader extends URLClassLoader {

	public static ServiceClassLoader create(ClassLoader pluginClassLoader,
			MavenProject project, Log logger) {
		List<URL> urls = new ArrayList<URL>();
		try {
			File classDir = null;
			classDir = new File(project.getBuild().getOutputDirectory());
            project.getParent().getModules();
            String s = project.getParent().getBasedir().getAbsolutePath();
            logger.info("parent base path ["+s+"]");
            logger.info("collectedProjects :"+project.getParent().getBuild().getOutputDirectory());
            List<String> l = project.getParent().getModules();
            for( String sModule: l) {
                File classDirTmp = new File(project.getParent().getBasedir(),sModule+"\\target\\classes");
                logger.info("path ["+classDirTmp.getAbsolutePath()+"]");
                urls.add(classDirTmp.toURI().toURL());
            }
			urls.add(classDir.toURI().toURL());
			classDir = new File(project.getBuild().getTestOutputDirectory());
			urls.add(classDir.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error while assembling classpath", e);
		}
		URL[] a = urls.toArray(new URL[0]);
		return new ServiceClassLoader(a, pluginClassLoader);
	}

	private ServiceClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
}
