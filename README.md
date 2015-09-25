Quick usage
-----------
Requires Oracle Java 8 Runtime.

The program can be run by opening the appropriate executable located within the 'program' directory.

**Mac OS X:**   Kiqo.app
**Windows:**    Kiqo.exe (You may need to allow the program to run depending on your security settings)
**Linux:**     Kiqo.sh (Please preserve file structure, as moving the jar will cause this script to fail)

The program can also be executed by opening the .jar file included.
Alternatively the .jar file can be executed from the command line using the following command:

	java -jar Kiqo-6.0.jar


Building from Source
--------------------
To build from source, you need to have maven installed along with Oracle Java SDK 8.
(Preferably the latest version, but older versions of maven may work.)

From the source directory run the command:

	mvn package

This will produce an executable jar in the ./target directory which can be run
using the same commands as the JAR above. The Mac OS X, Windows and Linux executables
will be placed in ./target/deploy.

Auxiliary Resources
---------------
To produce the auxiliary resources, run the command:

	mvn site

Javadoc for application code is in `/target/site/apidocs`
Javadoc for test code is in `/target/site/apidocs`
Unit test reports are in `/target/surefire-reports`

These reports and others can be viewed on the Maven-generated "website" based at `/target/site/index.html`

Testing
-------
**Note:** The unit tests for report generator require internet access because they validate the produced yaml using a web-based validator.
In most environments these tests will fail immediately when no internet connection is present.
On the University of Canterbury lab machines, the tests will hang because the program detects and internet connections and waits for a reply.
The solution is to enable internet using internet enabler.
