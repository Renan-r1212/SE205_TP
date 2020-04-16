.SUFFIXES: .p.java .java .class
.SECONDARY:

TAR=tar
## Utiliser /usr/local/bin/tar dans le PATH ou bien faire TAR=gtar

JAVAPREP=cpp -x c
PREPFLAGS=-C -P
JAVACFLAGS=

JAVASOURCES=\
Utils.java\
BoundedBuffer.java\
SemBoundedBuffer.java\
NatBoundedBuffer.java\
Consumer.java\
Producer.java\
BoundedBufferMain.java\

SOURCES=\
$(JAVASOURCES)

CLASSES=$(SOURCES:.java=.class)

%.java: %.p.java
	awk -f presources.awk -v TEACHER=$(TEACHER) $< >$@

%.class: %.java
	javac $(JAVACFLAGS) $<

default : compile

compile: $(CLASSES)

student:
	@make veryclean
	@make $(JAVASOURCES)

teacher:
	@make veryclean
	@make PREPFLAGS="$(PREPFLAGS) -DTEACHER=true" $(JAVASOURCES)

index.html: index.texi
	makeinfo \
		--no-headers --html --ifinfo --no-split \
		--css-include=style.css $< > $@

error :
	$(error "PREFIX variable not set")

install : veryclean index.html
	@if test -z "$(PREFIX)"; then \
	   make error; \
	fi
	@make student
	-mkdir -p $(PREFIX)
	chmod og=u-w $(PREFIX)
	tar cf src.tar $(SOURCES) Makefile
	gzip -f src.tar
	chmod og=u-w style.css index.html src.tar.gz
	cp -p style.css index.html src.tar.gz $(PREFIX)

clean:
	@-rm -f *~ *.class

veryclean: clean
	@-rm -f $(SOURCES)
