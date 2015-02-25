KicksEmu
========
[![Travis CI Build Status](https://travis-ci.org/neikeq/KicksEmu.svg?branch=master)](https://travis-ci.org/neikeq/KicksEmu)

KicksEmu is an open-source emulator for the Kicks Online MMO game.<br>
It uses Netty framework for networking and Apache DBCP for MySql connection pooling.

### Dependencies

* netty-4
* commons-dbcp2
* commons-logging
* commons-pool2
* opencsv-3.1

### Building

Requires **JDK 8**.

##### Ant

Build sources:

```
ant build.modules
```

Build jar artifacts:

```
ant build.all.artifacts
```

##### Intellij IDEA

* Project file: `KicksEmu.iml`.<br>
* The project was created using version 14.0 of the IDE.<br>
* Make sure the `Project language level` in `Project Structure > Project Settings > Project` is set to 8 or higher.

### Running

[Setup guide.](https://github.com/neikeq/KicksEmu/wiki/Setup)

Execute `run.sh` on unix and `run.bat` on windows.

If you wish to use a different configuration file, you can pass its name as first argument when running the emulator. If this argument is omitted, the default configuration will be used.

You can find a short description about the repository directories
[here](https://github.com/neikeq/KicksEmu/wiki/Directory-Tree).

[Visit the wiki](https://github.com/neikeq/KicksEmu/wiki) for more information

### License
Published under the [GNU GPL v3.0](https://github.com/neikeq/KicksEmu/blob/master/LICENSE) license.
