FROM openjdk:8-jre-alpine3.9

LABEL NAME="crawler" Version="1.0"

RUN mkdir /home/discussion
WORKDIR /home
COPY stock_discussion_crawler-1.0.jar run.jar
COPY stock_list.txt stock_list.txt

ENTRYPOINT java -jar ./run.jar 2>> ./discussion/$(date +%Y-%m-%d)_crawler.log