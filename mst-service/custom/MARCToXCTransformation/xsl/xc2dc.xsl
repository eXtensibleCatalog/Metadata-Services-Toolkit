<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:marc="http://www.loc.gov/MARC21/slim" 
	xmlns:oai="http://www.openarchives.org/OAI/2.0/"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:dc="http://purl.org/dc/elements/1.1/" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xc="http://www.extensiblecatalog.info/Elements"
	xmlns:dcterms="http://purl.org/dc/terms/"
	exclude-result-prefixes="marc">

	<!--
	<xsl:template match="oai:OAI-PMH/oai:ListRecords/oai:record/oai:metadata/xc:frbr">
	-->
	<xsl:template match="/">
		<xsl:if test="xc:frbr">
			<oai_dc:dc>
				<xsl:for-each select="xc:frbr/xc:entity/*">
					<xsl:if test="namespace-uri(.) = 'http://purl.org/dc/terms/'">
						<xsl:if test="
								local-name(.) = 'title'            or 
								local-name(.) = 'creator'          or
								local-name(.) = 'subject'          or
								local-name(.) = 'description'      or
								local-name(.) = 'publisher'        or
								local-name(.) = 'contributor'      or
								local-name(.) = 'date'             or
								local-name(.) = 'type'             or
								local-name(.) = 'format'           or
								local-name(.) = 'identifier'       or
								local-name(.) = 'source'           or
								local-name(.) = 'language'         or 
								local-name(.) = 'relation'         or
								local-name(.) = 'coverage'         or
								local-name(.) = 'rights'
							">
							<xsl:element name="{local-name(.)}" namespace="http://purl.org/dc/elements/1.1/">
								<xsl:value-of select="." />
							</xsl:element>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</oai_dc:dc>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>

