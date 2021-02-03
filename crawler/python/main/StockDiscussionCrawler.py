import requests
from bs4 import BeautifulSoup


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
        headers = {"referer": target_url}
        request = requests.get(target_url, headers=headers)


        bs = BeautifulSoup(request.text, "html.parser")

        content_table = bs.find("table", {"summary": "게시판 글 본문보기"})

        title = content_table.find("strong").contents
        content = content_table.find("div", {"id": "body"}).contents

        next_previous_table = bs.find("table", {"summary": "이전, 다음 글 목록"})

        print(next_previous_table)
        # print("title: ", title)
        # print("content : ", content)

    def get(self):
        discussion_url = self.__get_start_discussion_url()
        self.__get_discussion(discussion_url)


if __name__ == '__main__':
    samsungDiscussionCrawler = StockDiscussionCrawler("005930")
    samsungDiscussionCrawler.get()
