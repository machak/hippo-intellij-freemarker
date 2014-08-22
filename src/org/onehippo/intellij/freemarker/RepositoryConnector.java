/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onehippo.intellij.freemarker;


import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.onehippo.intellij.freemarker.config.ApplicationComponent;
import org.onehippo.intellij.freemarker.config.ProjectComponent;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;


/**
 * @version "$Id: RepositoryConnector.java 195401 2014-05-02 12:28:09Z mmilicevic $"
 */
public class RepositoryConnector {


    public final static String FTL_FOLDER = "site" + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator + "ftl";
    private Project project;

    public RepositoryConnector(final Project project) {
        this.project = project;

    }


    public Session getSession() throws RepositoryException {
        try {
            String userName = "admin";
            String password = "admin";
            String address = "rmi://localhost:1099/hipporepository";
            final ApplicationComponent component = project.getComponent(ProjectComponent.class);
            if (component != null) {
                if (!Strings.isNullOrEmpty(component.getUsername())) {
                    userName = component.getUsername();
                }
                if (!Strings.isNullOrEmpty(component.getRmiAddress())) {
                    address = component.getRmiAddress();
                }
                if (!Strings.isNullOrEmpty(component.getPassword())) {
                    password = component.getPassword();
                }
            }
            final HippoRepository repository = HippoRepositoryFactory.getHippoRepository(address);
            return repository.login(userName, password.toCharArray());
        } catch (Exception e) {
            FreemarkerEditor.error("Error connecting to hippo repository" + e.getMessage(), project);
            throw new RepositoryException("Error connecting to hippo repository templates");
        }


    }


    public TemplateData createFilePath(final String path, final File folder) {
        String repoPath = path.replaceAll("/hst:hst/hst:configurations/", "").replaceAll("hst:templates/", "");
        repoPath = repoPath.replaceAll("hst:default", "hst_default");
        final Iterable<String> pathParts = Splitter.on('/').split(repoPath);
        final List<String> folderParts = Lists.newArrayList(pathParts);
        // remove last item
        String parent = folder.getAbsolutePath();
        folderParts.remove(folderParts.size() - 1);
        File myFolder = folder;
        for (String folderPart : folderParts) {
            final String myFolderPath = MessageFormat.format("{0}{1}{2}", folder.getAbsolutePath(), File.separator, folderPart);

            myFolder = new File(myFolderPath);
            if (!myFolder.exists()) {
                myFolder.mkdir();
            }
            parent = MessageFormat.format("{0}{1}{2}", parent, File.separator, myFolderPath);
        }

        return new TemplateData(myFolder, folder.getAbsolutePath() + File.separator + Joiner.on(File.separator).join(pathParts));
    }


    public static class TemplateData {
        final File file;
        final String fileName;

        private TemplateData(final File file, final String fileName) {
            this.file = file;
            this.fileName = fileName;
        }

        public File getFile() {
            return file;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
