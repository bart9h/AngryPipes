TARGET=bin/AngryPipes-debug.apk

$(TARGET): clean
	ant debug

release: clean
	ant release
	jarsigner -verbose -keystore my-release-key.keystore bin/AngryPipes-release-unsigned.apk bart9h
	zipalign -v 4 bin/AngryPipes-release-unsigned.apk AngryPipes.apk

clean:
	rm -rf bin/ gen/

install: install-emulator

install-emulator: $(TARGET)
	adb -e install -r $(TARGET)

install-device: $(TARGET)
	adb -d install -r $(TARGET)
