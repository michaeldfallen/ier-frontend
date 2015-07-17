package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.waysToVote.WaysToVoteMustache
import uk.gov.gds.ier.test._

/**
 * Test rendering of Mustache template from given model
 */
class WaysToVoteTemplateTest
  extends TemplateTestSuite
  with WaysToVoteMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new WaysToVoteModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        byPost = Field(
          id = "byPostId",
          name = "byPostName",
          value = "byPostValue",
          attributes = "checked=\"checked1\""
        ),
        byProxy = Field(
          id = "byProxyId",
          name = "byProxyName",
          value = "byProxyValue",
          attributes = "checked=\"checked2\""
        ),
        inPerson = Field(
          id = "inPersonId",
          name = "inPersonName",
          value = "inPersonValue",
          attributes = "checked=\"checked3\""
        )
      )

      val html = Mustache.render("crown/waysToVote", data)
      val doc = Jsoup.parse(html.toString)

      { // by post option
        doc.select("label[for=byPostId]").size() should be(1)
        doc.select("input#byPostId").size() should be(1)
        val r = doc.select("input#byPostId").first()
        r.attr("id") should be("byPostId")
        r.attr("name") should be("byPostName")
        r.attr("value") should be("byPostValue")
        r.attr("checked") should be("checked1")
      }

      { // by proxy option
        doc.select("label[for=byProxyId]").size() should be(1)
        doc.select("input#byProxyId").size() should be(1)
        val r = doc.select("input#byProxyId").first()
        r.attr("id") should be("byProxyId")
        r.attr("name") should be("byProxyName")
        r.attr("value") should be("byProxyValue")
        r.attr("checked") should be("checked2")
      }

      { // in person option, aka 'In the UK, at a polling station'
        doc.select("label[for=inPersonId]").size() should be(1)
        doc.select("input#inPersonId").size() should be(1)
        val r = doc.select("input#inPersonId").first()
        r.attr("id") should be("inPersonId")
        r.attr("name") should be("inPersonName")
        r.attr("value") should be("inPersonValue")
        r.attr("checked") should be("checked3")
      }

      { // page
        val f = doc.select("form").first() // there should be only one form in the template
        f should not be(null)
        f.attr("action") should be ("http://some.server/post_url")

        val h = doc.select("header").first() // there should be only one header in the template
        h should not be(null)
        h.text should include ("123")
        h.text should include ("Page title ABC")
      }
    }
  }
}
