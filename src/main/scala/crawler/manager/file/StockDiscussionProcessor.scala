package crawler.manager.file

import crawler.StockDiscussion

trait StockDiscussionProcessor {

  def discussionProcessing(discussion: StockDiscussion)
}
