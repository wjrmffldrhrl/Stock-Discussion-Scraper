
# Stock Discussion Crawler
![Screen Shot 2021-04-25 at 23 30 28 PM](https://user-images.githubusercontent.com/49122299/115998561-c0e0f800-a622-11eb-9ad9-156bdfbc2e9b.png)  

종목 토론방 게시글 크롤러


# Environment
### OS
- Mac OS Big Sur 11.2.3
- Ubuntu 20.04.1
### JDK
OpenJDK 1.8.0_282
### Docker
Docker version 20.10.5


# Target Site
현재 네이버 주식 토론방 게시글만 크롤링 가능합니다.
- [Naver Stock Discussion](https://finance.naver.com/sise/)

# Feature  
- Target Site에서 생성되는 토론글을 실시간으로 크롤링 합니다.
  - 크롤링주기는 1초이며 새로운 게시글이 없으면 추기가 2배씩 늘어납니다.  
    - 무의미한 요청을 최대한 막기 위함입니다.  
  - 크롤링 되는 속도가 느리게 느껴질 수 있지만, 서버에 부담을 줄이기 위해 주기를 길게 했습니다.
- 크롤링 된 데이터는 `discussion/itemcode/YYYY_MM_DD.csv`파일로 생성됩니다.
  - 컬럼 형태는 `date,title,url,content`입니다.

# Result

```csv
date,title,url,content
2021.04.25T13:20,https://finance.naver.com/item/board_read.nhn?code=005930&nid=173153368&st=&sw=&page=1,남자는 사병복무 여자는 출산,한국군 이등병으로 복무 안해본 남자와 한국 인의 아이를 출산을 하지 않은 여자는 이나라의 기생충이고 쓰레기임. 싫어도 해야 나라가 유지되고 존속되니 해야하는게 저것 남자애들이 싫다고 장교 부사관만 하고 이등병 사병안하면 나라는 망하듯이.. 여자애들이 싫다고 손해라고 애 안낳으면 나라는 망함.. 실건 좋건의 문제가 아니라 어느나라든 국가가 존속하기 위해서는 싫어도 반드시 해야할 일이 저 두가지. 여자는 한국인의 아이를 싫어도 출산해야할 의무가 잇는거고 남자는 한국군의 사병으로 싫어도 징집되어야할 의무가 있는것. 40전에 한국인의 애를 하나도 안낳은 여자는 그냥 이니라의 기생충 쓰레기일 뿐임
2021.04.25T13:21,https://finance.naver.com/item/board_read.nhn?code=005930&nid=173153396&st=&sw=&page=1,어영부영....공매도시작...,공매도 없어도 오르고 내리고 잘 하는데  굳이??
2021.04.25T13:23,https://finance.naver.com/item/board_read.nhn?code=005930&nid=173153440&st=&sw=&page=1,다음주 증시가 살짝 기대되기는 하네 ㅋㅋ,비트코인은 사실상 양털깍기 들어갔고 손턴사람들은 슬슬 주식시장으로 다시 들어오면 살짝 다음주 증시가 기대되기는 하네 ㅋㅋ
2021.04.25T13:23,https://finance.naver.com/item/board_read.nhn?code=005930&nid=173153449&st=&sw=&page=1,▶▷ 급등주 리딩방 추천 ◁◀,https://bit.ly/3vgVP5R 1000 -> 4700 ^^
```
# How to run
바로 실행하시려면 Docker와 JDK(1.8 이상)가 설치되어있어야 합니다.
### for mac or linux
```shell
git clone https://github.com/wjrmffldrhrl/stock_discussion_crawler.git

cd stock_discussion_crawler

sh run_docker_crawler.sh
```

### for window
준비중...

## Edit stock_list.txt
크롤링 하고 싶은 종목의 종목 코드를 `stock_list.txt`파일에 입력합니다.
지금은 시가총액 상위 10개 종목의 코드만 `stock_list.txt` 파일에 입력되어있습니다.

`#`으로 시작되는 아이템 코드는 크롤링 하지 않습니다.  

### stock_list.txt
```text
005930
000660
051910
005935
035420
006400
207940
005380
068270
035720
#002501 크롤링 X
```  

크롤링 하는 종목당 한 개의 크롤러가 쓰레드로 생성되고 실행됩니다.  

# Docker
성공적으로 도커가 빌드되고 컨테이너가 실행된다면 프로젝트 루트 디렉터리에 `vol`디렉터리가 생성되고 해당 디렉터리가 컨테이너의 볼륨으로 지정되어 크롤링 데이터들과 로그 파일(`YYYY-MM-DD_crawler.log`)이 생성됩니다.  

  

