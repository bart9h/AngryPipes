TARGET=bin/AngryPipes-debug.apk

$(TARGET): clean
	ant debug

clean:
	rm -rf bin/ gen/

install: install-emulator

install-emulator: $(TARGET)
	adb -e install -r $(TARGET)

install-device: $(TARGET)
	adb -d install -r $(TARGET)
