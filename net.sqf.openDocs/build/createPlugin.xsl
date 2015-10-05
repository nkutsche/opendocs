<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:xt="http://www.oxygenxml.com/ns/extension" exclude-result-prefixes="#all" version="2.0" xmlns:jfile="java:java.io.File">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Sep 27, 2015</xd:p>
            <xd:p><xd:b>Author:</xd:b> Nico Kutscherauer</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <xsl:param name="version" select="'0.8'"/>
    <xsl:param name="target" select="'../../oxygen-debuger/plugins/openDocs/plugin.xml'"/>
    <xsl:param name="release" as="xs:string"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="targetAbs" select="resolve-uri($target)"/>
    <xsl:variable name="releaseAbs" select="xs:string(jfile:toURI(jfile:new($release)))" as="xs:string"/>
    <xsl:variable name="addon_file" select="tokenize($releaseAbs, '/')[last()]"/>

    <xsl:variable name="build" select="format-date(current-date(), '[Y0000][M00][D00]')"/>
    
    <xsl:template match="/">
        <xsl:result-document href="{$targetAbs}">
            <xsl:apply-templates/>
        </xsl:result-document>
        <xsl:result-document href="{resolve-uri('conf/about.htm', $targetAbs)}" method="xhtml" indent="no" omit-xml-declaration="yes" doctype-system="../plugin.dtd">
            <xsl:apply-templates select="doc('about.htm')/node()" mode="aboutHtml"/>
        </xsl:result-document>

        <xsl:result-document href="{$releaseAbs}">
            <xt:extensions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.oxygenxml.com/ns/extension http://www.oxygenxml.com/ns/extension/extensions.xsd">
                <xsl:variable name="collectionPath" select="concat(resolve-uri('.', $releaseAbs), '?select=', $addon_file, '.xml;recurse=yes;on-error=ignore')"/>
                <xsl:variable name="currentRelease" select="resolve-uri(concat($version, '/?select=', $addon_file, '.xml'), $releaseAbs)"/>
                <xsl:variable name="collection" select="collection($collectionPath)"/>
                <xsl:for-each select="$collection">
                    <xsl:if test="/xt:extension">
                        <xsl:call-template name="createXInclude"/>
                    </xsl:if>
                </xsl:for-each>
                <xsl:if test="not(collection($currentRelease)/xt:extension)">
                    <xsl:call-template name="createXInclude">
                        <xsl:with-param name="docPath" select="resolve-uri(concat($version, '/', $addon_file, '.xml'), $releaseAbs)"/>
                    </xsl:call-template>
                </xsl:if>
            </xt:extensions>
        </xsl:result-document>

        <xsl:apply-templates select="doc('openDocsAddon.xml')/node()" mode="addons"/>
    </xsl:template>

    <xsl:template name="createXInclude" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <xsl:param name="docPath" select="document-uri(/)"/>
        <xsl:variable name="href" select="string-join(tokenize($docPath, '/')[position() ge (last() - 1)], '/')"/>
        <xsl:element name="xi:include" xmlns:xi="http://www.w3.org/2001/XInclude">
            <xsl:attribute name="href" select="$href"/>
            <xsl:element name="xi:fallback">
                <xsl:comment>Warning - missing document <xsl:value-of select="$href"/></xsl:comment>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xt:extension" mode="addons">

        <xsl:copy>
            <xsl:apply-templates select="@*" mode="#current"/>
            <xsl:apply-templates select="node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="runtime/librariesFolder">
        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="dir" select="resolve-uri($name, $targetAbs)"/>
        <xsl:variable name="jfile" select="jfile:new($dir)"/>
        <xsl:for-each select="jfile:list($jfile)">
            <library name="{$name}/{.}"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="h:span[@class = 'version']" mode="aboutHtml addons">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="$version"/>
            <xsl:text>, build </xsl:text>
            <xsl:value-of select="$build"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="h:img[@id = 'logo']" mode="addons">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="#current"/>
            <xsl:attribute name="src" select="'http://nkutsche.com/wp-content/uploads/2015/09/openDocs.gif'"/>
            <xsl:apply-templates select="node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="h:div[@id = 'changelog']" mode="addons">
        <xsl:variable name="changelog" select="unparsed-text('../documenation/changelog.txt')"/>
        <xsl:variable name="cl_rows" select="tokenize($changelog, '\n')"/>
        <xsl:copy xmlns="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:for-each-group select="$cl_rows" group-adjacent="starts-with(., '*')">
                <xsl:choose>
                    <xsl:when test="current-grouping-key()">
                        <ul>
                            <xsl:for-each select="current-group()">
                                <li>
                                    <p>
                                        <xsl:value-of select="replace(., '^\*\s*', '')"/>
                                    </p>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:for-each select="current-group()">
                            <xsl:variable name="splitted" select="tokenize(., '\s')"/>
                            <xsl:variable name="firstChars" select="$splitted[1]"/>
                            <xsl:variable name="firstLenght" select="string-length($firstChars)"/>
                            <xsl:variable name="el" select="
                                    if (contains($firstChars, '-'))
                                    then
                                        if ($firstLenght gt 6 or not($splitted[2])) then
                                            ('hr')
                                        else
                                            (concat('h', $firstLenght))
                                    else
                                        ('p')"/>
                            <xsl:element name="{$el}">
                                <xsl:value-of select="
                                        if ($el = ('hr',
                                        'br')) then
                                            ()
                                        else
                                            ($splitted[position() ge 2])"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each-group>
        </xsl:copy>
    </xsl:template>

    <!-- 
        copies all nodes:
    -->
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="#current"/>
            <xsl:apply-templates select="node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xt:version" mode="addons">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="
                    if (normalize-space(tokenize($version, '\.')[3]) = '') then
                        (concat($version, '.0'))
                    else
                        ($version)"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xt:description" mode="addons">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="doc('about.htm')//h:body/node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xt:location" mode="addons">
        <xt:location href="openDocs{$version}build{$build}.zip"/>
    </xsl:template>

    <xsl:variable name="versionPattern">\$\{version\}</xsl:variable>
    <xsl:template match="@*[matches(., $versionPattern)]">
        <xsl:attribute name="{name()}">
            <xsl:analyze-string select="." regex="{$versionPattern}">
                <xsl:matching-substring>
                    <xsl:value-of select="$version"/>
                </xsl:matching-substring>
                <xsl:non-matching-substring>
                    <xsl:value-of select="."/>
                </xsl:non-matching-substring>
            </xsl:analyze-string>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xt:license" mode="addons">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
            <xsl:value-of select="tokenize(unparsed-text('../EULA-OpenDocs-oXygen-plugin.txt'), '&#xD;')" separator="&#x000A;"/>
            <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
        </xsl:copy>

    </xsl:template>

</xsl:stylesheet>
