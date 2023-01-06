FROM openjdk
COPY src .
RUN javac Main.java
CMD ["java", "Main", "-s", "new text", "-e", "add"]