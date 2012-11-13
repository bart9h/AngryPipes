TARGET=bin/AngryPipes-debug.apk

$(TARGET): build.xml clean
	ant debug

release: build.xml clean
	ant release
	jarsigner -verbose -keystore my-release-key.keystore bin/AngryPipes-unsigned.apk bart9h
	zipalign -v 4 bin/AngryPipes-unsigned.apk AngryPipes.apk

build.xml:
	android update project -p "$$PWD" -t android-16

clean:
	rm -rf bin/ gen/

install: install-emulator

install-emulator: $(TARGET)
	adb -e install -r $(TARGET)

install-device: $(TARGET)
	adb -d install -r $(TARGET)
