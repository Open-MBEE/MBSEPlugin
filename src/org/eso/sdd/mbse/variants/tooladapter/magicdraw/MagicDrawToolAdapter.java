/*
 * 
 *    (c) European Southern Observatory, 2011
 *    Copyright by ESO 
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *    
*/
package org.eso.sdd.mbse.variants.tooladapter.magicdraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

import org.eso.sdd.mbse.variants.gui.IGUILabels;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

/*
 * @author Bertil Muth
 */
public class MagicDrawToolAdapter implements IModelingToolAdapter{

	public MagicDrawToolAdapter(){
	}
	
	@Override
	public Collection<? extends Object> getModelElementsByStereotype(String stereotypeName) {
		
		Project magicDrawProject = (Project)getActiveProject();
	
		// find stereotype
		Stereotype stereotype = StereotypesHelper.getStereotype(magicDrawProject, stereotypeName);

		List<Element> stereotypedElements = StereotypesHelper
				.getExtendedElements(stereotype);

		return stereotypedElements;
	}
	
	@Override
	public Object getActiveProject() {
		ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
		Project project = projectsManager.getActiveProject();
		
		return project;
	}
	
	@Override
	public String getActiveProjectFileName() {
		Project project = (Project)getActiveProject();
		return project != null ? project.getFileName() : null;
	}
	
	@Override
	public void loadProject(String fileName) throws Exception {
		Application application = Application.getInstance();
		ProjectDescriptor projectDescriptor = null;
		
		// First, check if the user is logged in to teamwork server.
		/*String loggedUserName = TeamworkUtils.getLoggedUserName();
		if(loggedUserName != null){
			// Ok, the user is logged in. So try to load the project remotely.
			// load teamwork project
			projectDescriptor = 
					TeamworkUtils.getRemoteProjectDescriptorByQualifiedName(fileName);
		}*/
		
		if(projectDescriptor == null){
			File projectFile = new File(fileName);
			if(!projectFile.exists()) throw new FileNotFoundException(IGUILabels.TITLE + ": Can't find project file " + fileName);	
			String activeProjectFileName = getActiveProjectFileName();
			File activeProjectFile = null;
			if(activeProjectFileName != null){
				activeProjectFile = new File(activeProjectFileName);	
			}
			if(activeProjectFile == null || !(activeProjectFile.equals(projectFile))){
				projectDescriptor = ProjectDescriptorsFactory
						.createProjectDescriptor(projectFile.toURI());
			}
		}

		if(projectDescriptor != null){
			application.getProjectsManager().loadProject(projectDescriptor, true);
		}		
	}

	@Override
	public String getModelElementID(Object modelElement) {
		if(!(modelElement instanceof BaseElement)){
			throw new RuntimeException(IGUILabels.TITLE + ": ERROR: can't get id of model element " + modelElement.toString());
		}
		BaseElement elementWithId = (BaseElement)modelElement;
		return elementWithId.getID();
	}

	@Override
	public String getModelElementName(Object modelElement) {
		if(!(modelElement instanceof NamedElement)){
			throw new RuntimeException(IGUILabels.TITLE + ": ERROR: can't get name of model element " + modelElement.toString());
		}
		NamedElement namedElement = (NamedElement)modelElement;
		return namedElement.getName();
	}

	@Override
	public String getModelElementQName(Object modelElement) {
		if(!(modelElement instanceof NamedElement)){
			throw new RuntimeException(IGUILabels.TITLE + ": ERROR: can't get qualified name of model element " + modelElement.toString());
		}
		NamedElement namedElement = (NamedElement)modelElement;
		return namedElement.getQualifiedName();
	}

	@Override
	public void saveProject(String fileName) throws Exception {
		ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
		File projectFile = new File(fileName);
		ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory
				.createLocalProjectDescriptor((Project)getActiveProject(), projectFile);
		projectsManager.saveProject(projectDescriptor, true);
	}

	@Override
	public void removeModelElement(Object modelElement) throws ReadOnlyElementException{
		SessionManager.getInstance().createSession(IGUILabels.TITLE + ": remove package");
		ModelElementsManager.getInstance().removeElement((Element) modelElement);
		SessionManager.getInstance().closeSession();
	}

	@Override
	public String getProjectFileNameExtension() {
		return "mdzip";
	}

	@Override
	public boolean modelElementHasStereotype(Object modelElement, String stereotypeName){
		if(!(modelElement instanceof Element)){
			throw new RuntimeException(IGUILabels.TITLE + ": ERROR: can't get stereotype of model element " + modelElement.toString());
		}
		Element element = (Element)modelElement;

		boolean hasStereotype = StereotypesHelper.hasStereotype(element, stereotypeName);
		return hasStereotype;
	}
}