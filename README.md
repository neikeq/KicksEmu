KicksEmu
========

KicksEmu is an open-source emulator for the Kicks Online MMO game.<br>
It uses Netty framework for networking and Apache DBCP for MySql connection pooling.

### Dependencies

* netty-4
* commons-dbcp2
* commons-logging
* commons-pool2

### Usage

Requires **JDK 8**.

##### Ant

Compile:

```
ant build.modules
```

Build jars:

```
ant build.all.artifacts
```

##### Intellij IDEA

* Project file: `KicksEmu.iml`.<br>
* The project was tested with version 14.0 of the IDE.<br>
* Make sure the `Project language level` in `Project Structure > Project Settings > Project` is set to 8 or higher.

### Running

The first argument may point to an alternative configuration file. Otherwise, the default one will be used.

The execution directory may contain the following subfolders:
- data
  - Where the default configuration file (`config.properies`) is stored.
  - If this file cannot be found, the application will initialize its configuration with the default variables.
  - The application can initialize with a different config file if the first argument of the application represents a valid path.
- lang
  - This folder contains the translation files.
  - The name must follow this format: `lang_<acronym>.properties`, where which acronym to be loaded is decided by value of `lang` in the configuration file.
- logs
  - This is the directory where the application will write all the logs.
  - The application will create it automatically if logging is enabled.
  - If the application fails to create the folder, logging will be disabled.

### License
Published under the [GNU GPL v3.0](https://github.com/neikeq/KicksEmu/blob/master/LICENSE) license.
