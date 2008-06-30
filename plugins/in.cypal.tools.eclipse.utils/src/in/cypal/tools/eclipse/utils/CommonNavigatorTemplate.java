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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.BooleanOption;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Prakash G.R.
 * 
 */
public class CommonNavigatorTemplate extends OptionTemplateSection {
	
	private BooleanOption addToPerspective;
	private IPluginBase plugin;
	private IPluginModelFactory factory;
	private String viewId;


	public CommonNavigatorTemplate() {
		setPageCount(1);
		createOptions();
	}

	private void createOptions() {
		
		addOption("viewId", "View Id:", "com.example.test", 0); //$NON-NLS-1$
		addOption("viewName", "View Name:", "My Common Navigator", 0); //$NON-NLS-1$
		addToPerspective = (BooleanOption) addOption("addToPerspective", "Add to Resource Perspective", true, 0); //$NON-NLS-1$
	}

	@Override
	protected URL getInstallURL() {
		return Activator.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
	}

	@Override
	public String getSectionId() {
		return "commonNavigator"; //$NON-NLS-1$
	}

	@Override
	protected ResourceBundle getPluginResourceBundle() {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		return Platform.getResourceBundle(bundle);
	}

	@Override
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		
		plugin = model.getPluginBase();
		factory = model.getPluginFactory();
		viewId = getStringOption("viewId"); //$NON-NLS-1$

		createView();

		createViewer();
		
		if (addToPerspective.isSelected()) {
			createAddToPerspective();
		}
	}

	private void createAddToPerspective() throws CoreException {
		
		IPluginExtension perspectiveExtension = createExtension("org.eclipse.ui.perspectiveExtensions", true); //$NON-NLS-1$

		IPluginElement perspectiveElement = factory.createElement(perspectiveExtension);
		perspectiveElement.setName("perspectiveExtension"); //$NON-NLS-1$
		perspectiveElement.setAttribute("targetID", "org.eclipse.ui.resourcePerspective"); //$NON-NLS-1$ //$NON-NLS-2$

		IPluginElement view = factory.createElement(perspectiveElement);
		view.setName("view"); //$NON-NLS-1$
		view.setAttribute("id", viewId); //$NON-NLS-1$
		view.setAttribute("relative", "org.eclipse.ui.navigator.ProjectExplorer"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("relationship", "stack"); //$NON-NLS-1$ //$NON-NLS-2$
		view.setAttribute("ratio", "0.5"); //$NON-NLS-1$ //$NON-NLS-2$
		perspectiveElement.add(view);

		perspectiveExtension.add(perspectiveElement);
		if (!perspectiveExtension.isInTheModel())
			plugin.add(perspectiveExtension);
	}

	private void createViewer() throws CoreException {

		IPluginExtension viewerExtension = createExtension("org.eclipse.ui.navigator.viewer", true); //$NON-NLS-1$
		if (!viewerExtension.isInTheModel())
			plugin.add(viewerExtension);
		
		
		createActionBinding(viewerExtension);
		
		createContentBinding(viewerExtension);
		
	}

	private void createContentBinding(IPluginExtension viewerExtension) throws CoreException {

		IPluginElement viewerContentBindingElement = factory.createElement(viewerExtension);
		viewerContentBindingElement.setName("viewerContentBinding"); //$NON-NLS-1$
		viewerContentBindingElement.setAttribute("viewerId", viewId); //$NON-NLS-1$

		IPluginElement includesElement = factory.createElement(viewerContentBindingElement);
		includesElement.setName("includes"); //$NON-NLS-1$
		createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resourceContent"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.filters.*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.linkHelper"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.workingSets"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		viewerContentBindingElement.add(includesElement);
		viewerExtension.add(viewerContentBindingElement);

	}

	private void createActionBinding(IPluginExtension viewerExtension) throws CoreException {
		
		IPluginElement viewerActionBindingElement = factory.createElement(viewerExtension);
		viewerActionBindingElement.setName("viewerActionBinding"); //$NON-NLS-1$
		viewerActionBindingElement.setAttribute("viewerId", viewId); //$NON-NLS-1$
		
		IPluginElement includesElement = factory.createElement(viewerActionBindingElement);
		includesElement.setName("includes"); //$NON-NLS-1$
		
		createChild(includesElement, "actionExtension", "pattern", "org.eclipse.ui.navigator.resources.*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		viewerActionBindingElement.add(includesElement);
		viewerExtension.add(viewerActionBindingElement);
	}

	private void createChild(IPluginElement parent, String name, String attrName, String attrValue) throws CoreException {
		
		IPluginElement child = factory.createElement(parent);
		child.setName(name);
		child.setAttribute(attrName, attrValue);
		parent.add(child);
	}

	private void createView() throws CoreException {
		
		IPluginExtension viewExtension = createExtension("org.eclipse.ui.views", true); //$NON-NLS-1$
		IPluginElement viewElement = factory.createElement(viewExtension);
		viewElement.setName("view"); //$NON-NLS-1$
		viewElement.setAttribute("id", viewId); //$NON-NLS-1$
		viewElement.setAttribute("name", getStringOption("viewName")); //$NON-NLS-1$ //$NON-NLS-2$
		//		viewElement.setAttribute("icon", "icons/sample.gif"); //$NON-NLS-1$ //$NON-NLS-2$

		viewElement.setAttribute("class", "org.eclipse.ui.navigator.CommonNavigator"); //$NON-NLS-1$  //$NON-NLS-2$
		viewExtension.add(viewElement);
		if (!viewExtension.isInTheModel())
			plugin.add(viewExtension);
	}

	public String[] getNewFiles() {
		return new String[0];
	}

	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.navigator.CommonNavigator"; //$NON-NLS-1$
	}
	
	
	@Override
	public void addPages(Wizard wizard) {
		WizardPage page0 = createPage(0);
		page0.setTitle("Common Navigator Settings");
		page0.setDescription("Choose the options for the Common Navigator");
		wizard.addPage(page0);

		markPagesAdded();
	}
	
	@Override
	public IPluginReference[] getDependencies(String schemaVersion) {
		ArrayList<IPluginReference> result = new ArrayList<IPluginReference>();
		result.add(new PluginReference("org.eclipse.core.resources")); //$NON-NLS-1$
		result.add(new PluginReference("org.eclipse.ui.navigator")); //$NON-NLS-1$
		result.add(new PluginReference("org.eclipse.ui.navigator.resources")); //$NON-NLS-1$
		return (IPluginReference[]) result.toArray(new IPluginReference[result.size()]);
	}

}
