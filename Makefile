COMPILER_JAR := tigerc.jar 
JAR_DIR := cs8803_bin
GRAMMAR := Tiger.g4


.PHONY:
all: 	
	chmod +x ./gradlew
	./gradlew jar
	mkdir $(JAR_DIR)
	cp build/libs/tigerc.jar $(JAR_DIR)/$(COMPILER_JAR)

.PHONY:
clean:
	./gradlew clean
	rm -r $(JAR_DIR)
