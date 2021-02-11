package crawler

import org.scalatest.FunSuite

class StockDiscussionCrawlerTest extends FunSuite {
    test("test get discussion") {
      val crawler = new StockDiscussionCrawler("005930")
      val discussion = crawler.getDiscussion("/item/board_read.nhn?code=005930&nid=163402849&st=&sw=&page=8")

      assert(discussion.title.equals("삼성전자 단기7만  중장기6만이에요."))
      assert(discussion.content.equals("지금 늦기전에 매수하고 7만까지 버티고 6만가면 매도 하세요."))
      assert(discussion.nextDiscussionUrl.startsWith("board_read.nhn?code=005930&nid=163402840"))
      assert(discussion.previousDiscussionUrl.startsWith("board_read.nhn?code=005930&nid=163402943"))

    }

  test("test no previous discussion") {
    val crawler = new StockDiscussionCrawler("005930")
    val firstUrl = crawler.getStartDiscussionUrl

    val discussion = crawler.getDiscussion(firstUrl)

    print(discussion)
    assert(discussion.previousDiscussionUrl.length < 1)
    assert(discussion.nextDiscussionUrl.length > 10)

  }

  test("test no next discussion") {
    val crawler = new StockDiscussionCrawler("005930")
    val discussion = crawler.getDiscussion("/item/board_read.nhn?code=005930&nid=64104583&st=&sw=&page=46711")

    print(discussion)
    assert(discussion.nextDiscussionUrl.length < 1)
    assert(discussion.previousDiscussionUrl.length > 10)
  }

}
