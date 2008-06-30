/*
 * Copyright 2008 Cypal Solutions (tools@cypal.in)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package in.cypal.tools.eclipse.utils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginReference;

/**
 * 
 * @author Prakash G.R.
 *
 */
public class PluginReference implements IPluginReference {
	private int match = NONE;
	private String version;
	private String id;

	public boolean equals(Object object) {
		if (object instanceof IPluginReference) {
			IPluginReference source = (IPluginReference)object;
			if (id==null) return false;
			if (id.equals(source.getId())==false) return false;
			if (version==null && source.getVersion()==null) return true;
			return version.equals(source.getVersion());
		}
		return false;
	}

	public PluginReference(String id) {
		this(id, null);
	}
	
	public PluginReference(String id, String version) {
		this(id, version, 0);
	}

	public PluginReference(String id, String version, int match) {
		this.id = id;
		this.version = version;
		this.match = match;
	}

	/*
	 * @see IPluginReference#getMatch()
	 */
	public int getMatch() {
		return match;
	}

	/*
	 * @see IPluginReference#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * @see IPluginReference#setMatch(int)
	 */
	public void setMatch(int match) throws CoreException {
		this.match = match;
	}

	/*
	 * @see IPluginReference#setVersion(String)
	 */
	public void setVersion(String version) throws CoreException {
		this.version = version;
	}

	/*
	 * @see IIdentifiable#getId()
	 */
	public String getId() {
		return id;
	}

	/*
	 * @see IIdentifiable#setId(String)
	 */
	public void setId(String id) throws CoreException {
		this.id = id;
	}

}
