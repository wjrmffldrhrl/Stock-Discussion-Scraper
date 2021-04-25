# Build crawler with gradle & move to docker dir
./gradlew build
cp ./build/libs/stock_discussion_crawler-1.0.jar ./docker_crawler/stock_discussion_crawler-1.0.jar
cp ./stock_list.txt ./docker_crawler/stock_list.txt

docker build --tag crawler:1.0 ./docker_crawler
mkdir ./vol

docker run -d -v `pwd`/vol:/home/discussion --name crawler crawler:1.0
