package crawler

class StockDiscussion(val url: String, val title: String, val content: String, val date: String,
                      val previousDiscussionUrl: String, val nextDiscussionUrl: String
                     ) {


  def hasNextDiscussionUrl: Boolean = {
    this.nextDiscussionUrl.length > 1
  }


  override def toString = s"url : $url \n title : $title \n content: $content \n date: $date \n previousDiscussionUrl : $previousDiscussionUrl \n nextDiscussionUrl : $nextDiscussionUrl"

  def toCsv = s"$url,$title,$content,$date,$previousDiscussionUrl,$nextDiscussionUrl"
}
