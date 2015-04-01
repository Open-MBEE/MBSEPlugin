package org.eso.sdd.mbse.doc.options;

import com.nomagic.magicdraw.resources.ResourceManager;

/**
 * Resource handler class.
 * This class is an interface to MagicDraw ResourceManager.
 *
 * @author Mindaugas Genutis
 */
public final class EnvironmentOptionsResources
{
	/**
	 * Resource bundle name.
	 */
	public static final String BUNDLE_NAME = "org.eso.sdd.mbse.doc.options.EnvironmentOptionsResources";

	/**
	 * Constructs this resource handler.
	 */
	private EnvironmentOptionsResources()
	{
		// do nothing.
	}

	/**
	 * Gets resource by key.
	 *
	 * @param key key by which to get the resource.
	 * @return translated resource.
	 */
	public static String getString(String key)
	{
		return ResourceManager.getStringFor(key, BUNDLE_NAME, EnvironmentOptionsResources.class.getClassLoader());
	}
}
