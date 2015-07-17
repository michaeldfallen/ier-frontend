package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.openRegister.OpenRegisterMustache
import uk.gov.gds.ier.test._

class OpenRegisterTemplateTest
  extends TemplateTestSuite
  with OpenRegisterMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new OpenRegisterModel(
        question = Question(),
        openRegister = Field(
          id = "openRegisterId",
          name = "openRegisterName",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("overseas/openRegister", data)
      val doc = Jsoup.parse(html.toString)

      val openRegisterInput = doc.select("input[id*=openRegisterId]").first()
      openRegisterInput.attr("id") should be("openRegisterId")
      openRegisterInput.attr("name") should be("openRegisterName")
      openRegisterInput.attr("foo") should be("foo")

    }
  }
}
