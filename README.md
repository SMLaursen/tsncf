#Time Sensitive Network Configuration Framework
The TSN configuration framework is used to retrieve and optimize the VLAN routings used for configuring a TSN network topology.

## Dependencies ###
Being based on Java TSNCF should run on all major platforms. Following external dependencies are needed and have been verified:
* [Apache Commons CLI 1.3.1](http://commons.apache.org/proper/commons-cli/) for Command Line Parsing
* [JGraphT 0.9.1](http://jgrapht.org/) jgrapht-core for the internal data-structures and algorithms
* JGraphx 2.0.0.1 and JGraph-ext 0.9.1 (Included in the [JGraphT](http://jgrapht.org/) download) for visualization (not mandatory)
* [JUnit 4.12](http://junit.org/) and [Hamcrest 2.0.0.0](http://hamcrest.org/JavaHamcrest/) For unit-testing (not mandatory)
* [SLF4J 1.7.13](http://www.slf4j.org/) and [LogBack](http://logback.qos.ch/) for logging (not mandatory)

## Getting Started ##
The folder `/resource` includes some example files. The solution for these can be displayed using the `-display` command where the `-net` argument refers to architecture **GraphML** files and `-app` argument for **XML** application files. 
	
	$ Java -jar TSNCF -net <file> -app <file> -display -verbose

## License ##
TSNCF is released under the [LGPLv3 license](http://www.gnu.org/licenses/lgpl-3.0.html).

## Authors ##
-[Sune Mølgaard Laursen](http://smlaursen.github.io/)

