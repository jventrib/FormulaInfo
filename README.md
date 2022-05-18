[![Build](https://github.com/jventrib/FormulaInfo/actions/workflows/build.yml/badge.svg)](https://github.com/jventrib/FormulaInfo/actions/workflows/build.yml)

# Formula Info
![Formula Info](https://play-lh.googleusercontent.com/_uCswjBOWgxz8h_VExW2biO0IHNrQZrIf4dftGtEPYpFy49sxWYU-gMjRp5hftEPMp0=w720-h310-rw)
![Formula Info](https://play-lh.googleusercontent.com/wzCfTrWAJNzuTHj32F712KNHwknyFkOiMN5NReeJwG4ga5fok8fYbO8AEWwgHzGM9pau=w720-h310-rw)
![Formula Info](https://play-lh.googleusercontent.com/EJx3Nk8m8Ocl2eg9odEwr4MT8ggAOrCJGZP2hGrPceYqgUbEsj1O1UgASYAmwd7gTAUU=w720-h310-rw)
![Formula Info](https://play-lh.googleusercontent.com/Z9xaQ7qj8y0WcD47466-IGNs_uaSlBouZkrzmLMHaUcyM76rECeWQ3tfgZfoMcIA9Q=w720-h310-rw)

This app is a personal project to improve my personal skills in Android application development. By now, there is no dedicated backend, and no static content inside the app. All the content is coming from various public APIs

### Play Store
https://play.google.com/store/apps/details?id=com.jventrib.formulainfo

### Disclaimer
This app is unofficial and is not associated in any way with the Formula One group or companies. F1, FORMULA ONE, FORMULA 1, FIA FORMULA ONE CHAMPIONSHIP, GRAND PRIX and related marks are trade marks of Formula One Licensing B.V.
### Datasources
#### Ergast MRD API
Most historical infos are coming from this great API
#### Wikipedia
Wikipedia is serving most images used in the app (flag, circuit, drivers)
#### F1Calendar API
F1Calendar API is serving the sessions information for the ongoing season

# Screenshot tests #

This project use  [Shot](https://github.com/pedrovgs/Shot) to do screenshot tests.

Reference screenshots are generated using emulator with Pixel4 device, API-31 and swiftshader_indirect gpu mode.

The AVD can be created with:
```
./avdmanager create avd --force -n test2 --abi 'default/x86_64' --package 'system-images;android-31;default;x86_64' --device 'pixel_4'
```

The emulator can be launched by command line:
```
emulator -avd test -no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none
```

To use the emulator from the AVD manager, the device must be configured with:
```hw.gpu.mode = swiftshader_indirect``` in AVD [config.ini](https://github.com/jventrib/FormulaInfo/blob/2b320a6594290289bf742c0b71a50812d2ec97bc/publish/config.ini#L66)

