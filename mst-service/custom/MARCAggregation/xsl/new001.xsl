<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
        xmlns:marc="http://www.loc.gov/MARC21/slim"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="marc">
    <xsl:output indent="yes" />
    <xsl:param name="new001"/>
    <xsl:param name="new003"/>

    <xsl:variable name="leader" select="//marc:leader" />
    <xsl:variable name="the001" select="//marc:controlfield[@tag='001']" />
    <xsl:variable name="the003" select="//marc:controlfield[@tag='003']" />
    <xsl:variable name="the005" select="//marc:controlfield[@tag='005']" />
    <xsl:variable name="the035s" select="//marc:datafield[@tag='035']" />
    <xsl:variable name="the003_001" select="concat(concat(concat('(',$the003),')'),$the001)" />

    <xsl:template match="@* | node()">
       <xsl:copy>
           <xsl:apply-templates select="@* | node()" />
       </xsl:copy>
    </xsl:template>

    <xsl:template match="marc:leader">
       <xsl:call-template name="applyLeader" />
       <xsl:call-template name="apply001" />
       <xsl:call-template name="apply003" />
       <xsl:call-template name="apply005" />
       <xsl:call-template name="apply035" />
    </xsl:template>

    <xsl:template match="marc:controlfield[@tag='001']" />
    <xsl:template match="marc:controlfield[@tag='003']" />
    <xsl:template match="marc:controlfield[@tag='005']" />
    <xsl:template match="marc:datafield[@tag='035']" />

    <xsl:template name="applyLeader">
       <xsl:copy-of select="$leader" />
       <xsl:text>&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="apply001">
       <xsl:choose>
           <xsl:when test="$new001 != ''">
              <marc:controlfield tag="001"><xsl:value-of select="$new001" /></marc:controlfield>
              <xsl:text>&#xa;</xsl:text>
           </xsl:when>
       </xsl:choose>
    </xsl:template>

    <xsl:template name="apply003">
       <xsl:choose>
           <xsl:when test="$new003 != ''">
              <marc:controlfield tag="003"><xsl:value-of select="$new003" /></marc:controlfield>
              <xsl:text>&#xa;</xsl:text>
           </xsl:when>
       </xsl:choose>
    </xsl:template>

    <xsl:template name="apply005">
        <marc:controlfield tag="005"><xsl:value-of select="$the005" /></marc:controlfield>
        <xsl:text>&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="apply035">
       <xsl:choose>
          <xsl:when test="$the001 != '' and $the003 != ''">
             <xsl:choose>
                <xsl:when test="not($the035s/marc:subfield[@code='a']=$the003_001)">
                   <marc:datafield ind1=" " ind2=" " tag="035">
                      <marc:subfield code="a"><xsl:value-of select="$the003_001"/></marc:subfield>
                   </marc:datafield>
                   <xsl:text>&#xa;</xsl:text>
                </xsl:when>
             </xsl:choose>
          </xsl:when>
       </xsl:choose>
       <xsl:copy-of select="$the035s" />
    </xsl:template>

</xsl:stylesheet>

