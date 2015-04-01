<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:d="http://docbook.org/ns/docbook" 
  xmlns:exsl="http://exslt.org/common" version="1.0" exclude-result-prefixes="exsl d">

  <xsl:import href="ESOTransform.xsl"/>
  
<!-- This stylesheet was copied from C:\Program Files\Oxygen XML Editor 12\frameworks\docbook\xsl\fo/titlepage-templates.xsl-->
<!-- changes wrt original are marked with the keyword CHANGE -->


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
                <fo:table-cell><fo:block text-align="left">Head of Project Office</fo:block> 
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
  
  
   
</xsl:stylesheet>
