TARGET=bin/AngryPipes-debug.apk

SRCS=\
	src/org/ninehells/angrypipes/AngryPipes.java \
	src/org/ninehells/angrypipes/View.java \

$(TARGET): $(SRCS)
	ant debug

install: $(TARGET)
	adb uninstall org.ninehells.angrypipes
	adb install $(TARGET)
