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

package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class CreateAuthorAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;
	
	public CreateAuthorAction() {
		super("", "SE2: createAuthor", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
		ut = new Utilities();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = null;
		Object userObject = null;
		NamedElement ne = null;
		Node node = null;

		tree = getTree();
		if (tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			if (userObject instanceof Package) {

			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");
		}
		addAuthor2(ne);
	}

	
	private void addAuthor2(NamedElement ne)	{
	    // get project from element
	    final Project project = Project.getProject(ne);

	    Class theAuthor;
	    SessionManager.getInstance().createSession("MBSE-doc");
	    try  {
	        theAuthor = project.getElementsFactory().createClassInstance();
	        theAuthor.setName("New Author");
			StereotypesHelper.addStereotype(theAuthor, ut.getTheBlockStereotype());
			StereotypesHelper.addStereotype(theAuthor, ut.getTheAuthorStereotype());

	        ModelElementsManager.getInstance().addElement(theAuthor, ne);
	        SessionManager.getInstance().closeSession();
	    }
	    catch (Exception roee)  {
	        // cancel session if creation fails
	        SessionManager.getInstance().cancelSession();
			Utilities.displayWarning("Read only element");
			return;
	    }

	    final Browser browser = project.getBrowser();
	    browser.getContainmentTree().openNode(theAuthor, true, true);
	    Application.getInstance().getMainFrame().getProjectWindowsManager().activateWindow("PROPERTIES");

	    // do delay as properties panel is updated with delay too
	    final TimerTask timerTask = new TimerTask()	    {
	        @Override
	        public void run()  {
	            SwingUtilities.invokeLater(new Runnable()  {
	                @Override
	                public void run()   {
	                    final JTabbedPane tabbedPane = findComponent(browser.getQPanel(), JTabbedPane.class);
	                    if (tabbedPane != null)  {
	                        final int tabCount = tabbedPane.getTabCount();
	                        for (int i = 0; i < tabCount; ++i)   {
	                            if ("MBDG".equals(tabbedPane.getTitleAt(i)))  {
	                                tabbedPane.setSelectedIndex(i);
	                                final Component selectedComponent = tabbedPane.getSelectedComponent();
	                                if (selectedComponent instanceof Container)   {
	                                    final JTable table = findComponent((Container) selectedComponent, JTable.class);
	                                    if (table != null)  {
	                                        table.editCellAt(1, 1);
	                                    }
	                                }
	                                return;
	                            }
	                        }
	                    }
	                }
	            });
	        }
	    };
	    new java.util.Timer().schedule(timerTask, 500);
	}	
	
	private static <C extends JComponent> C findComponent(Container container, java.lang.Class<C> type) 	{
	    final Component[] components = container.getComponents();
	    C c = null;
	    for (int i = components.length - 1; i >= 0 && c == null; --i)    {
	        Component component = components[i];
	        if (type.isInstance(component))  {
	            c = (C) component;
	        }
	        else if (component instanceof Container)   {
	            c = findComponent((Container) component, type);
	        }
	    }
	    return c;
	}

}
