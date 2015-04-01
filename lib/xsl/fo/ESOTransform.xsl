<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:d="http://docbook.org/ns/docbook" 
  xmlns:exsl="http://exslt.org/common" version="1.0" exclude-result-prefixes="exsl d">

  <xsl:import href="docbook_custom.xsl"/>
  
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
 *    $Id: ESOTransform.xsl 616 2012-10-02 09:28:16Z karo-se2 $
 *
 * 
-->
<!--  ************don't put a separator after pubdate because it is the last entry******************-->
<xsl:template match="d:pubdate" mode="bibliography.mode">
  <fo:inline>
    <xsl:apply-templates mode="bibliography.mode"/>
 <!--   <xsl:value-of select="$biblioentry.item.separator"/> -->
  </fo:inline>
</xsl:template>

  <!--  ******************************-->

  <xsl:attribute-set name="list.block.spacing">
    <xsl:attribute name="margin-left">0.25in</xsl:attribute>
  </xsl:attribute-set>
  
  <!--  ******************************-->
  
  <xsl:attribute-set name="book.titlepage.recto.style">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.fontset"/>
    </xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>
  
  <!--  ******************************-->
  
  <xsl:attribute-set name="book.titlepage.verso.style">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.fontset"/>
    </xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
  </xsl:attribute-set>
  
  <!--  ******************************-->
  
  <xsl:attribute-set name="formal.title.properties" 
    use-attribute-sets="normal.para.spacing">
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>
  
  <!--  ******************************-->
  
  
<xsl:template name="book.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:variable name="recto.content">
    <!-- CHANGE - commented to avoid having two pages for the title page pageclass -->
<!--      <xsl:call-template name="book.titlepage.before.recto"/>-->
      <xsl:call-template name="book.titlepage.recto"/>
    </xsl:variable>
    <xsl:variable name="recto.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($recto.content) != '') or ($recto.elements.count &gt; 0)">
      <fo:block break-after="page"><xsl:copy-of select="$recto.content"/></fo:block>
    </xsl:if>
        
    <xsl:variable name="verso.content">
      <xsl:call-template name="book.titlepage.before.verso"/>
      <xsl:call-template name="book.titlepage.verso"/>
    </xsl:variable>
    <xsl:variable name="verso.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($verso.content) != '') or ($verso.elements.count &gt; 0)">
      <fo:block><xsl:copy-of select="$verso.content"/></fo:block>
    </xsl:if>
    <xsl:call-template name="book.titlepage.separator"/>
  </fo:block>
</xsl:template>
  
  <!--  ******************************-->

  
  <xsl:template name="book.titlepage.recto">
    <fo:block>
      <xsl:attribute name="space-before.minimum">8cm</xsl:attribute>
    </fo:block>
    <fo:block text-align="left" font-size="20pt" space-before="8cm" font-weight="bold" font-family="{$title.fontset}">
      <xsl:variable name="document.element" select="ancestor-or-self::*"/>
      <xsl:value-of select="$document.element/d:info/d:title[1]"/>
<!--     <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:info/d:title"/>  -->
    </fo:block>
    <fo:block text-align="left" font-size="12pt" space-before="0.5cm" font-weight="bold">
      <xsl:variable name="document.element" select="ancestor-or-self::*"/>
      <xsl:value-of select="$document.element/d:info/d:subtitle[1]"/>
<!--        <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:info/d:subtitle"/>-->
    </fo:block>
    <fo:block text-align="left" font-size="10pt">
      <xsl:variable name="document.element" select="ancestor-or-self::*"/>
      <fo:inline> <xsl:value-of select="$document.element/d:info/d:productnumber[1]"/></fo:inline>
      <fo:inline padding="0.3cm">ISSUE</fo:inline>
      <fo:inline padding="0.2cm"><xsl:value-of select="$document.element/d:info/d:issuenum[1]"/></fo:inline>
      <fo:block>
      <xsl:value-of select="$document.element/d:info/d:pubdate[1]"/>
        </fo:block>
