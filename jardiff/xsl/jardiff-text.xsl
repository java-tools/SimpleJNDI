<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:jd="http://www.osjava.org/jardiff/0.1" version="1.0">

  <!-- Format output as html -->
  <xsl:output method="text" indent="yes"/>

  <xsl:template match="jd:diff">Comparing <xsl:value-of select="@old"/> to <xsl:value-of select="@new"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="jd:removed">
    <xsl:for-each select="jd:class">
Class removed: 
  <xsl:call-template name="print-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="jd:added">
    <xsl:for-each select="jd:class">
Class added: 
  <xsl:call-template name="print-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="jd:changed">
    <xsl:for-each select="jd:classchanged">
Class changed: <xsl:value-of select="@name"/>
      <xsl:if test="jd:removed/jd:method">
  Methods removed:
<xsl:for-each select="jd:removed/jd:method">
          <xsl:call-template name="print-method">
            <xsl:with-param name="classname" select="../../@name"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:added/jd:method">
  Methods added:
<xsl:for-each select="jd:added/jd:method">
          <xsl:call-template name="print-method">
            <xsl:with-param name="classname" select="../../@name"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:removed/jd:field">
  Fields removed:
<xsl:for-each select="jd:removed/jd:field">
          <xsl:call-template name="print-field"/>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:added/jd:field">
  Fields added:
<xsl:for-each select="jd:added/jd:field">
          <xsl:call-template name="print-field"/>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:changed/jd:classchange">
  Class descriptor changed:<xsl:for-each select="jd:changed/jd:classchange">
  old:
    <xsl:for-each select="jd:from/jd:class">
      <xsl:call-template name="print-class"/>
    </xsl:for-each>
  new:
    <xsl:for-each select="jd:to/jd:class">
      <xsl:call-template name="print-class"/>
    </xsl:for-each>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:changed/jd:methodchange">
        <xsl:for-each select="jd:changed/jd:methodchange">
  Method changed:
  old:
<xsl:for-each select="jd:from/jd:method">
      <xsl:call-template name="print-method">
        <xsl:with-param name="classname" select="../../../../@name"/>
      </xsl:call-template>
    </xsl:for-each>
  new:
<xsl:for-each select="jd:to/jd:method">
      <xsl:call-template name="print-method">
        <xsl:with-param name="classname" select="../../../../@name"/>
      </xsl:call-template>
    </xsl:for-each>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="jd:changed/jd:fieldchange">
        <xsl:for-each select="jd:changed/jd:fieldchange">
  Field changed:
  old:
<xsl:for-each select="jd:from/jd:field">
      <xsl:call-template name="print-field"/>
    </xsl:for-each>
  new:
<xsl:for-each select="jd:to/jd:field">
      <xsl:call-template name="print-field"/>
    </xsl:for-each>
        </xsl:for-each>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="print-class">
     <xsl:if test="@deprecated='yes'"><i>deprecated: </i></xsl:if>
     <xsl:value-of select="@access"/><xsl:value-of select="' '"/>
     <xsl:if test="@abstract='yes'">abstract<xsl:value-of select="' '"/></xsl:if>
     <xsl:if test="@static='yes'">static<xsl:value-of select="' '"/></xsl:if>
     <xsl:if test="@final='yes'">final<xsl:value-of select="' '"/></xsl:if>
     <xsl:value-of select="@name"/>
     <xsl:if test="@superclass!='java.lang.Object'"> extends <xsl:value-of select="@superclass"/></xsl:if>
     <xsl:for-each select="jd:implements">
       <xsl:if test="position()=1"> implements </xsl:if>
       <xsl:value-of select="@name"/>
       <xsl:if test="position()!=last()">, </xsl:if>
     </xsl:for-each>
  </xsl:template>

  <xsl:template name="print-method">
    <xsl:param name="classname"/>
<xsl:value-of select="'    '"/><xsl:if test="@deprecated='yes'">deprecated: </xsl:if>
    <xsl:value-of select="@access"/><xsl:value-of select="' '"/>
    <xsl:if test="@final='yes'">final </xsl:if>
    <xsl:if test="@static='yes'">static </xsl:if>
    <xsl:if test="@synchronized='yes'">synchronized </xsl:if>
    <xsl:if test="@abstract='yes'">abstract </xsl:if>
    <xsl:if test="@varargs='yes'">varargs </xsl:if>
    <xsl:choose>
      <xsl:when test="@name='&lt;init&gt;'">
        <xsl:call-template name="print-short-name">
          <xsl:with-param name="classname" select="$classname"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="jd:return/jd:type">
          <xsl:call-template name="print-type"/>
        </xsl:for-each>
        <xsl:value-of select="' '"/><xsl:value-of select="@name"/>
      </xsl:otherwise>
    </xsl:choose>(<xsl:for-each select="jd:arguments/jd:type"><xsl:call-template name="print-type"/><xsl:if test="position()!=last()">, </xsl:if></xsl:for-each>)<xsl:for-each select="jd:exception"><xsl:if test="position() = 1"> throws </xsl:if><xsl:value-of select="@name"/><xsl:if test="position()!=last()">, </xsl:if></xsl:for-each>;
</xsl:template>
  
  <xsl:template name="print-field">
<xsl:value-of select="'    '"/><xsl:if test="@deprecated='yes'">deprecated: </xsl:if>
    <xsl:value-of select="@access"/><xsl:value-of select="' '"/>
    <xsl:if test="@final='yes'">final </xsl:if>
    <xsl:if test="@static='yes'">static </xsl:if>
    <xsl:if test="@synchronized='yes'">synchronized </xsl:if>
    <xsl:if test="@abstract='yes'">abstract </xsl:if>
    <xsl:if test="@transient='yes'">transient </xsl:if>
    <xsl:if test="@volatile='yes'">volatile </xsl:if>
    <xsl:for-each select="jd:type">
      <xsl:call-template name="print-type"/>
    </xsl:for-each>
    <xsl:value-of select="concat(' ',@name)"/>
    <xsl:if test="@value"> = <xsl:value-of select="@value"/></xsl:if>;
</xsl:template>

  <xsl:template name="print-type">
    <xsl:value-of select="@name"/>
    <xsl:if test="@array='yes'">
      <xsl:call-template name="array-subscript">
        <xsl:with-param name="dimensions" select="@dimensions"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="array-subscript"><xsl:param name="dimensions"/>[]<xsl:if test="$dimensions > 1"><xsl:call-template name="array-subscript"><xsl:with-param name="dimensions" select="$dimensions - 1"/></xsl:call-template></xsl:if></xsl:template>

  <xsl:template name="print-short-name">
    <xsl:param name="classname"/>
    <xsl:choose>
      <xsl:when test="contains($classname,'.')">
        <xsl:call-template name="print-short-name">
          <xsl:with-param name="classname" select="substring-after($classname,'.')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$classname"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
