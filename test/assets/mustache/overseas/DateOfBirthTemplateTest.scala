package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthMustache
import uk.gov.gds.ier.test._

class DateOfBirthTemplateTest
  extends TemplateTestSuite
  with DateOfBirthMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = DateOfBirthModel(
        question = Question(postUrl = "/register-to-vote/overseas/date-of-birth",
        number = "1",
        title = "What is your date of birth?"
        ),
        day = Field(
          id = "dayId",
          name = "dayName",
          classes = "dayClass",
          value = "12"
        ),
        month = Field(
          id = "monthId",
          name = "monthName",
          classes = "monthClass",
          value = "12"
        ),
        year = Field(
          id = "yearId",
          name = "yearName",
          classes = "yearClass",
          value = "1980"
        )
      )

      val html = Mustache.render("overseas/dateOfBirth", data)
      val doc = Jsoup.parse(html.toString)

      doc.select("label[for=dayId]").first().attr("for") should be("dayId")

      val dayInput = doc.select("input[id=dayId]").first()
      dayInput.attr("id") should be("dayId")
      dayInput.attr("name") should be("dayName")
      dayInput.attr("value") should be("12")
      dayInput.attr("class") should include("dayClass")

      doc.select("label[for=dayId]").first().attr("for") should be("dayId")

      val monthInput = doc.select("input[id=monthId]").first()
      monthInput.attr("id") should be("monthId")
      monthInput.attr("name") should be("monthName")
      monthInput.attr("value") should be("12")
      monthInput.attr("class") should include("monthClass")

      doc.select("label[for=yearId]").first().attr("for") should be("yearId")

      val yearInput = doc.select("input[id=yearId]").first()
      yearInput.attr("id") should be("yearId")
      yearInput.attr("name") should be("yearName")
      yearInput.attr("value") should be("1980")
      yearInput.attr("class") should include("yearClass")
    }
  }
}
