
# binMeta project

all: basic objectives methods

basic: Data.class Memory.class
	echo "basic classes compiled on" > basic
	date >> basic

objectives: Objective.class BitCounter.class ColorPartition.class Fermat.class
	echo "objectives compiled on" > objectives
	date >> objectives

methods: binMeta.class RandomWalk.class WolfSearch.class
	echo "meta-heuristic methods compiled on" > methods
	date >> methods

binMeta.class: binMeta.java Data.class Objective.class
	javac -cp . binMeta.java

BitCounter.class: BitCounter.java Data.class Objective.class
	javac -cp . BitCounter.java

ColorPartition.class: ColorPartition.java Data.class Objective.class
	javac -cp . ColorPartition.java

Data.class: Data.java
	javac -cp . Data.java

Fermat.class: Fermat.java Data.class Objective.class
	javac -cp . Fermat.java

Memory.class: Memory.java Data.class
	javac -cp . Memory.java

Objective.class: Objective.java
	javac -cp . Objective.java

RandomWalk.class: RandomWalk.java binMeta.class objectives
	javac -cp . RandomWalk.java

WolfSearch.class: WolfSearch.java binMeta.class objectives
	javac -cp . WolfSearch.java

clean:
	\rm basic objectives methods *.class

jar:
	jar cvf binMeta.jar *.java *.class

