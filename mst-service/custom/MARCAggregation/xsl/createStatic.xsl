<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	                      xmlns:marc="http://www.loc.gov/MARC21/slim">
    <xsl:output indent="yes" />

    <!--Empty template for the content we want to redact -->
    <!--xsl:template match="*[CCC[not(.='B')]]" /-->
    <xsl:template match="marc:controlfield[@tag='001']" />
    <xsl:template match="marc:controlfield[@tag='003']" />
    <!--the 035 deleter is not quite right, leaves the 1st line intact -->
    <!--xsl:template match="marc:datafield[@tag='035']/marc:subfield[@code='a']" /-->

    <!--By default, copy all content forward -->
    	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>

