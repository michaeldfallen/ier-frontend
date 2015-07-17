package assets.mustache.forces

import uk.gov.gds.ier.transaction.forces.rank.RankMustache
import uk.gov.gds.ier.test._

class RankTemplateTest
  extends TemplateTestSuite
  with RankMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new RankModel(
        question = Question(),
        serviceNumber = Field(
          id = "serviceNumberId",
          name = "serviceNumberName",
          classes = "serviceNumberClass",
          value = "serviceNumberValue"
        ),
        rank = Field(
          id = "rankId",
          name = "rankName",
          classes = "rankClass",
          value = "rankValue"
        )
      )

      val html = Mustache.render("forces/rank", data)
      val doc = Jsoup.parse(html.toString)

      doc
        .select("label[for=serviceNumberId]")
        .first()
        .attr("for") should be("serviceNumberId")

      val serviceNumberDiv = doc.select("div[class*=serviceNumberClass]").first()
      serviceNumberDiv.attr("class") should include("serviceNumberClass")
      val serviceNumberInput = serviceNumberDiv.select("input").first()
      serviceNumberInput.attr("id") should be("serviceNumberId")
      serviceNumberInput.attr("name") should be("serviceNumberName")
      serviceNumberInput.attr("value") should be("serviceNumberValue")
      serviceNumberInput.attr("class") should include("serviceNumberClass")


      doc
        .select("label[for=rankId]")
        .first()
        .attr("for") should be("rankId")

      val rankDiv = doc.select("div[class*=rankClass]").first()
      rankDiv.attr("class") should include("rankClass")
      val rankInput = rankDiv.select("input").first()
      rankInput.attr("id") should be("rankId")
      rankInput.attr("name") should be("rankName")
      rankInput.attr("value") should be("rankValue")
      rankInput.attr("class") should include("rankClass")

    }
  }
}
