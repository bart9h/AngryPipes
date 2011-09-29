TARGET=bin/AngryPipes-debug.apk

SRCS=\
	AndroidManifest.xml \
	src/org/ninehells/angrypipes/AngryPipes.java \
	src/org/ninehells/angrypipes/Board.java \
	src/org/ninehells/angrypipes/Config.java \
	src/org/ninehells/angrypipes/Game.java \
	src/org/ninehells/angrypipes/View.java \

$(TARGET): $(SRCS)
	ant debug

clean:
	rm -rf bin/ gen/

install: install-emulator

install-emulator: $(TARGET)
	adb -e uninstall org.ninehells.angrypipes
	adb -e install $(TARGET)

install-device: $(TARGET)
	adb -d uninstall org.ninehells.angrypipes
	adb -d install $(TARGET)
