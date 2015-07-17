package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.nino.NinoMustache
import uk.gov.gds.ier.test._

class NinoTemplateTest
  extends TemplateTestSuite
  with NinoMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = NinoModel(
        question = Question(postUrl = "/whatever-url",
        number = "6",
        title = "nino title"
        ),
        nino = Field(
          id = "ninoId",
          name = "ninoName",
          value = "AB123456C"
        ),
        noNinoReason = Field(
          id = "noNinoReasonId",
          name = "noNinoReasonName",
          value = "noNinoReason"
        ),
        noNinoReasonShowFlag = Text (
          value = "noNinoReasonShowFlag"
        )
      )

      val html = Mustache.render("crown/nino", data)
      val doc = Jsoup.parse(html.toString)

      val ninoInput = doc.select("input[id=ninoId]").first()
      ninoInput.attr("id") should be("ninoId")
      ninoInput.attr("name") should be("ninoName")
      ninoInput.attr("value") should be("AB123456C")

      val noNinoReasonInput = doc.select("textarea[id=noNinoReasonId]").first()
      noNinoReasonInput.attr("id") should be("noNinoReasonId")
      noNinoReasonInput.attr("name") should be("noNinoReasonName")
      noNinoReasonInput.text() should be("noNinoReason")

    }
  }
}
