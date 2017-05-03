This repository contains the solution for the lab part of the course "Distributed Programming with Space Based Computing Middleware"  (University of Technology Vienna, summerterm 2017).

## Documentation
The bpnm model has been created with the website [bpnm.io](https://bpmn.io/)

## Startup
1. Make a mvn clean install in the code directory
2. There are 2 directories in scripts. One is for the SBC approch and the other one is for JMS.
3. Start up the bakery with Bakery.bat in the scripts directory.
4. Start up the robots with the scripts or do it yourself with: 
-f ../../code/robotbakery.xvsm/pom.xml exec:java -P[knead|bake|service] uuid
The uuid is for the robot id and is an optional parameter.
5. Start up a customer and start ordering with the tablet.
6. Enjoy our baked goods! :)
