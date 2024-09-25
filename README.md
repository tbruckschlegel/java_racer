# JAVA based 3D Racing Game (1/4 mile racing) - Windows & Linux (2004)
This game is a work in progress contractor job in 2004 for German !["Speed Days"](https://www.takt-magazin.de/magazin/speeddays_211078) - but never got released.
It still runs fine at 4K with 120 Hz ;)

Technologies: 
  * JME Engine 0.8
  * OdeJava & ODE Physics
  * LWJGL 1.x

Key Bindings: 

* A - shift up
* D - shift down
* Left Shift - clutch

Arrows - gas, brake, left, right

P.S. You have to use the clutch to change gears!!!


![ScreenShot](https://raw.github.com/tbruckschlegel//java_racer/main/shot2.png)

![ScreenShot](https://raw.github.com/tbruckschlegel//java_racer/main/shot1.png)

Gameplay video
https://youtu.be/ylgMphfeElM





How to run it:

You have to use a 32 bit environment (I could not make Ubuntu 22.xx to run with 32 bit HW accelration on NVIDIA GPUs, so I had to use Windows) becauce I can't find ODEJava JNI part to recompile it in x64-

 * best to use IntelliJ IDEA Community Edition 2024 to run it
 * install Java 8 SDK (due to old classes!) e.g. https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html

Command Line:

```"C:\Program Files (x86)\Java\jre-1.8\bin\java.exe" -Djava.library.path=C:\DEV\Java\java_racer\src\jmephysics\lib;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\native\windows "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.2.2\lib\idea_rt.jar=50992:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.2.2\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files (x86)\Java\jre-1.8\lib\charsets.jar;C:\Program Files (x86)\Java\jre-1.8\lib\deploy.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\access-bridge-32.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\cldrdata.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\dnsns.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\jaccess.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\jfxrt.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\localedata.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\nashorn.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\sunec.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\sunjce_provider.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\sunmscapi.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\sunpkcs11.jar;C:\Program Files (x86)\Java\jre-1.8\lib\ext\zipfs.jar;C:\Program Files (x86)\Java\jre-1.8\lib\javaws.jar;C:\Program Files (x86)\Java\jre-1.8\lib\jce.jar;C:\Program Files (x86)\Java\jre-1.8\lib\jfr.jar;C:\Program Files (x86)\Java\jre-1.8\lib\jfxswt.jar;C:\Program Files (x86)\Java\jre-1.8\lib\jsse.jar;C:\Program Files (x86)\Java\jre-1.8\lib\management-agent.jar;C:\Program Files (x86)\Java\jre-1.8\lib\plugin.jar;C:\Program Files (x86)\Java\jre-1.8\lib\resources.jar;C:\Program Files (x86)\Java\jre-1.8\lib\rt.jar;C:\DEV\Java\java_racer\out\production\race_game;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lzma.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lwjgl.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\jinput.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lwjgl_test.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lwjgl_util.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lwjgl-debug.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\asm-debug-all.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\trident-7.2.1.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\substance-7.2.1.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\laf-plugin-7.2.1.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\laf-widget-7.2.1.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\lwjgl_util_applet.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\AppleJavaExtensions.jar;C:\DEV\Java\java_racer\src\lwjgl-2.9.3\jar\log4j-api-2.0-beta9.jar;C:\DEV\Java\java_racer\src\jmephysics\lib\junit.jar" Racer```
