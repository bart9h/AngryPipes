TARGET=bin/AngryPipes-debug.apk

$(TARGET): clean
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
