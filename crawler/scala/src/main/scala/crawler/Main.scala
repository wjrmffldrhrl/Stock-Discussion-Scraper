package crawler
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object Main extends App {
  val browser = JsoupBrowser()
  val doc2 = browser.get("http://example.com")

  print(doc2)
}