<!--    <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="d:info/d:productnumber"/>-->
<!--    <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="d:info/d:issuenum"/>-->
<!--    <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="d:info/d:pubdate"/>-->
      <!--    <xsl:call-template name="get.doc.date"/>-->
      </fo:block>
    
    <fo:block>
      <xsl:attribute name="space-before.minimum">6cm</xsl:attribute>
    </fo:block>
    
    <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:info/d:author"/>
    <fo:table table-layout="fixed">
      <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="4" column-width="proportional-column-width(1)"/>
      <fo:table-body start-indent="0pt" end-indent="0pt">
        <fo:table-row>
          <fo:table-cell><fo:block text-align="left"></fo:block> 
          </fo:table-cell>
          <fo:table-cell>
            <fo:block text-align="left">Name</fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block text-align="left">Date</fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block text-align="left">Signature</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

  </xsl:template>

  <!--  ******************************-->

  <xsl:template match="d:title" mode="book.titlepage.recto.auto.mode">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format"  space-before="10cm" font-weight="bold" font-family="{$title.fontset}">
      
      <xsl:call-template name="division.title">
        <xsl:with-param name="node" select="ancestor-or-self::d:book[1]"/>
      </xsl:call-template>
    </fo:block>
  </xsl:template>
  
  <!--  ******************************-->
  <!--  continouos numbering -->
  <xsl:template name="initial.page.number">auto</xsl:template>
  <xsl:template name="page.number.format">1</xsl:template>
  
  <xsl:template match="d:info/d:author[@role]" mode="book.titlepage.verso.mode">
    <xsl:choose>
      <xsl:when test="@role = 'author'">
    <fo:table table-layout="fixed" border-width="0.5mm" border-style="solid">
      <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
      <fo:table-body start-indent="0pt" end-indent="0pt">
        <fo:table-row>
          <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
            <fo:block text-align="left"><xsl:call-template name="person.name"/></fo:block>
          </fo:table-cell>
          <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
            <fo:block text-align="left"><xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="d:affiliation"/></fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
      </xsl:when>
    </xsl:choose>   
  </xsl:template>
  
  
  <!--- ChangeRecord implementation, MBDG-11 -->

<xsl:template match="d:bookinfo/d:revhistory/d:revision"  mode="book.titlepage.verso.mode">
  <xsl:variable name="revnumber" select="d:revnumber"/>
  <xsl:variable name="revdate"   select="d:date"/>
  <xsl:variable name="revauthor" select="d:authorinitials|d:author"/>
  <xsl:variable name="revremark" select="d:revremark"/>
  <xsl:variable name="revdescription" select="d:revdescription"/>
  <fo:table-row>
  
    <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid" >
      <fo:block>
        <xsl:call-template name="anchor"/>
        <xsl:if test="$revnumber">
          <xsl:call-template name="gentext.space"/>
          <xsl:apply-templates select="$revnumber[1]"/>
        </xsl:if>
      </fo:block>
    </fo:table-cell>
  
    <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
      <fo:block>
        <xsl:apply-templates select="$revdate[1]"/>
      </fo:block>
    </fo:table-cell>
    
    
    <!-- revdescription is used to comment on modified sections  -->
   <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
      <fo:block>
      <xsl:apply-templates select="$revdescription[1]" />
      </fo:block>
    </fo:table-cell>

	<fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
		<fo:block>
			<xsl:if test="$revremark">
				<fo:block>
					<xsl:apply-templates select="$revremark[1]" />
				</fo:block>
			</xsl:if>

		</fo:block>
	</fo:table-cell>
  </fo:table-row>
 </xsl:template>

  
  <xsl:template name="book.titlepage.verso">
      <fo:block>
        <xsl:attribute name="space-before.minimum">2cm</xsl:attribute>
      </fo:block>
    <fo:block font-size="20">
      Authors
    </fo:block>
    <fo:table table-layout="fixed" border-width="0.5mm" border-style="solid">
      <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
      <fo:table-body start-indent="0pt" end-indent="0pt">
        <fo:table-row>
          <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
            <fo:block text-align="left" font-weight="bold">Name</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
            <fo:block text-align="left" font-weight="bold">Affiliation</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
      </fo:table>

    <xsl:apply-templates mode="book.titlepage.verso.mode" select="d:info/d:author"/>
    
