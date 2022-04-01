COMPILER_JAR := tigerc.jar 
GRAMMAR := Tiger.g4


.PHONY:
all: 	
	./gradlew jar
	cp build/libs/tiger_compiler.jar $(COMPILER_JAR)

.PHONY:
clean:
	./gradlew clean
	rm -r lib
	rm $(COMPILER_JAR)
