# This project is read only and was moved to gitlab: [https://gitlab.com/ManfredTremmel/gwt-commons-codec](https://gitlab.com/ManfredTremmel/gwt-commons-codec)

gwt-commons-codec
=================

A compatible GWT port of the apache commons codec, which provides most of the functionality also on client (browser) side.

The Package itself has to be includes as binary and source package. I've done it this way, to give you the possibility to take the original apache commons binary package (full compatible) and combine it with the gwt-commons version of the source package for the gwt compiler, this is useful, if you do have the binary package already in the dependency list.

gwt-commons-codec requires gwt >= 2.7.0, older versions are not supported!

```
    <dependency>
      <groupId>de.knightsoft-net</groupId>
      <artifactId>gwt-commons-codec</artifactId>
      <version>1.11-0</version>
    </dependency>
```

GWT Integration
---------------

Add this inherit command into your project .gwt.xml file:

```
<inherits name="org.apache.commons.GWTCommonsCodec" />
```