<!--    <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="d:info/d:author/d:affiliation"/>-->

    <fo:block>
      <xsl:attribute name="space-before.minimum">1cm</xsl:attribute>
    </fo:block>
    <fo:block font-size="20">
      Change Record
    </fo:block>
    <fo:table table-layout="fixed" border-width="0.5mm" border-style="solid">
        <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
        <fo:table-column column-number="4" column-width="proportional-column-width(4)"/>
        <fo:table-body start-indent="0pt" end-indent="0pt">
          <fo:table-row>
            <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
              <fo:block text-align="left" font-weight="bold">Issue</fo:block> 
            </fo:table-cell>
            <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
              <fo:block text-align="left" font-weight="bold">Date</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
              <fo:block text-align="left" font-weight="bold">Section / Paragraph affected</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm" border-width="0.5mm" border-style="solid">
              <fo:block text-align="left" font-weight="bold">Reason / Initiation Documents / Remarks</fo:block>
            </fo:table-cell>
          </fo:table-row>
      <xsl:apply-templates mode="book.titlepage.verso.mode" select="d:bookinfo/d:revhistory/d:revision"/>
        </fo:table-body>
      </fo:table>
    <fo:block>
      <xsl:attribute name="space-before.minimum">1cm</xsl:attribute>
    </fo:block>
  </xsl:template>
  
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
                <fo:table-cell><fo:block text-align="left">WP Manager</fo:block> 
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
                <fo:table-cell><fo:block text-align="left">Releaser</fo:block> 
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
  
  <xsl:template match="d:book/d:info/d:author[@role]|d:book/d:info/d:author[@role]" mode="book.titlepage.verso.auto.mode">
    <fo:block  xsl:use-attribute-sets="book.titlepage.verso.style">
      <xsl:choose>
        <xsl:when test="@role = 'author'">
          <xsl:call-template name="person.name"/>
        </xsl:when>
      </xsl:choose>
    </fo:block>
  </xsl:template>
  
  <!--  ******************************-->
  
  <xsl:template match="d:info/d:revhistory" mode="book.titlepage.verso.auto.mode">
    
    <xsl:variable name="explicit.table.width">
      <xsl:call-template name="pi.dbfo_table-width"/>
    </xsl:variable>
    
    <xsl:variable name="table.width">
      <xsl:choose>
        <xsl:when test="$explicit.table.width != ''">
          <xsl:value-of select="$explicit.table.width"/>
        </xsl:when>
        <xsl:when test="$default.table.width = ''">
          <xsl:text>100%</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$default.table.width"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <fo:table table-layout="fixed" width="{$table.width}" xsl:use-attribute-sets="revhistory.table.properties">
      <fo:table-column column-number="1" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="2" column-width="proportional-column-width(1)"/>
      <fo:table-column column-number="3" column-width="proportional-column-width(1)"/>
      <fo:table-body start-indent="0pt" end-indent="0pt">
        <fo:table-row>
          <fo:table-cell number-columns-spanned="3" xsl:use-attribute-sets="revhistory.table.cell.properties">
            <fo:block xsl:use-attribute-sets="revhistory.title.properties">
              <xsl:choose>
                <xsl:when test="d:title|d:info/d:title">
                  <xsl:apply-templates select="d:title|d:info/d:title" mode="titlepage.mode"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="gentext">
                    <xsl:with-param name="key" select="'RevHistory'"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <xsl:apply-templates select="*[not(self::d:title)]" mode="titlepage.mode"/>
      </fo:table-body>
    </fo:table>
  </xsl:template>

