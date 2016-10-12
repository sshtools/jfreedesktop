/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.freedesktop.mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.AbstractFreedesktopService;

public class DefaultAliasService extends AbstractFreedesktopService<AliasEntry> implements AliasService {
    
    private Map<FileObject, AliasBase> aliasBases = new TreeMap<FileObject, AliasBase>(new FileObjectComparator());

    @Override
    protected Collection<AliasEntry> scanBase(FileObject base) throws IOException {
    	FileObject f = base.resolveFile("aliases");
        AliasBase aliasBase = new AliasBase();
        aliasBases.put(base, aliasBase);
        InputStream fin = f.getContent().getInputStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.startsWith("#")) {
                    int idx = line.indexOf(' ');
                    if(idx == -1) {
                        throw new IOException(f + " contains invalid data '" + line  +"'.");
                    }
                    String mimeType = line.substring(0, idx);
                    String alias = line.substring(idx + 1);
                    AliasEntry entry = new AliasEntry(mimeType, alias);
                    aliasBase.byType.put(mimeType, entry);
                    aliasBase.byAlias.put(alias, entry);
                }
            }
        } finally {
            fin.close();
        }
        return aliasBase.byType.values();
    }

    public void removeBase(FileObject base) {
        super.removeBase(base);
        aliasBases.remove(base);
    }
    
    class AliasBase {
        Map<String, AliasEntry> byType = new HashMap<String, AliasEntry>();        
        Map<String, AliasEntry> byAlias = new HashMap<String, AliasEntry>();
    }

    public AliasEntry getAliasEntryForMimeType(String mimeType) {
        for(FileObject base : getBasesInReverse()) {
            AliasEntry entry = aliasBases.get(base).byType.get(mimeType);
            if(entry != null) {
                return entry;
            }
        }
        return null;
    }

    public AliasEntry getAliasEntryForAlias(String mimeType) {
        for(FileObject base : getBasesInReverse()) {
            AliasEntry entry = aliasBases.get(base).byAlias.get(mimeType);
            if(entry != null) {
                return entry;
            }
        }
        return null;
    }

}
