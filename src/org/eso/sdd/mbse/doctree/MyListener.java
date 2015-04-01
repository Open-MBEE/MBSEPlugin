package org.eso.sdd.mbse.doctree;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.impl.PropertyNames;
import com.nomagic.magicdraw.uml.ExtendedPropertyNames;
import com.nomagic.utils.Utilities;
import com.nomagic.magicdraw.core.Project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Property change listener which shows message, if "Is Derived" property is changed into true.
 *
 * @author Michele Zamparelli
 */
public class MyListener implements PropertyChangeListener {
	private Logger  logger = null;
	        public void propertyChange1(PropertyChangeEvent evt)     {
                if (PropertyNames.IS_DERIVED.equals(evt.getPropertyName()) &&
                        Utilities.isEqual(evt.getNewValue(), Boolean.TRUE))         {
                        Application.getInstance().getGUILog().showMessage("Attribute is derived");
                }
        }

    	@Override
    	public void propertyChange(PropertyChangeEvent evt)    	{
    		
    		logger = Logger.getLogger("org.eso.sdd.mbse.doc");
        	// This request is enabled, because WARN >= INFO.
    		logger.setLevel(Level.DEBUG);

    		//Application.getInstance().getGUILog().showMessage(evt.getPropertyName() + " is Changed");
    		if(evt.getSource() instanceof NamedElement) { 
    			NamedElement ne = (NamedElement)(evt.getSource());
    			logger.debug(evt.getPropertyName() + " is Changed, for element " + ne.getHumanName());
    		}
    	}

}


