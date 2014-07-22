Steam Condenser
===============

[![Build Status](https://secure.travis-ci.org/koraktor/steam-condenser-java.png)](http://travis-ci.org/koraktor/steam-condenser-java)

The Steam Condenser is a multi-language library for querying the Steam
Community, Source and GoldSrc game servers as well as the Steam master servers.
Currently it is implemented in Java, PHP and Ruby.

## Requirements

* Linux, MacOS X or Windows
* Java 1.5 or newer

The following Java libraries are required:

* Apache Commons Compress (for Source servers sending compressed responses)
* Apache Commons Lang 3
* Apache Commons HttpClient (for the Web API features)
* JSON (for the Web API features)
* JUnit (for testing)
* PowerMock (for testing)

Maven will install these for you.

## Installation

To install and use Steam Condenser in your Maven managed project use the
following dependency definition:

    <dependency>
        <groupId>com.github.koraktor</groupId>
      	<artifactId>steam-condenser</artifactId>
      	<version>x.y.z</version>
    </dependency>
    
Remember to specify a version using appropriate tag.

## Logging

Steam Condenser provides logging based on [SLF4J][slf4j]. To make use of it you
have to add a logger implementation (like slf4j-log4j) to your application's
classpath. See [this list][loggers] for some available SLF4J loggers.

## License

This code is free software; you can redistribute it and/or modify it under the
terms of the new BSD License. A copy of this license can be found in the
included LICENSE file.

## Credits

* Sebastian Staudt – koraktor(at)gmail.com
* David Wursteisen – david.wursteisen(at)gmail.com
* Guto Maia – guto(at)guto.net
* Sam Kinard – snkinard(at)gmail.com

## See Also

* [Steam Condenser home](http://koraktor.de/steam-condenser)
* [GitHub project page](https://github.com/koraktor/steam-condenser)
* [Wiki](https://github.com/koraktor/steam-condenser/wiki)
* [Google group](http://groups.google.com/group/steam-condenser)
* [Ohloh profile](http://www.ohloh.net/projects/steam-condenser)

Follow Steam Condenser on Google Plus+ via
[+Steam Condenser](https://plus.google.com/b/109400543549250623875/109400543549250623875)
or on Twitter via [@steamcondenser](https://twitter.com/steamcondenser).

 [loggers]: http://www.slf4j.org/manual.html#swapping
 [slf4j]:   http://www.slf4j.org
