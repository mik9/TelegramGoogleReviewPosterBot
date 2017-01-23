FROM openjdk:8-jre

WORKDIR /usr/app
COPY build/libs/kakashkaposterbot-1.0-SNAPSHOT.jar /usr/app/kakashkaposterbot.jar
COPY TELEGRAM_TOKEN /usr/app
RUN mkdir /usr/app/data

STOPSIGNAL SIGKILL
ENTRYPOINT java -jar /usr/app/kakashkaposterbot.jar