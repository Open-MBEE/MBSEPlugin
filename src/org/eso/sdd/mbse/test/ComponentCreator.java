package org.eso.sdd.mbse.test;

import org.eso.sdd.mbse.doc.algo.Utilities;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * MagicDraw application sample to test.
 */
public class ComponentCreator {

	private static Utilities theUtilities = null;

	public static Object createComponent(String name, String type,
			Project project, Element dad) throws ReadOnlyElementException {

		theUtilities = new Utilities();

		if (type.equals("section")) {
			return createSection(name, project, dad);
		} else if (type.equals("paragraph")) {
			return createParagraph(name, project, dad);
		} else if (type.equals("chapter")) {
			return createChapter(name, project, dad);
		}

		return null;
	}

	private static Comment createParagraph(String text, Project proj,
			Element dad) throws ReadOnlyElementException {

		Comment component = proj.getElementsFactory().createCommentInstance();
		StereotypesHelper.addStereotype(component,
				theUtilities.getTheParagraphStereotype());
		component.setBody(text);

		ModelElementsManager.getInstance().addElement(component, dad);

		if (Utilities.isSection(dad)) {

			StereotypesHelper.setStereotypePropertyValue(dad,
					theUtilities.getTheSectionStereotype(), "blockelements",
					component, true);
		}

		if (Utilities.isChapter(dad)) {

			StereotypesHelper.setStereotypePropertyValue(dad,
					theUtilities.getTheChapterStereotype(), "blockelements",
					component, true);
		}
		
		return component;
	}

	private static Package createSection(String text, Project proj, Element dad) throws ReadOnlyElementException {

		Package component = proj.getElementsFactory().createPackageInstance();
		StereotypesHelper.addStereotype(component,
				theUtilities.getTheSectionStereotype());
		component.setName(text);

		ModelElementsManager.getInstance().addElement(component, dad);
		
		StereotypesHelper.setStereotypePropertyValue(dad ,theUtilities.getTheChapterStereotype(), "sections", component,true);

		return component;
	}

	private static Package createChapter(String text, Project proj, Element dad)
			throws ReadOnlyElementException {

		Package component = proj.getElementsFactory().createPackageInstance();
		StereotypesHelper.addStereotype(component,
				theUtilities.getTheChapterStereotype());
		component.setName(text);

		ModelElementsManager.getInstance().addElement(component, dad);

		StereotypesHelper.setStereotypePropertyValue(dad,
				theUtilities.getTheBookStereotype(), "bookComponent",
				component, true);

		return component;
	}
}
