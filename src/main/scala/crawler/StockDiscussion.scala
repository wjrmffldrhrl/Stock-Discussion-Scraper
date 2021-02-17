package crawler

/**
 * 종목 토론 객체
 * @param url 주소
 * @param title 제목
 * @param content 내용
 * @param date 게시 날짜
 * @param previousDiscussionUrl 이전 글 주소
 * @param nextDiscussionUrl 다음 글 주소
 */
class StockDiscussion(val url: String, val title: String, val content: String, val date: String,
                      val previousDiscussionUrl: String, val nextDiscussionUrl: String
                     ) {

  override def toString = s"url : [$url] \n title : [$title] \n content: [$content] \n date: [$date] \n previousDiscussionUrl : [$previousDiscussionUrl] \n nextDiscussionUrl : [$nextDiscussionUrl]"

  def toCsv = s"$date,$title,$content,$url,$previousDiscussionUrl,$nextDiscussionUrl"
}
