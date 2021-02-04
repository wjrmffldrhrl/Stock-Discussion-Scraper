#!/usr/bin/python
# -*- coding: utf8 -*-

from Discussion import Discussion
from bs4 import BeautifulSoup
import requests
import time

class StockDiscussionCrawler(object):

    def __init__(self, item_code):
        self.__item_code = item_code
        self.__main_url = "https://finance.naver.com"
        self.__board_url = "/item/board.nhn"

    def __get_start_discussion_url(self):
        target_url = self.__main_url + self.__board_url + "?code=" + self.__item_code
        headers = {"referer": target_url}
        request = requests.get(url=target_url, headers=headers)

        bs = BeautifulSoup(request.text, "html.parser")

        start_discussion_tag = bs.find("td", {"class": "title"})

        discussion_url = start_discussion_tag.a.get("href")

        return discussion_url

    def __get_discussion(self, discussion_url):
        target_url = self.__main_url + discussion_url
        # target_url = "https://finance.naver.com/item/board_read.nhn?code=005930&nid=162275548&st=&sw=&page=1"
        headers = {"referer": target_url}

        request = requests.get(target_url, headers=headers, verify=False)

        bs = BeautifulSoup(request.text, "html.parser")

        # print(bs.contents)
        content_table = bs.find("table", {"summary": "게시판 글 본문보기"})

        title = content_table.find("strong").text

        content = content_table.find("div", {"id": "body"}).contents

        next_previous_table = bs.find("table", {"summary": "이전, 다음 글 목록"})

        # 이전 다음 글 조회
        # 처음과 마지막 a 태그의 속성이 타겟이다
        # 현재 게시글까지 총 3개가 나오지만 가끔 답글이 달리는 경우
        # 4개 이상이 되는 경우도 있다.
        next_discussion_url = ""

        a_tags = next_previous_table.findAll("a")
        if "title" in a_tags[0].attrs:
            next_discussion_url = a_tags[0].attrs["href"]

        previous_discussion_url = a_tags[len(a_tags) - 1].attrs["href"]

        return Discussion(target_url, title, content, "comment", previous_discussion_url, next_discussion_url)

    def crawling_discussions(self, quantity):
        discussion_url = self.__get_start_discussion_url()

        count = 0
        while count < quantity:
            time.sleep(3)

            discussion = self.__get_discussion(discussion_url)

            # if not discussion.has_next():
            #     print("no next discussion...")
            #     continue

            discussion_url = self.__main_url + "/item/" + discussion.get_previous_discussion_url()
            print(discussion.get_title())
            print(discussion_url)
            count += 1


if __name__ == '__main__':
    samsungDiscussionCrawler = StockDiscussionCrawler("005930")
    samsungDiscussionCrawler.crawling_discussions(10)
