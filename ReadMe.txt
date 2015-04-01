Installation Guide for the MBSE plugin for MagicDraw
===================================================

The MBSE plugin for MagicDraw requires MagicDraw 17.0 SP4 or higher with an installed SysML plugin 17.0 SP5 or higher.

The MBSE plugin for MagicDraw provides several functions:
1. Support for the recommendations of the INCOSE SE2 Challenge team (http://mbse.gfse.de) described in the "Cookbook for MBSE with SysML".
   It consists of several profiles (SE2Profile, SE2Marte, SE2QFTP), a model library (SE2Definitions), and a DSL Customization.
   The APE model follows those recommendations and can be used as an example. It can be found in the samples directory.
   The proposed model organization can be generated automatically with SE2:GetTemplate which creates from a Block all packages, BDDs, IBds, 
   and hyperlinks for all aspects and views. Invoke GetTemplate on a SysML Block, having the SE2Profile loaded in your project.
2. Model Based Document Generation The plugin is used to transform a document, which is  "stored" in a SysML/UML model into an 
   XML file conforming  with DocBook, for further usage. Most typically the document will be converted to PDF.
   It provides a wysiwyg preview editor which allows to preview diagrams, and re-arrange the order of chapters and sections.
   It allows users to extract a printable formatted document from a properly edited model. The parts of the model from which 
   the document is extracted shall in general contain structure, prose and references to various types of system elements 
   also present in the same artifact.
   The User Manual itself is fully generated. The corresponding model can be found in samples.
   In order to use it the "Customization For Docbook" and the “DocBookProfile.mdzip” must be used by your project.
3. Variant Management. It allows to extract models of system variant which are modeled according to the "Cookbook for MBSE with SysML" recommendations.
   In order to use it the the “Se2Profile.mdzip” must be used by your project.
   The APE model follows those recommendations and can be used as an example. It can be found in the samples directory.
4. Systems Reasoner. It allows to calculate total cost and power of elements in a product tree. This part is complemented
   with a more advanced version which makes use of MagicDraw's Cameo Simulation Toolkit (CST) using parametric constraints to evaluate any kind of
   constraint on the system model. The user is supported by a number of convenience functions which allow to instrument the model and carry out
   any kind of evaluation.

To install the MBSE plugin for MagicDraw, do the following:

1. Start MagicDraw as an administrator 
   (IMPORTANT NOTE for Windows Vista / Windows 7: to start MagicDraw, go to the 
   "bin" subfolder of the MagicDraw installation root, and start mduml.exe via 
   the context menu item “Run an administrator”).

2. Invoke the Resource / Plugin Manager via the menu 
   “Help” => “Resource / Plugin Manager”.

3. In the Resource / Plugin Manager, click “Import” and select the downloaded 
   ZIP file "MBSEPugin.zip".

4. After successful installation, the user guide will be available: 
   Menu “Help” => “Other Documentation” => 
   * "Model Based Document Generation for MagicDraw"
   * "Variant Management User Manual"

5. Make sure that projects using the plugin include the appropriate profiles and customization modules,
   which is now available on MagicDraw’s standard paths.

Building from source code:

1. Load the project in Eclipse
2. Set the MAGICDRAW_HOME variable to your MD installation directory
3. Run buildPlugin as ant to create MBSEPlugin.jar
4. Run buildDistribution as ant to create the importable plugin

Writing a document
1. The document should be a MD project or module
2. Create per document a single module
3. Use the modules (describing you system) you need for the documentation
4. Do NOT make a document part of your system module
5. The document module shall depend on the system module but not the other way round.
 
Updating the profiles and examples
1. All model libraries, profiles, and manuals are on the TWS of the SE2 team
2. The TWS is the single source
3. No modifications shall be done directly in the distributed profiles, modules, models
4. For releases, the TWS models are stored as local
---

/*
 *
 *    (c)  INCOSE SE2 Challenge Team for Telescope Modeling 2011
 *    Copyright by ESO, HOOD, TUM, oose, GfSE
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
 *
*/
