@echo off
set dev=C:/Users/tp23836/dev
set jbullet=%dev%/jbullet-20101010
set ecj=%dev%/ecj
set lib=%dev%/lib
set bin=%dev%/jbullet-ecj/bin

set classpath=%bin%
set classpath=%classpath%;%jbullet%/lib/lwjgl/jinput.jar
set classpath=%classpath%;%jbullet%/lib/lwjgl/lwjgl_util.jar
set classpath=%classpath%;%jbullet%/dist/jbullet.jar
set classpath=%classpath%;%jbullet%/dist/jbullet-demos.jar
set classpath=%classpath%;%jbullet%/lib/vecmath/vecmath.jar
set classpath=%classpath%;%ecj%
set classpath=%classpath%;%lib%/guava-14.0.1.jar
set classpath=%classpath%;%lib%/itext-1.2.jar
set classpath=%classpath%;%lib%/jcommon-1.0.0.jar
set classpath=%classpath%;%lib%/jfreechart-1.0.1.jar
set classpath=%classpath%;%lib%/jzlib-1.0.7.jar


rem echo %classpath%

java -ea -Djava.library.path=C:/Users/tp23836/dev/jbullet-20101010/lib/lwjgl/win32 %1 %2 %3 %4 %5



