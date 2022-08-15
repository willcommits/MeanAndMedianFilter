JAVAC=/usr/bin/javac
.SUFFEXES: /java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=MeanFilterSerial.class MedianFilterSerial.class MeanFilterParallel.class MedianFilterParallel.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/*.class
runMeanSerial:	$(CLASS_FILES)
	java -cp bin MeanFilterSerial ./image2.jpg ./MeanSerial.jpg 3
runMeanParallel: $(CLASS_FILES)
	java -cp bin MeanFilterParallel ./image2.jpg ./MeanParallel.jpg 33
runMedianSerial:	 $(CLASS_FILES)
	java -cp bin MedianFilterSerial  ./image2.jpg ./MedianSerial.jpg 3
runMedianParallel:	$(CLASS_FILES)
	java -cp bin MedianFilterParallel ./image2.jpg ./MedianParallel.jpg 3

