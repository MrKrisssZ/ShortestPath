# Makefile for Dijkstra && DijkstraNlogN Java Program

# Variables
JAVAC = javac
JAVA = java
MAIN1 = Dijkstra
MAIN2 = DijkstraNlogN

# List of .java files
JAVALIST = $(wildcard *.java)

# List of .class files
LISTCLASSES = $(JAVALIST:.java=.class)

# Default target to compile the code
all: $(LISTCLASSES)

# Pattern rule to compile .java files to .class files
%.class: %.java
	$(JAVAC) $<

# Target to run the Dijkstra program
run-dijkstra: all
	$(JAVA) $(MAIN1)

# Target to run the DijkstraNlogN program
run-dijkstranlogn: all
	$(JAVA) $(MAIN2)

# Target to run both programs
run: run-dijkstra run-dijkstranlogn

# Target to clean the directory
clean:
	rm -f *.class
