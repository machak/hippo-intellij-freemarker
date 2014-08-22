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

/**
 * @version "$Id: HippoTemplate.java 195401 2014-05-02 12:28:09Z mmilicevic $"
 */
public class HippoTemplate {


    private String id;
    private String path;
    private String script;

    public HippoTemplate(final String id, final String path, final String script) {
        this.id = id;
        this.path = path;
        this.script = script;
    }

    public String getPath() {

        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = script;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HippoTemplate{");
        sb.append("id='").append(id).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", script='").append(script).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
