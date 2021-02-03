class Discussion(object):
    def __init__(self, url, title, content, comment, previous_discussion_url, next_discussion_url):
        self.__url = url
        self.__title = title
        self.__content = content
        self.__comment = comment
        self.__previous_discussion_url = previous_discussion_url
        self.__next_discussion_url = next_discussion_url

    def get_next_discussion_url(self):
        return self.__next_discussion_url

    def get_previous_discussion_url(self):
        return self.__previous_discussion_url

    def has_next(self):
        return len(self.__next_discussion_url) > 1

    def get_title(self):
        return self.__title
