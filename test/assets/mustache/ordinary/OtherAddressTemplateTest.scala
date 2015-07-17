package assets.mustache.ordinary

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.ordinary.otherAddress.OtherAddressMustache
import play.api.i18n.Lang

class OtherAddressTemplateTest
  extends TemplateTestSuite
  with OtherAddressMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      implicit val lang = Lang("en")
      val data = OtherAddressModel(
        question = Question(postUrl = "/whatever-url",
          number = "1",
          title = Messages("has other address title (test)")
        ),
        hasOtherAddress = FieldSet(
          classes = "class123"
        ),
        hasOtherAddressStudent = Field(
          id = "test_hasOtherAddress_student",
          name = "test_hasOtherAddress",
          value = "test_student",
          attributes = "aaa=\"test_aaa\""
        ),
        hasOtherAddressHome = Field(
          id = "test_hasOtherAddress_secondHome",
          name = "test_hasOtherAddress",
          value = "test_secondHome",
          attributes = "foo=\"test_foo\""
        ),
        hasOtherAddressNone = Field(
          id = "test_hasOtherAddress_no",
          name = "test_hasOtherAddress",
          value = "test_no",
          attributes = "ccc=\"test_ccc\""
        )
      )

      val html = Mustache.render("ordinary/otherAddress", data)
      val doc = Jsoup.parse(html.toString)

      {
        val div = doc.select("input[id=test_hasOtherAddress_student]").first()
        div should not be(null)
        div.attr("type") should be("radio")
        div.attr("id") should be("test_hasOtherAddress_student")
        div.attr("name") should be("test_hasOtherAddress")
        div.attr("aaa") should include("test_aaa")
      }

      {
        val div = doc.select("input[id=test_hasOtherAddress_secondHome]").first()
        div should not be(null)
        div.attr("type") should include("radio")
        div.attr("id") should be("test_hasOtherAddress_secondHome")
        div.attr("name") should be("test_hasOtherAddress")
        div.attr("foo") should include("test_foo")
      }

      {
        val div = doc.select("input[id=test_hasOtherAddress_no]").first()
        div should not be(null)
        div.attr("type") should include("radio")
        div.attr("id") should be("test_hasOtherAddress_no")
        div.attr("name") should be("test_hasOtherAddress")
        div.attr("ccc") should include("test_ccc")
      }
    }
  }
}
