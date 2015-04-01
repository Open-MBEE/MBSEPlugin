# settings for sqa4 initially
ECHO=/
JAVA_HOME=/opt/jdk1.7.0_51
JAVAC=$(JAVA_HOME)/bin/javac
JAR=$(JAVA_HOME)/bin/jar
MAGIC_DRAW_HOME=\/opt\/MagicDraw_UML-17.0.5
#MAGIC_DRAW_HOME=PIPPO
TMPCLASSPATH:=grep "kind=\"var\"" ../.classpath | sed -n 's/.*path=\"\(.*\)\".*$$/\1/p;'|  sed  's/MAGIC_DRAW_HOME/$(MAGIC_DRAW_HOME)/g;' | tr '\n' ':' ; ls -1 $(PWD)/lib/*.jar | tr '\n' ':'
CLASSPATH:= $(subst,MAGIC_DRAW_HOME,$(MAGIC_DRAW_HOME),$(TMPCLASSPATH))

FILESLIST:=find . -name \*.java -type f ! -path '*/.svn/*' | tr '\n' ' '

ifeq ($(HOSTNAME),sqa4)
SVNVERSION_CMD:=/opt/CollabNet_Subversion/bin/svnversion
else
SVNVERSION_CMD:=svnversion
endif

ZIPFILE=MBSE.zip
JARFILE=MBSEPlugin.jar

.PHONY: all
all: transferZip


src/MANIFEST.txt:
	-@rm $@
	@echo "Name: MBSEPlugin" >> $@
	@echo "Specification-Title: Model Based Systems Engineering Plugin for MagicDraw" >>  $@
	@echo "Specification-Vendor: ESO DOE SED" >>  $@
	@echo "Implementation-Version: `$(SVNVERSION_CMD) -c .`" >>  $@


$(JARFILE):src/MANIFEST.txt
	@echo "	Compiling..."
	cd src && FILELIST=`$(FILESLIST)`  \
	  && $(JAVAC) -classpath `$(TMPCLASSPATH)` $${FILELIST} && $(JAR)   -cmf  MANIFEST.txt $(JARFILE)  `find . -name \*.class -type f ! -path '*/.svn/*' | tr '\n' ' '` && mv $(JARFILE) ..

.PHONY: makeZip
makeZip: $(JARFILE) Changelog
	@echo "	Zipping..."
	@zip $(ZIPFILE) Changelog plugin.xml $(JARFILE)	

.PHONY: transferZip
transferZip: makeZip
	@echo "	Transferring..."
	@scp $(ZIPFILE) sqa-ops@webnri:docs/downloads/

clean:
	@find src -name \*.class -type f -exec rm -fr {} \;
	@rm -f $(ZIPFILE) $(JARFILE) Changelog

Changelog:
	@cd src && svn2cl.sh > ../Changelog && unix2dos Changelog




