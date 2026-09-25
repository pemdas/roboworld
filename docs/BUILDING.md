# Building the Package

## Required software

* A JDK (at least version 11)
* [Apache Maven](https://maven.apache.org)
* [git](https://git-scm.com/)

If you want to alter or add art assets, the process of converting SVGs to PNGs additionally requires

* [inkscape](www.inkscape.org)
* [make](https://www.gnu.org/software/make/)

## Getting the code

```
git clone https://github.com/pemdas/roboworld.git
```

## Building the package

From the `roboworld` directory:

```
mvn package
```

If successful, you should end up with a jar file named `roboworld-`*version*`.jar`, e.g. `roboworld-0.1.jar`

## Running tests

(Note that tests are automatically run when the package is built)

```
mvn test
```

## Running the example scenario


TODO