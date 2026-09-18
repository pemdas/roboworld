# RoboWorld

## What is this?
RoboWorld is a tool intended for use in introductory programming courses taught in Java.  It provides a way to give students programming exercises that let them practice basic skill such as coditionals, loops, and method calls.

It's inspired by [Karel the Robot](https://compedu.stanford.edu/karel-reader/docs/python/en/chapter1.html) and related packages, particularly the Java adaptation by [Byron Weber Becker](https://uwaterloo.ca/computer-science/contacts/byron-weber-becker).

## Project Goals
1. **Minimal setup:** this package is intended for use by students that are just beginning their computer science journey, and many student won't have significant computer expertise yet.  Using the library should involve nothing more than adding the jar file to the classpath, and the boilerplate needed to initialize and start a scenario should be as minimal as possible.  This means sometimes we do complicated things with threading deep in the library to preseve the simplicity of the student experience.

2. **Maximum platform compatibility:** the same setup flow should work on Linux, Windows, and MacOS.  The library should also work with older versions of Java, since many schools don't update their JRE's often.

3. **Scenario flexibility:** The library should allow for a wide variety of programming-related challenges to be expressed.

## Contributing

Pull requests are welcome.  As with any open source project, you're strongly encouraged to discuss features and ideas before generating pull requests; the right way to do that here is via the GitHub issue tracker.  There is not at present an official Discord or similar.  You're also welcome to email me at [foozle+roboworld@gmail.com](mailto:foozle+roboworld@gmail.com)

New code should ideally include unit test coverage.

If you want to reuse code from another open source project, please clearly identify the source of the code and be sure to adhere to the license of the original code.

<ul>
#### Artificial Intelligence Policy

All submissions must be from a human.  It's permissible to use AI to help you code a contribution, but ultimately a human must be responsible for ensuring that the resulting code is correct and comprehensible.

AI-generated assets (sprites, sounds, etc) are not accepted in this project.
</ul>

## Versioning
As of this writing the version is 0.1, meaning the library is still in the "initial hacking" state.  If the current state works for your purposes, please do feel free to use it!  However, the architecture may shift radically as I continue to develop it.  

After things stablize a bit and 1.0 is reached, then the intended convention moving forward is that minor releases (.x) will be backwards compatible (e.g. student code compiled against a newer minor release will still work) whereas major releases may involve breaking the student facing API.  

