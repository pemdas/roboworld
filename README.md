# RoboWorld

## What is this?
RoboWorld is a tool intended for use in introductory programming courses taught in Java.  It provides a way to give students programming exercises that let them practice basic skill such as coditionals, loops, and method calls.

It's inspired by [Karel the Robot](https://compedu.stanford.edu/karel-reader/docs/python/en/chapter1.html) and related packages, particularly the Java adaptation by [Byron Weber Becker](https://uwaterloo.ca/computer-science/contacts/byron-weber-becker).

## Project Goals
1. **Minimal environment setup:** this package is intended for use by students that are just beginning their computer science journey, and many student won't have significant computer expertise yet.  Getting the environment set up should involve nothing more than adding the jar file to the classpath, and the boilerplate needed to initialize and start a scenario should be as minimal as possible.  This means sometimes we do complicated things with threading deep in the library to preseve the simplicity of the student experience.

1. **Minimal student boilerplate:** Students should be able to start working on a with just a line or two of boilerplate in their `main` method.  Furthermore, the boilerplate should not
be "weird"--it should look reasonable comprehensible to a student with minimal experience.

1. **Simple student-facing API:** students interact with the `Robot` interface for challenges--this API should be minimal, well-documented, and straightfoward.  THe goal of this package is not to gice 

1. **Maximum platform compatibility:** the same setup flow should work on Linux, Windows, and MacOS.  The library should also work with older versions of Java, since many schools don't update their JRE's often.  Right now the package targets version 11.  If there are compelling reasons to move the required JRE version forward, we will do so, but we want to be mindful that, especially at smaller programs, sometimes the installed version on school computers can be very old.

1. **Scenario flexibility:** The library should allow for a wide variety of programming-related challenges to be expressed.

## Non-Goals

1.  The focus here is on basic imperative programming skills, emphasizing program flow.  The focus is *not* OOP skills (inheritance, encapsulation, interfaces, generic programming, etc).  

## Documentation Links

* [Building from Scratch](docs/BUILDING.md)
* [Threading Model](docs/THREADING.md)


## Contributing

Pull requests are welcome.  As with any open source project, you're strongly encouraged to discuss features and ideas before generating pull requests; the right way to do that here is via the GitHub issue tracker.  There is not at present an official Discord or similar.  You're also welcome to email me at [foozle+roboworld@gmail.com](mailto:foozle+roboworld@gmail.com)

If you want to reuse code from another open source project, please clearly identify the source of the code and be sure to adhere to the license of the original code.

## Versioning
As of this writing the version is 0.1, meaning the library is still in the "initial hacking" state.  If the current state works for your purposes, please do feel free to use it!  However, the architecture may shift radically as I continue to develop it.  

After things stablize a bit and 1.0 is reached, then the intended convention moving forward is that minor releases (.x) will be backwards compatible (e.g. student code compiled against a newer minor release will still work) whereas major releases may involve breaking the student facing API.  

