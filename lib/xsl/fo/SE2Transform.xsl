<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:d="http://docbook.org/ns/docbook" 
  xmlns:exsl="http://exslt.org/common" version="1.0" exclude-result-prefixes="exsl d">

  <xsl:import href="ESOTransform.xsl"/>
  
<!-- This stylesheet was copied from C:\Program Files\Oxygen XML Editor 12\frameworks\docbook\xsl\fo/titlepage-templates.xsl-->
<!-- changes wrt original are marked with the keyword CHANGE -->

<!--
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
 *    $Id: SE2Transform.xsl 320 2011-12-09 07:16:58Z mzampare $
 *
 * 
-->

  <!--  ******************************-->
  
  
  
  <xsl:template match="d:book/d:info/d:author[@role]|d:book/d:info/d:author[@role]" mode="titlepage.mode">
    <fo:block  xsl:use-attribute-sets="book.titlepage.recto.style">
     <xsl:choose>
       <xsl:when test="@role = 'wpManager'">
          <fo:table table-layout="fixed">
            <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="3" column-width="proportional-column-width(2)"/>
            <fo:table-body start-indent="0pt" end-indent="0pt">
              <fo:table-row>
                <fo:table-cell><fo:block text-align="left">Manager</fo:block> 
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left"><xsl:call-template name="person.name"/></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left">______________________________</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </xsl:when>
       <xsl:when test="@role = 'releaser'">
          <fo:table table-layout="fixed">
            <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="3" column-width="proportional-column-width(2)"/>
            <fo:table-body start-indent="0pt" end-indent="0pt">
              <fo:table-row>
                <fo:table-cell><fo:block text-align="left">Project Manager</fo:block> 
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left"><xsl:call-template name="person.name"/></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left">______________________________</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </xsl:when>
        <xsl:when test="@role = 'owner'">
          <fo:table table-layout="fixed">
            <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
            <fo:table-column column-number="3" column-width="proportional-column-width(2)"/>
            <fo:table-body start-indent="0pt" end-indent="0pt">
              <fo:table-row>
                <fo:table-cell><fo:block text-align="left">Owner</fo:block> 
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left"><xsl:call-template name="person.name"/></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block text-align="left">______________________________</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-body>
          </fo:table>
        </xsl:when>
      </xsl:choose>
    </fo:block>
  </xsl:template>
  
  <!--  ******************************-->
  
  
  <!-- ACHTUNG, ADDED FOR TEST PURPOSES, MZA/RKA -->
  <doc:refentry xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" id="SE2.logo.image">
    <refmeta>
      <refentrytitle>SE2.logo.image</refentrytitle>
      <refmiscinfo class="other" otherclass="datatype">uri</refmiscinfo>
    </refmeta>
    <refnamediv>
      <refname>SE2.logo.image</refname>
      <refpurpose>The URI of the image to be used for draft watermarks</refpurpose>
    </refnamediv>
    <refsection>
      <info>
        <title>Description</title>
      </info>
      <para>The image to be used for SE2 Logo</para>
    </refsection>
  </doc:refentry>
  <xsl:param name="SE2.logo.image"
    >http://www.eso.org/~rkarban/SE2/SE2Logo.jpg</xsl:param>

  <xsl:template name="draft.text">
    <xsl:choose>
      <xsl:when test="$draft.mode = 'yes'">
        <xsl:call-template name="gentext">
          <xsl:with-param name="key" select="'Draft'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$draft.mode = 'no'">
        <!-- nop -->
      </xsl:when>
      <xsl:when test="ancestor-or-self::*[@status][1]/@status = 'draft'">
        <xsl:call-template name="gentext">
          <xsl:with-param name="key" select="'Draft'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <!-- nop -->
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="header.content">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="position" select="''"/>
    <xsl:param name="gentext-key" select="''"/>

    
      <xsl:choose>
        <xsl:when test="$pageclass != 'titlepage' or ($pageclass = 'titlepage' and $sequence!='first')">
           <xsl:choose>
             <xsl:when test="$sequence='blank'">
               <!-- no output -->
             </xsl:when>
             
             <xsl:when test="$double.sided = 0 and $position='right'">
               
               <fo:block text-align="end">
                 <xsl:variable name="document.element" select="ancestor-or-self::*"/>
                 <xsl:value-of select="$document.element/d:info/d:productnumber[1]"/>/
                 <xsl:value-of select="$document.element/d:info/d:issuenum[1]"/>
               </fo:block>
               
               
                 <fo:block text-align="end">
                   Page <fo:page-number/>
                 </fo:block>
               
               
<!--               <xsl:call-template name="get.doc.date"/> -->
               <xsl:variable name="document.element" select="ancestor-or-self::*"/>
               <xsl:variable name="date">
                 <xsl:choose>
                   <xsl:when test="$document.element/d:info/d:pubdate[1]">
                     <xsl:value-of select="$document.element/d:info/d:pubdate[1]"/>
                   </xsl:when>
                   <xsl:otherwise>[could not find document date]</xsl:otherwise>
                 </xsl:choose>
               </xsl:variable>
               <fo:block> <xsl:value-of select="$date"/></fo:block>
             </xsl:when>
             
             <xsl:when test="$double.sided = 0 and $position = 'center'">  
               <fo:block text-align="center">
                 <xsl:variable name="document.element" select="ancestor-or-self::*"/>
                 <xsl:value-of select="$document.element/d:info/d:subtitle[1]"/>
<!--               <xsl:call-template name="get.doc.title"/>-->
               </fo:block>
               <!--<xsl:call-template name="log.message">
                 <xsl:with-param name="level">1 </xsl:with-param>
                 <xsl:with-param name="source">log message</xsl:with-param>
               </xsl:call-template>-->
             </xsl:when>
             
             <xsl:when test="$position = 'left'" >
               <fo:block>
                 <xsl:variable name="document.element" select="ancestor-or-self::*"/>
                 <xsl:value-of select="$document.element/d:info/d:publishername[1]"/>
               </fo:block>
             </xsl:when>
           </xsl:choose>
        </xsl:when>
    
        <xsl:when test="$pageclass = 'titlepage' and $sequence='first'">
          <xsl:choose>
            <xsl:when test="$position = 'left'">
              <fo:external-graphic content-height="1.5cm">
                <xsl:attribute name="src">
                  <xsl:call-template name="fo-external-image">
                    <xsl:with-param name="filename" select="$SE2.logo.image"/>
                  </xsl:call-template>   
                </xsl:attribute>
              </fo:external-graphic>
            </xsl:when>       
            <xsl:when test="$position = 'center'">
              <fo:table table-layout="fixed">
                <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
                <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
                <fo:table-body start-indent="0pt" end-indent="0pt">
                  <fo:table-row>
                    <fo:table-cell><fo:block text-align="left"></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block text-align="left"></fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block text-align="left">SE2 Challenge Team</fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </fo:table-body>
              </fo:table>
            </xsl:when>    
            <xsl:when test="$position = 'right'">
<!--              no output-->
              </xsl:when>
          </xsl:choose>
        </xsl:when>  
     </xsl:choose>        
  </xsl:template>
  
  <!--******************************-->

</xsl:stylesheet>
