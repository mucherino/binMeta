
# binMeta project

all: Data.jar Memory.class Objectives.jar MetaHeuristics.jar MetaTest.class

objs=Objective.class BitCounter.class ColorPartition.class Fermat.class SubsetSum.class NumberPartition.class Knapsack.class

methods=binMeta.class LocalOpt.class RandomWalk.class WolfSearch.class

binMeta.class: binMeta.java Data.jar Objectives.jar
	javac -cp .:Data.jar:Objectives.jar binMeta.java

BitCounter.class: BitCounter.java Objective.class Data.jar
	javac -cp .:Data.jar BitCounter.java

ColorPartition.class: ColorPartition.java Objective.class Data.jar
	javac -cp .:Data.jar ColorPartition.java

Data.jar: Data.java
	javac -cp . Data.java
	jar cvf Data.jar Data*.class > /dev/null
	\rm Data*.class

Fermat.class: Fermat.java Objective.class Data.jar
	javac -cp .:Data.jar Fermat.java

Knapsack.class: Knapsack.java Objective.class Data.jar
	javac -cp .:Data.jar Knapsack.java

LocalOpt.class: LocalOpt.java Data.jar Objectives.jar binMeta.class 
	javac -cp .:Data.jar:Objectives.jar LocalOpt.java

Memory.class: Memory.java Data.jar
	javac -cp .:Data.jar Memory.java

MetaHeuristics.jar: Data.jar Memory.class Objectives.jar $(methods)
	jar cvf MetaHeuristics.jar $(methods) > /dev/null

MetaTest.class: MetaTest.java Data.jar Memory.class Objectives.jar MetaHeuristics.jar
	javac -cp .:Data.jar:Objectives.jar:MetaHeuristics.jar MetaTest.java

NumberPartition.class: NumberPartition.java Objective.class Data.jar
	javac -cp .:Data.jar NumberPartition.java

Objectives.jar: $(objs)
	jar cvf Objectives.jar $(objs) > /dev/null

Objective.class: Objective.java Data.jar
	javac -cp .:Data.jar Objective.java

RandomWalk.class: RandomWalk.java Data.jar Objectives.jar binMeta.class
	javac -cp .:Data.jar:Objectives.jar RandomWalk.java

SubsetSum.class: SubsetSum.java Objective.class Data.jar
	javac -cp .:Data.jar SubsetSum.java 

WolfSearch.class: WolfSearch.java Data.jar Objectives.jar binMeta.class LocalOpt.class
	javac -cp .:Data.jar:Objectives.jar WolfSearch.java

pclean:
	\rm -f $(objs) $(methods)

clean:
	\rm -f *.class *.jar

