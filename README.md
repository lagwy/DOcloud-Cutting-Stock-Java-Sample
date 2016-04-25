# IBM Decision Optimization on Cloud Cutting Stock Java Sample

IBM Decision Optimization on Cloud (DOcplexcloud) allows you to solve optimization
problems on the cloud without installing or configuring a solver. 

This example illustrates the use of the DOcplexcloud API to iteratively solve a 
multi-model, a series of linked optimization problems. In the example, large 
bulk rolls of sheet metal of a given width are cut into rolls of different 
widths to meet customer demands while minimizing the number of bulk rolls that
need to be sliced.

The problem details are described [later in the Optimization Problem section](#optimization-problem).

## Sample contents

The sample consists of a Java project which contains:

* Java files: `src/main/java/com/ibm/optim/oaas/sample/cuttingStock`
* The optimization (OPL .mod, .dat and project) files: `src/main/resources/com/ibm/optim/oaas/sample/cuttingStock/opl` 
* Maven build file: `pom.xml`
* Eclipse project file: `.project`

## Installation

### Prerequisites

1. Install [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).  
   Maven will check the version based on either your path or your `JAVA_HOME` environment 
variable. If Maven cannot find the correct version, it will notify you with an error 
message. The version of Java from your path can be checked using this command:

   ```
java -version
```

2. Install [Maven](https://Maven.apache.org/download.cgi).  
   Once installed, you can check that it is accessible using this command:

   ```
mvn --version
```

3. Once you have registered and logged in to DOcplexcloud, get the IBM DOcplexcloud 
base URL and an API key, which are accessible on the 
[Get API Key page](https://dropsolve-oaas.docloud.ibmcloud.com/dropsolve/api). 
Copy the base URL and the API key to the maven properties in your `~/.m2/settings.xml` 
settings file, where
  * `yourKey` is the API key (clientID) that you generate after registering for DOcplexcloud.
  * `yourURL` is the base URL that you get after registering for DOcplexcloud.

   ```xml
  <profile>
    <id>docloud</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <docloud.baseurl>yourURL</docloud.baseurl>
      <docloud.apikey.clientid>yourKey</docloud.apikey.clientid>
    </properties>
  </profile>
```

4. Download and install the IBM DOcplexcloud API for Java client libraries.  
   You can download the library from the [developer center](https://developer.ibm.com/docloud/docs/java-client-api/java-client-library/).  
Extract the jar file starting with `docloud_api_java_client` from the downloaded zip file (ignore the javadoc jar file).
Then add this jar file to your local Maven repository like this:

   ```
mvn install:install-file -Dfile=<path-to-file> -DgroupId=com.ibm.optim.oaas  -DartifactId=api_java_client -Dversion=1.0-R1-SNAPSHOT -Dpackaging=jar
```

### Build with Maven

* From the sample directory, compile with Maven:

   ```
mvn install
```

### Run the sample

* From the sample directory, execute with Maven:

   ```
mvn exec:java
```

## Dependencies
    
### Server side runtime dependencies loaded by Maven (see the pom.xml file)

* com.ibm.optim.org.slf4j:slf4j-jdk14
* com.ibm.optim.oaas:api_java_client
* com.ibm.icu:icu4j
* org.apache.httpcomponents:httpclient
* com.fasterxml.jackson.core:jackson-databind

## Optimization Problem

This example illustrates how to use the DOcplexcloud API to iteratively solve a multi-model, 
a series of linked optimization problems. The cutting stock problem is a classic example 
of the column-generation algorithm for solving large, structured optimization models. 
This example is adapted from the IBM CPLEX Optimization Studio sample library, 
where it appears in several forms:

* [In OPLScript](http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.3/ilog.odms.ide.help/OPL_Studio/opllanguser/topics/opl_languser_script_fc_column_cutstock.html)
* [In Java](http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.3/ilog.odms.ide.help/examples/html/opl_interfaces/java/cutstock/src/cutstock/Cutstock.java.html)

The problem was originally formulated by Gilmore and Gomory in a pair of papers in Operations Research in 1961 and 1963.

### Business Context

There are numerous real-world examples of cutting stock problems. Here is one:

A steel fabrication business needs to fill orders for rolls of sheet metal of different 
widths (say 20", 25", 35",…) by slicing them from larger bulk rolls of a given width 
(say 110"). Similar situations occur in supplying paper, carpet, and many other materials. 
The firm fabricates the material to fill the orders by setting up the cutter machine 
in a pattern that comprises several widths of slices, as long as the total width of 
the pattern does not exceed the bulk roll width. Typically, a pattern will not fully 
utilize the width of the bulk roll, leaving some of it as scrap. The business objective 
is to minimize the amount of scrap, or equivalently, to minimize the number of bulk rolls 
that need to be sliced in order to fill the orders.

Such an optimization application would often be embedded in an operational planning 
system that would track incoming material orders, assign them to task orders, promise 
delivery dates to customers, track completion and delivery, and exchange data with the 
cost accounting and billing systems.

### How the Optimization Works

The cutting stock problem is solved using a pair of optimization models called the Master 
problem and the Subproblem. The Master problem takes as input the demand for items (the 
number of rolls of various widths) that have been ordered and a set of slicing patterns, 
and it determines how many bulk rolls to cut with each pattern in order to satisfy the 
demand for each item, minimizing the number of bulk rolls used. Associated with the demand 
for each item, the Master problem also computes a dual price, which is the incremental 
cost, in terms of bulk rolls used, to cut an additional item of that width. Since the 
Master problem includes only a subset of all the possible slicing patterns (of which 
there can be a huge number), it is natural to ask whether there might be other slicing 
patterns that, if used, could further reduce the number of bulk rolls needed. That 
question is addressed in the Subproblem.

The Subproblem takes as input the set of item widths that have been ordered, the dual 
prices of the items from the Master problem, and the bulk roll width, and it determines 
a new slicing pattern. The objective is to minimize the reduced cost of the pattern, 
which is essentially the incremental value, in terms of bulk rolls used, if a new pattern 
were introduced into the Master problem. As long as the reduced cost in the Subproblem 
is negative, the new pattern should be included in the Master problem. On the other 
hand, if the minimum reduced cost in the Subproblem is positive, there is no other 
pattern that could improve the cutting plan produced by the Master problem.

The column-generation algorithm proceeds iteratively by solving the Master problem with 
the current set of patterns, computing the dual prices of the items, solving the 
Subproblem with these dual prices to calculate a new pattern, testing the reduced cost 
computed by the Subproblem, and if it is negative, adding the new pattern to the Master 
problem and resolving it. The iteration continues until the Subproblem reduced cost 
becomes positive.

An important technical point: The Master problem is generally solved as a linear program, 
meaning that the number of times each pattern is used is not necessarily a whole number. 
Thus, it is an approximation, although that caveat is often considered acceptable for 
business use, especially when the number of rolls used is very large. The Subproblem, 
on the other hand, is a discrete optimization problem in which the number of slices in 
each pattern is held to be an integer.

### How DOcplexcloud Solves the Cutting Stock Problem

The OPL models cited in the samples referenced above have been modified slightly in order 
to conform with the DOcplexcloud requirement that data exchange take place only through tuples 
and tuple sets in which the fields are primitive data types (strings, integers, floating 
point numbers, etc.) In particular, a pattern is represented by a Pattern tuple that 
provides an ID and a cost and by a set of Slice tuples, one for each item, that specifies 
the pattern ID, the item ID, and the number of slices of the item in the pattern.

```
tuple Pattern {
   key int id;
   int cost;
}
{Pattern} patterns = ...;

tuple Slice {
	key string item;
	key int pattern;
	int number; // Number of slices of this item in this pattern
}
{Slice} slices= ...;
```

In the code of the example, there is a Java class that represents each OPL tuple:
*	`Dual`
*	`Item`
*	`Objective`
*	`Parameters`
*	`Pattern`
*	`Slice`
*	`Usage`

These classes constitute the Domain Object Model (DOM) for the Cutting Stock problem. In 
this example, each of these classes was hand coded using a common template. A set of 
private fields represents the tuple's fields, e.g. item, 
pattern, and number of the `Slice` tuple. There are several constructors, including a 
no-argument constructor that is used by the JSON deserialization and a copy constructor 
that is used to make deep copies of the data objects for tracking the progress of the 
algorithm. There is a getter and a setter method for each field that are used by the 
JSON serialization and deserialization. The `equals`, `hashCode`, and `toString` methods are 
overridden. Each DOM class also has a static inner class that represents the corresponding 
tuple set as a Java `List`.

The DOM also includes collector classes that comprise the inputs and outputs of the master 
problem and subproblem. These classes are used by the DOcplexcloud API to serialize the input 
and deserialize the output as part of the solve job request.

The structure of the code strongly mirrors the algorithm described above. In particular, 
the algorithm appears in the `optimize` method of the `ColumnGeneration` class. This method 
creates two instances of the `Optimizer` class, one for the Master problem and the other 
for the Subproblem, which actually handle the DOcplexcloud job requests. Note that the `Optimizer` 
class takes a generic parameter representing the solution class of the optimization model; 
thus the same code can be used for different optimization models. The solve method of the 
`Optimizer` class creates and executes the job request and retrieves the output, handling 
the JSON serialization and deserialization of the data objects.

## License

This sample is delivered under the Apache License Version 2.0, January 2004 (see `LICENSE.txt`).
