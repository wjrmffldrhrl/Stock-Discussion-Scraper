from selenium import webdriver
import time

options = webdriver.ChromeOptions()
options.add_argument("headless")
driver = webdriver.Chrome(executable_path="./chromedriver.exe", options=options)

url = "https://finance.naver.com/item/board_read.nhn?code=005930&nid=161866412&st=&sw=&page=1"
driver.get(url)
time.sleep(3)
print(driver.find_element_by_id("body").text)


