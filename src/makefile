# Robert Durst
# Last updated: 2/22/18
# CS461 Project 12

# makefile begins
# Ref: http://profesores.elo.utfsm.cl/~agv/elo329/Java/javamakefile.html
# Ref: https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java
JFLAGS = -g # compiler flags
JC = javac # compiler variable
JVM= java   # specify JVM
CLASSPATH = ../include/*: # maybe not most elegant, or best practices, but ok for now (we get all them jars)
SOURCEPATH = ./ # specify where the .class files should be found for file by file compilation

# define some vars for easier to read code
BASE=proj19DeutschDurstSavillo# base folder name for this project
MAIN=Main
CFGTEST=bantam.codeanalysis.ControlFlowGraphAnalyzer

.SUFFIXES: .java .class

.java.class:
	$(JC) -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) $(JFLAGS) $*.java

# not really sure if I need all these files, but hey, this works
CLASSES =  $(BASE)/*.java 
	  
# name of our main class with its preceding path location
MAIN_CLASS = $(BASE).$(MAIN)

# name of the class for testing out our sweetness of a mips code generator
CFG_TEST = $(BASE).$(CFGTEST)

default: classes

classes: $(CLASSES:.java=.class)

# executes the main code
# notice the colon, this is necessary on unix to seperate between classpath and file to execute
run:
	$(JVM) -classpath $(CLASSPATH) $(MAIN_CLASS)

# executes the mips test
cfg:
	$(JVM) -classpath $(CLASSPATH) $(CFG_TEST)

# cleans up all the compiled class files... and by all I mean all!
# https://unix.stackexchange.com/questions/352636/remove-all-class-files-from-folders-in-bash
clean:
	find $(BASE) -type f -name "*.class" -delete
