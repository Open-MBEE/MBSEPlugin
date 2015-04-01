package org.eso.sdd.mbse.doc.options;

import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import org.eso.sdd.mbse.doc.options.EnvironmentOptionsResources;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.ChoiceProperty;
import com.nomagic.magicdraw.properties.FileProperty;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.nomagic.magicdraw.ui.ImageMap16;
import com.nomagic.ui.SwingImageIcon;
import com.nomagic.magicdraw.export.image.ImageExporter;

import java.util.ArrayList;
import java.util.List;


/**
 * Options group for MBSE
 *
 * @author Mindaugas Genutis
 * 
 * @author Martin Hochwallner (some extensions and clean up)
 * 
 * 
 * https://www.magicdraw.com/files/manuals/MagicDraw%20OpenAPI%20UserGuide.pdf
 * page 101: ENVIRONMENT OPTIONS
 * 
 * based on example: ${MagicDraw}\openapi\examples\environmentoptions
 *
 */
public class MBSEOptionsGroup extends AbstractPropertyOptionsGroup
{
	/**
	 * ID of the options group.
	 */
	public static final String ID = "options.mbse";

	/**
	 * ID of property group MBDG.
	 */
	public static final String GROUP_MBDG = "MBDG";

	
	/*
	 * MBSE_DIAGRAM_GRAPHICS_FORMAT_ID
	 * [HM]
	 */
	public static final String MBSE_DIAGRAM_GRAPHICS_FORMAT_ID = "MBSE_DIAGRAM_GRAPHICS_FORMAT_ID";
	
	public enum DiagramGraphicsFormat {
		EMF(ImageExporter.EMF), 
		EPS(ImageExporter.EPS), 
		JPEG(ImageExporter.JPEG), 
		PNG(ImageExporter.PNG), 
		SVG(ImageExporter.SVG), 
		TIFF(ImageExporter.TIFF), 
		WMF(ImageExporter.WMF);
	
		private int valueImageExporter;
		
		DiagramGraphicsFormat(int v) {
			this.valueImageExporter = v;
		}
		
		
		/**
		 * 
		 * @return id for the file format parameter for com.nomagic.magicdraw.export.image.ImageExporter
		 */
		public int getValueImageExporter() {
			return this.valueImageExporter;
		}
		
		
		/**
		 * @return Sting: file extension e.g. ".svg"
		 */
		public String getFileExtension() {
			return "." + this.toString().toLowerCase();
		}
		
		public static List<String> getStrings() {
			List<String> ret = new ArrayList<String>();
			for (DiagramGraphicsFormat v :DiagramGraphicsFormat.values()) {
				ret.add(v.toString());
			}
			return ret;
		}
	}
	
	public static final List<String> MBSE_DIAGRAM_GRAPHICS_FORMAT_ID__CHOICE_VALUES = DiagramGraphicsFormat.getStrings();
	
	
	public void setDiagramGraphicsFormat(String value)
	{
		DiagramGraphicsFormat v = DiagramGraphicsFormat.valueOf(value);	
		setDiagramGraphicsFormat(v);
	}

	public void setDiagramGraphicsFormat(DiagramGraphicsFormat value)
	{
		ChoiceProperty property = new ChoiceProperty(MBSE_DIAGRAM_GRAPHICS_FORMAT_ID, value.toString(), MBSE_DIAGRAM_GRAPHICS_FORMAT_ID__CHOICE_VALUES);
		property.setValuesTranslatable(false);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
		property.setGroup(GROUP_MBDG);
		addProperty(property, true);
	}
	
	
	public String getDiagramGraphicsFormat_String()
	{
		Property p = getProperty(MBSE_DIAGRAM_GRAPHICS_FORMAT_ID);
		return (String) p.getValue();
	}
	
	
	public DiagramGraphicsFormat getDiagramGraphicsFormat()
	{
		return DiagramGraphicsFormat.valueOf(getDiagramGraphicsFormat_String());
	}
	
	// --- end MBSE_DIAGRAM_GRAPHICS_FORMAT_ID
	
	
	/*
	 * PDF_DRAFT_MODE
	 */
	public static final String PDF_DRAFT_MODE_ID = "PDF_DRAFT_MODE_ID";

	/**
	 * Gets my boolean property value.
	 *
	 * @return my boolean property value.
	 */
	public boolean isDraftMode()
    {
        Property p = getProperty(PDF_DRAFT_MODE_ID);
        return (Boolean) p.getValue();
    }

	/**
	 * Sets my boolean property value.
	 *
	 * @param value my boolean property value.
	 */
    public void setDraftMode(boolean value)
    {
        BooleanProperty property = new BooleanProperty(PDF_DRAFT_MODE_ID, value);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(GROUP_MBDG);

        addProperty(property);
    }
	
	
	/*
	 * TransformationProperty / PDF_STYLESHEET_ID
	 *
	 */
	public static final String PDF_STYLESHEET_ID = "PDF_STYLESHEET_ID";

	public void setTransformationProperty(String value)
	{
		FileProperty property = new FileProperty (PDF_STYLESHEET_ID, value);
		property.setSelectionMode (FileProperty.FILES_ONLY);
		property.setFileType("*.xsl");
		property.setDisplayFullPath (false);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
		property.setGroup(GROUP_MBDG);

		addProperty(property, true);
	}

	public String getTransformationPropertyValue()
	{
		Property p = getProperty(PDF_STYLESHEET_ID);
		return (String) p.getValue();
	}

	
	
	/**
	 * Resource name of options.
	 */
	private static final String MBSE_OPTIONS_NAME = "MBSE_OPTIONS_NAME";

	
	/**
	 * Provides resources to the properties.
	 */
	public static final PropertyResourceProvider PROPERTY_RESOURCE_PROVIDER = new PropertyResourceProvider()
	{
		@Override
		public String getString(String key, Property property)
		{
			return EnvironmentOptionsResources.getString(key);
		}
	};

	
	
	/**
	 * Constructs this options group.
	 */
	public MBSEOptionsGroup()
	{
		super(ID);
	}

	

	@Override
	public void setDefaultValues()
	{
		setTransformationProperty("ESOTransform.xsl");
		setDraftMode(true);
		setDiagramGraphicsFormat(DiagramGraphicsFormat.SVG.toString());
		setImageDeleteMode(false);
	}

	/*
	 * IMAGE CLEAN UP 
	 */
	
	
	public static final String MBSE_IMAGE_CLEANUP_ID = "MBSE_IMAGE_CLEANUP_ID";
    public void setImageDeleteMode(boolean value)  {
        BooleanProperty property = new BooleanProperty(MBSE_IMAGE_CLEANUP_ID, value);
		property.setResourceProvider(PROPERTY_RESOURCE_PROVIDER);
        property.setGroup(GROUP_MBDG);

        addProperty(property);
    }
	public boolean getImageDeleteMode()   {
        Property p = getProperty(MBSE_IMAGE_CLEANUP_ID);
        return (Boolean) p.getValue();
    }

    

	@Override
	public String getName()
	{
		return EnvironmentOptionsResources.getString(MBSE_OPTIONS_NAME);
	}

	
	
	@Override
	public SwingImageIcon getIcon()
	{
		return (SwingImageIcon) ImageMap16.SYSTEM_BOUNDARY;
	}
}