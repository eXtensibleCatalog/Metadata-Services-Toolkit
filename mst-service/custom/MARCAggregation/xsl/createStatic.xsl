<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
        xmlns:marc="http://www.loc.gov/MARC21/slim"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="marc">
    <xsl:output indent="yes" />

    <!--Empty template for the content we want to redact -->
    <!--xsl:template match="*[CCC[not(.='B')]]" /-->
    <xsl:template match="marc:controlfield[@tag='001']" />
    <xsl:template match="marc:controlfield[@tag='003']" />
    <xsl:template match="marc:datafield[@tag='035']" />

    <!--By default, copy all content forward -->
    	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>

