class Discussion(object):
    def __init__(self, url, previous_discussion_url, next_discussion_url, title, content, comment):
        self.__url = url
        self.__previous_discussion_url = previous_discussion_url
        self.__next_discussion_url = next_discussion_url
        self.__title = title
        self.__content = content
        self.__comment = comment


