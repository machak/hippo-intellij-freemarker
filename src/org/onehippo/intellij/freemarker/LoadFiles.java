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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.onehippo.intellij.freemarker.config.ApplicationComponent;
import org.onehippo.intellij.freemarker.config.ProjectComponent;

import com.google.common.base.Strings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @version "$Id: LoadFiles.java 195401 2014-05-02 12:28:09Z mmilicevic $"
 */
public class LoadFiles extends AnAction {


    @Override
    public void actionPerformed(final AnActionEvent event) {
        final Project project = CommonDataKeys.PROJECT.getData(event.getDataContext());
        if (project != null) {
            RepositoryConnector connector = new RepositoryConnector(project);
            final Set<HippoTemplate> templates = fetchTemplates(project, connector);
            final VirtualFile baseDir = project.getBaseDir();
            final String projectPath = baseDir.getPath();
            String ftlFolder = projectPath + File.separator + RepositoryConnector.FTL_FOLDER;
            final ApplicationComponent component = project.getComponent(ProjectComponent.class);
            if (component != null) {
                final String freemarkerDirectory = component.getFreemarkerDirectory();
                if (!Strings.isNullOrEmpty(freemarkerDirectory)) {
                    ftlFolder = freemarkerDirectory;

                }

            }
            saveTemplates(connector, project, templates, ftlFolder);
        }


    }

    public void saveTemplates(final RepositoryConnector connector, final Project project, final Set<HippoTemplate> templates, final String ftlFolder) {
        try {


            final File file = new File(ftlFolder);
            if (!file.exists()) {
                file.mkdir();
            }

            final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            for (HippoTemplate template : templates) {
                final RepositoryConnector.TemplateData path = connector.createFilePath(template.getPath(), file);
                final String fileName = path.getFileName();
                final File templateFile = new File(fileName);
                if (!templateFile.exists()) {
                    templateFile.createNewFile();
                }
                final Writer writer = new BufferedWriter(new FileWriter(templateFile));
                try {
                    writer.write(template.getScript());
                    writer.flush();
                    writer.close();
                } finally {
                    writer.close();
                }
                final LocalFileSystem instance = LocalFileSystem.getInstance();
                instance.refresh(true);
                final VirtualFile vf = instance.findFileByPath(templateFile.getAbsolutePath());
                if (vf != null) {
                    fileEditorManager.openFile(vf, true, true);
                }


            }
        } catch (IOException e) {
            FreemarkerEditor.error(e.getMessage(), project);
        }

    }

    public Set<HippoTemplate> fetchTemplates(final Project project, final RepositoryConnector connector) {
        final Set<HippoTemplate> templates = new HashSet<HippoTemplate>();
        Session session = null;
        try {


            session = connector.getSession();
            final QueryManager queryManager = session.getWorkspace().getQueryManager();
            final Query query = queryManager.createQuery("//element(*, hst:template)[@hst:script]", Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();

            while (nodes.hasNext()) {
                final Node node = nodes.nextNode();
                final Property property = node.getProperty("hst:script");
                final String script = property.getString();
                templates.add(new HippoTemplate(node.getIdentifier(), node.getPath(), script));
            }
        } catch (RepositoryException e) {
            FreemarkerEditor.error("Error loading templates", project);
        } finally {
            if (session != null) {
                session.logout();
            }
        }


        return templates;

    }

}