<!--*************************************************-->
  <!-- change default proportions of header columns -->
  
  <xsl:param name="header.column.widths">1 8 4</xsl:param>
  
  <!--******************************-->
  
  <!-- Change default values of parameters defined in params.xsl -->
  <xsl:param name="generate.toc">
    /appendix toc,title
    article/appendix  nop
    /article  toc,title
    book      toc,title
    /chapter  toc,title
    part      toc,title
    /preface  toc,title
    reference toc,title
    /sect1    toc
    /sect2    toc
    /sect3    toc
    /sect4    toc
    /sect5    toc
    /section  toc
    set       toc,title
  </xsl:param>
  
  <xsl:param name="biblioentry.item.separator">, </xsl:param>
  <xsl:param name="body.font.family">Helvetica</xsl:param>
  <xsl:param name="body.font.size">10pt</xsl:param>
  <xsl:param name="body.margin.top">0.7in</xsl:param>
  <xsl:param name="paper.type">A4</xsl:param>
  <xsl:param name="fop1.extensions">1</xsl:param>
  <xsl:param name="fop.extensions">0</xsl:param>
  <xsl:param name="toc.section.depth">5</xsl:param>
  
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>
  <xsl:param name="headers.on.blank.pages" select="1"/>
  <xsl:param name="formal.title.placement">
    figure after
    example after
    equation after
    table after
    procedure after
    task after
  </xsl:param>
  
  <xsl:attribute-set name="section.title.level1.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.4"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level2.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.3"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level3.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.2"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level4.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.1"/>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:param name="body.start.indent">
    <xsl:choose>
      <xsl:when test="$fop.extensions != 0">0pt</xsl:when>
      <xsl:when test="$passivetex.extensions != 0">0pt</xsl:when>
      <xsl:otherwise>0pc</xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  
  <!-- Change default values of parameters defined in titlepages-templates.xsl -->
  
  <xsl:template match="d:title" mode="chapter.titlepage.recto.auto.mode">
    <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="chapter.titlepage.recto.style" font-weight="bold">
      <xsl:attribute name="font-size">
        <xsl:value-of select="$body.font.master * 1.6"/>
        <xsl:text>pt</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="component.title">
        <xsl:with-param name="node" select="ancestor-or-self::d:chapter[1]"/>
      </xsl:call-template>
    </fo:block>
  </xsl:template>
  
  <!--******************************-->
  
  <xsl:attribute-set name="formal.title.properties" use-attribute-sets="normal.para.spacing">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.2"></xsl:value-of>
      <xsl:text>pt</xsl:text>
    </xsl:attribute>
    <xsl:attribute name="hyphenate">false</xsl:attribute>
    <xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">0.6em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">0.8em</xsl:attribute>
  </xsl:attribute-set>
 
  <!--******************************-->
 
  <xsl:template match="caption">
    <fo:block  text-align="center">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
    
  <xsl:template match="processing-instruction('linebreak')">
    <fo:block/>
  </xsl:template>
  
  <!-- ACHTUNG, ADDED FOR TEST PURPOSES, MZA/RKA -->
  <doc:refentry xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" id="ESO.logo.image">
    <refmeta>
      <refentrytitle>ESO.logo.image</refentrytitle>
      <refmiscinfo class="other" otherclass="datatype">uri</refmiscinfo>
    </refmeta>
    <refnamediv>
      <refname>ESO.logo.image</refname>
      <refpurpose>The URI of the image to be used for draft watermarks</refpurpose>
    </refnamediv>
    <refsection>
      <info>
        <title>Description</title>
      </info>
      <para>The image to be used for ESO Logo</para>
    </refsection>
  </doc:refentry>
  <xsl:param name="ESO.logo.image"
    >http://webnri.hq.eso.org/downloads/eso-logo-p3005.jpg</xsl:param>

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

  <xsl:template name="get.doc.date">
    <xsl:choose>
      <xsl:when test="//*[local-name() = 'date']">
        <xsl:value-of select="//*[local-name() = 'date']"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <!--******************************-->
  
  <xsl:template name="footer.content">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="position" select="''"/>
    <xsl:param name="gentext-key" select="''"/>
      
    
    <!--
      <fo:block>
      <xsl:value-of select="$pageclass"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$sequence"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$position"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$gentext-key"/>
      </fo:block>
    -->

    <fo:block>
      <!-- pageclass can be front, body, back -->
      <!-- sequence can be odd, even, first, blank -->
      <!-- position can be left, center, right -->
      <xsl:choose>
        <xsl:when test="$pageclass = 'titlepage'">
          <!-- nop; no footer on title pages -->
        </xsl:when>

        <xsl:when test="$double.sided != 0 and $sequence = 'even'
          and $position='left'">
          <fo:page-number/>
        </xsl:when>

        <xsl:when
          test="$double.sided != 0 and ($sequence = 'odd' or $sequence = 'first')
          and $position='right'">
          <fo:page-number/>
        </xsl:when>

        <xsl:when test="$double.sided = 0 and $position='center'">
          <!-- nop -->
        </xsl:when>

        <xsl:when test="$sequence='blank'">
          <xsl:choose>
            <xsl:when test="$double.sided != 0 and $position = 'left'">
              <fo:page-number/>
            </xsl:when>
            <xsl:when test="$double.sided = 0 and $position = 'center'">
              <fo:page-number/>
            </xsl:when>
            <xsl:otherwise>
              <!-- nop -->
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>


        <xsl:otherwise>
          <!-- nop -->
        </xsl:otherwise>
      </xsl:choose>
    </fo:block>
  </xsl:template>

