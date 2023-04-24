
# binMeta project

all: Data.jar Objectives.jar Memory.class MetaHeuristics.jar MetaTest.class

objs=Objective.class BitCounter.class ColorPartition.class Pi.class Fermat.class SubsetSum.class NumberPartition.class Knapsack.class SetCover.class

methods=binMeta.class LocalOpt.class RandomWalk.class WolfSearch.class VariableNeighbourhoodSearch.class MultiStart.class

###

binMeta.class: binMeta.java Data.jar Objectives.jar
	javac -cp .:Data.jar:Objectives.jar binMeta.java

BitCounter.class: BitCounter.java Objective.class Data.jar
	javac -cp .:Data.jar BitCounter.java

ColorPartition.class: ColorPartition.java Objective.class Data.jar
	javac -cp .:Data.jar ColorPartition.java

Data.jar: Data.java
	javac -cp . Data.java
	jar cvf Data.jar Data*.class > /dev/null

Fermat.class: Fermat.java Objective.class Data.jar
	javac -cp .:Data.jar Fermat.java

Knapsack.class: Knapsack.java Objective.class Data.jar
	javac -cp .:Data.jar Knapsack.java

LocalOpt.class: LocalOpt.java Data.jar Objectives.jar binMeta.class 
	javac -cp .:Data.jar:Objectives.jar LocalOpt.java

Memory.class: Memory.java Data.jar Objectives.jar
	javac -cp .:Data.jar:Objectives.jar Memory.java

MetaHeuristics.jar: Data.jar Memory.class Objectives.jar $(methods)
	jar cvf MetaHeuristics.jar $(methods) > /dev/null

MetaTest.class: MetaTest.java Data.jar Memory.class Objectives.jar MetaHeuristics.jar
	javac -cp .:Data.jar:Objectives.jar:MetaHeuristics.jar MetaTest.java

MultiStart.class: MultiStart.java Data.jar Objectives.jar binMeta.class LocalOpt.class
	javac -cp .:Data.jar:Objectives.jar MultiStart.java

NumberPartition.class: NumberPartition.java Objective.class Data.jar
	javac -cp .:Data.jar NumberPartition.java

Objectives.jar: $(objs)
	jar cvf Objectives.jar $(objs) > /dev/null

Objective.class: Objective.java Data.jar
	javac -cp .:Data.jar Objective.java

Pi.class: Pi.java Objective.class Data.jar
	javac -cp .:Data.jar Pi.java

RandomWalk.class: RandomWalk.java Data.jar Objectives.jar binMeta.class
	javac -cp .:Data.jar:Objectives.jar RandomWalk.java

SetCover.class: SetCover.java Objective.class Data.jar
	javac -cp .:Data.jar SetCover.java

SubsetSum.class: SubsetSum.java Objective.class Data.jar
	javac -cp .:Data.jar SubsetSum.java 

VariableNeighbourhoodSearch.class: VariableNeighbourhoodSearch.java Data.jar Objectives.jar binMeta.class LocalOpt.class
	javac -cp .:Data.jar:Objectives.jar VariableNeighbourhoodSearch.java

WolfSearch.class: WolfSearch.java Data.jar Objectives.jar binMeta.class LocalOpt.class
	javac -cp .:Data.jar:Objectives.jar WolfSearch.java

light:
	\rm -f $(objs) $(methods)

clean:
	\rm -f *.class *.jar

