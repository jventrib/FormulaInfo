./avdmanager create avd --force -n test2 --abi 'default/x86_64' --package 'system-images;android-31;default;x86_64' --device 'pixel_4'

emulator -avd test -no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none &