FROM openjdk
COPY src .
RUN javac Main.java
CMD ["java", "Main", "-s", "text", "-e", "search"]