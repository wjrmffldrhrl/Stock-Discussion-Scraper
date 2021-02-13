package crawler

import crawler.manager.file.{GoogleFileUploader, SimpleFileManager}
import org.scalatest.FunSuite

class NaverStockDiscussionCrawlerTest extends FunSuite {
    test("test get discussion") {
      val crawler = new NaverStockDiscussionCrawler("005930", 1, new SimpleFileManager(10000))
      val discussion = crawler.getDiscussion("/item/board_read.nhn?code=005930&nid=64102696&st=&sw=&page=46750")

      println(discussion)
      assert(discussion.title.equals("문가가  정말 그랬을까에  자서전 당장 사봐야겠네에  댕큐"))
      assert(discussion.content.equals("베트남이 패망한것을 보며 희열을 느꼈다고 자서전에 고백했듯이 625에 김일성이 수백만 명을 죽인 것에 지금도 희열을 느껴 조기를 달지 못하게 했느냐 조기 없는 현충일은 처음본다 625 남침을 대통령이 김정은과 한패가 되어 애국이라고 떠벌이니"))
      assert(discussion.nextDiscussionUrl.startsWith("board_read.nhn?code=005930&nid=64102679"))
      assert(discussion.previousDiscussionUrl.startsWith("board_read.nhn?code=005930&nid=64102719"))

    }

  test("test no previous discussion") {
    val crawler = new NaverStockDiscussionCrawler("005930", 1, new SimpleFileManager(10000))
    val firstUrl = crawler.getStartDiscussionUrl

    val discussion = crawler.getDiscussion(firstUrl)

    print(discussion)
    assert(discussion.previousDiscussionUrl.length < 1)
    assert(discussion.nextDiscussionUrl.length > 10)

  }

  test("test no next discussion") {
    val crawler = new NaverStockDiscussionCrawler("005930", 1, new SimpleFileManager(10000))
    val discussion = crawler.getDiscussion("/item/board_read.nhn?code=005930&nid=64104583&st=&sw=&page=46711")

    print(discussion)
    assert(discussion.nextDiscussionUrl.length < 1)
    assert(discussion.previousDiscussionUrl.length > 10)
  }


}