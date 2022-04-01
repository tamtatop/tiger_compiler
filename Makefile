COMPILER_JAR := tigerc.jar 
GRAMMAR := Tiger.g4


.PHONY:
all: 	
	chmod +x ./gradlew
	./gradlew jar
	cp build/libs/tigerc.jar $(COMPILER_JAR)

.PHONY:
clean:
	./gradlew clean
	rm $(COMPILER_JAR)