<!--******************************-->

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
                   Page <fo:page-number/> / <xsl:value-of select="$ebnf.statement.terminator"/>
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
                    <xsl:with-param name="filename" select="$ESO.logo.image"/>
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
                    <fo:table-cell><fo:block text-align="left">European Organisation</fo:block> <fo:block text-align="left">for Astronomical Research in the Southern Hemisphere</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block text-align="left">Organisation Européenne pour des Recherches Astronomiques dans I'Hémisphère Austral</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                      <fo:block text-align="left">Europäische Organisation für astronomische Forschung in der südlichen Hemisphäre</fo:block>
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
  
  <xsl:template name="header.table">
    <xsl:param name="pageclass" select="''"/>
    <xsl:param name="sequence" select="''"/>
    <xsl:param name="gentext-key" select="''"/>
    
    <!-- default is a single table style for all headers -->
    <!-- Customize it for different page classes or sequence location -->
    
    <xsl:choose>
      <xsl:when test="$pageclass = 'index'">
        <xsl:attribute name="margin-{$direction.align.start}">0pt</xsl:attribute>
      </xsl:when>
    </xsl:choose>
    
    <xsl:variable name="column1">
      <xsl:choose>
        <xsl:when test="$double.sided = 0">1</xsl:when>
        <xsl:when test="$sequence = 'first' or $sequence = 'odd'">1</xsl:when>
        <xsl:otherwise>3</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="column3">
      <xsl:choose>
        <xsl:when test="$double.sided = 0">3</xsl:when>
        <xsl:when test="$sequence = 'first' or $sequence = 'odd'">3</xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="candidate">
      <fo:table xsl:use-attribute-sets="header.table.properties">
        <xsl:call-template name="head.sep.rule">
          <xsl:with-param name="pageclass" select="$pageclass"/>
          <xsl:with-param name="sequence" select="$sequence"/>
          <xsl:with-param name="gentext-key" select="$gentext-key"/>
        </xsl:call-template>
        
        <fo:table-column column-number="1">
          <xsl:attribute name="column-width">
            <xsl:text>proportional-column-width(</xsl:text>
            <xsl:call-template name="header.footer.width">
              <xsl:with-param name="location">header</xsl:with-param>
              <xsl:with-param name="position" select="$column1"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>
        </fo:table-column>
        <fo:table-column column-number="2">
          <xsl:attribute name="column-width">
            <xsl:text>proportional-column-width(</xsl:text>
            <xsl:call-template name="header.footer.width">
              <xsl:with-param name="location">header</xsl:with-param>
              <xsl:with-param name="position" select="2"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>
        </fo:table-column>
        <xsl:choose>
          <xsl:when test="$pageclass = 'titlepage' and $gentext-key = 'book'
            and $sequence='first'">
            <!-- no output -->
          </xsl:when>
          <xsl:when test="$sequence = 'blank' and $headers.on.blank.pages = 0">
            <fo:block><xsl:call-template name="blank.page.content"/></fo:block>
          </xsl:when>
          <xsl:otherwise>
            <fo:table-column column-number="3">
              <xsl:attribute name="column-width">
                <xsl:text>proportional-column-width(</xsl:text>
                <xsl:call-template name="header.footer.width">
                  <xsl:with-param name="location">header</xsl:with-param>
                  <xsl:with-param name="position" select="$column3"/>
                </xsl:call-template>
                <xsl:text>)</xsl:text>
              </xsl:attribute>
            </fo:table-column>
          </xsl:otherwise>
        </xsl:choose>
        
        
        <fo:table-body>
          <fo:table-row>
            <xsl:attribute name="block-progression-dimension.minimum">
              <xsl:value-of select="$header.table.height"/>
            </xsl:attribute>
            <fo:table-cell text-align="start"
              display-align="before">
              <xsl:if test="$fop.extensions = 0">
                <xsl:attribute name="relative-align">baseline</xsl:attribute>
              </xsl:if>
              <fo:block>
                <xsl:call-template name="header.content">
                  <xsl:with-param name="pageclass" select="$pageclass"/>
                  <xsl:with-param name="sequence" select="$sequence"/>
                  <xsl:with-param name="position" select="$direction.align.start"/>
                  <xsl:with-param name="gentext-key" select="$gentext-key"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell text-align="center"
              display-align="before">
              <xsl:if test="$fop.extensions = 0">
                <xsl:attribute name="relative-align">baseline</xsl:attribute>
              </xsl:if>
              <fo:block>
                <xsl:call-template name="header.content">
                  <xsl:with-param name="pageclass" select="$pageclass"/>
                  <xsl:with-param name="sequence" select="$sequence"/>
                  <xsl:with-param name="position" select="'center'"/>
                  <xsl:with-param name="gentext-key" select="$gentext-key"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
            <xsl:choose>
              <xsl:when test="$pageclass = 'titlepage' and $gentext-key = 'book'
                and $sequence='first'">
                <!-- no output -->
              </xsl:when>
              <xsl:when test="$sequence = 'blank' and $headers.on.blank.pages = 0">
                <!-- no output -->
              </xsl:when>
              <xsl:otherwise>
            <fo:table-cell text-align="right"
              display-align="before">
              <xsl:if test="$fop.extensions = 0">
                <xsl:attribute name="relative-align">baseline</xsl:attribute>
              </xsl:if>
              <fo:block>
                <xsl:call-template name="header.content">
                  <xsl:with-param name="pageclass" select="$pageclass"/>
                  <xsl:with-param name="sequence" select="$sequence"/>
                  <xsl:with-param name="position" select="$direction.align.end"/>
                  <xsl:with-param name="gentext-key" select="$gentext-key"/>
                </xsl:call-template>
              </fo:block>
            </fo:table-cell>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </xsl:variable>
    
    <!-- Really output a header? -->
    <xsl:choose>
      <xsl:when test="$pageclass = 'titlepage' and $gentext-key = 'book'
        and $sequence='first'">
      <xsl:copy-of select="$candidate"/>
      </xsl:when>
      <xsl:when test="$sequence = 'blank' and $headers.on.blank.pages = 0">
        <!-- no output -->
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$candidate"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


<!--************************************************************************************-->

  <xsl:template name="blank.page.content">
    <fo:static-content flow-name="blank-body">
      <fo:block text-align="center">Page intentionally left blank</fo:block>
    </fo:static-content>
  </xsl:template>

  <!--************************************************************************************-->

   
</xsl:stylesheet>
