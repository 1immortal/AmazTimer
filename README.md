# AmazTimer

## Description
This app is an interval timer made from scratch for amazfit devices to do interval trainings with HR monitoring, default values are tabata training's but you can use any times and sets you want

## Bugs
This app is in development so you may find bugs, if you have any please create an issue with logcat and I'll try to fix it as fast as I can. Don't report bugs that are already reported or listed in release's description

## Compatible devices
I have just tested it in Amazfit Pace with Hybrid ROM by Saratoga using stratos 3's apps but it should work in all amazfit devices running their modified android

## Installation
`adb install app-release.apk && adb shell am force-stop com.huami.watch.launcher`

## Uninstallation
`adb uninstall me.micrusa.amaztimer`

## Screenshots
<img src="https://github.com/micrusa/AmazTimer/raw/master/screen1.png"/><img src="https://github.com/micrusa/AmazTimer/raw/master/screen2.png"/><img src="https://github.com/micrusa/AmazTimer/raw/master/screen3.png"/><img src="https://github.com/micrusa/AmazTimer/raw/master/screen4.png"/>

## Thanks to
- [@KieronQuinn](https://github.com/KieronQuinn) for [Springboard Plugin Example](https://github.com/KieronQuinn/AmazfitSpringboardPluginExample)
- [@GreatApo](https://github.com/GreatApo) for [Amazfit Calendar Widget](https://github.com/GreatApo/AmazfitPaceCalendarWidget)

## How to move or disable widget
- If you have AmazMod, you can do it through mobile app or watch app
- If you don't have AmazMod, you can use [@KieronQuinn](https://github.com/KieronQuinn)'s [springboard settings app](https://github.com/KieronQuinn/AmazfitSpringboardSettings)