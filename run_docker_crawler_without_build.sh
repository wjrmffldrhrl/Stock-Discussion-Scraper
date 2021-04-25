mv ./stock_list.txt ./docker_crawler

docker build --tag crawler:1.0 ./docker_crawler
mkdir ./vol

docker run -d -v `pwd`/vol:/home/discussion --name crawler crawler:1.0
