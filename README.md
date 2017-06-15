This repository contains the solution for the lab part of the course "Distributed Programming with Space Based Computing Middleware"  (University of Technology Vienna, summerterm 2017).

## Documentation
In the "documentation" directory the assignment descriptions can be found. Addionally, it contains a BPMN model and slides for the final presentation.

The BPMN model has been created with the website [bpnm.io](https://bpmn.io/)

## Startup
Before you start make sure to initiate a maven clean install either with your IDE or via comandline with the command:
'cd code
mvn clean install
'

2. In the directory "scripts" are startup scripts for both the XVSM implementation and the JMS implementation.
3. Start up the bakery with Bakery.bat in the scripts directory. 
The bakery must be fully initialized before actors can be started!
4. Start up the robots with the scripts or do it yourself with: 
mvn -f code/robotbakery.xvsm/pom.xml exec:java -P[knead|bake|service] uuid
The uuid is for the robot id and is an optional parameter.
5. Start up a customer and start ordering with the tablet.
6. Enjoy our baked goods! :)
