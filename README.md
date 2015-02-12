KicksEmu
========

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

Execute `run.sh` on unix and `run.bat` on windows.

If you wish to use a different configuration file, you can pass its name as first argument when running the emulator. If this argument is omitted, the default configuration will be used.

The execution directory must contain the following subdirectories:
- [<root directory>/data/config](https://github.com/neikeq/KicksEmu/tree/master/data/config)
  - The default configuration files are stored here.
  - If a configuration file cannot be located/loaded, the application will initialize its configuration with the default variables.
  - The application can initialize with a different configuration file if the first argument of the application represents a valid configuration file name.
- [<root directory>/data/lang](https://github.com/neikeq/KicksEmu/tree/master/data/lang)
  - This folder contains all the translation files.
  - The name of the files must follow this format: `lang_<acronym>.properties`, where the acronym to be loaded is decided by value of `lang` in the configuration file.
- [<root directory>/data/table](https://github.com/neikeq/KicksEmu/tree/master/data/table)
  - Contains all the game table files required that may be required for different operations.
- <root directory>/logs
  - This is the directory where all the logs are written to.
  - If logging is enabled, and this folder does not exist, the application will try to create it automatically. If it fails to create the folder, logging will be disabled.

### License
Published under the [GNU GPL v3.0](https://github.com/neikeq/KicksEmu/blob/master/LICENSE) license.
