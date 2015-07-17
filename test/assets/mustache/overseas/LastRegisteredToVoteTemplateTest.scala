package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteMustache
import uk.gov.gds.ier.test._

class LastRegisteredToVoteTemplateTest
  extends TemplateTestSuite
  with LastRegisteredToVoteMustache {

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new LastRegisteredModel(
        question = Question(),
        registeredType = Field(
          classes = "registeredTypeClasses"
        ),
        ukResident = Field(
          id = "ukResidentId",
          name = "ukResidentName",
          classes = "ukResidentClasses",
          attributes = """ foo="foo" """,
          value = "ukResidentValue"
        ),
        ukOverseas = Field(
          id = "ukOverseasId",
          name = "ukOverseasName",
          classes = "ukOverseasClasses",
          attributes = """ foo="foo" """,
          value = "ukOverseasValue"
        ),
        armedForces = Field(
          id = "armedForcesId",
          name = "armedForcesName",
          classes = "armedForcesClasses",
          attributes = """ foo="foo" """,
          value = "armedForcesValue"
        ),
        crownServant = Field(
          id = "crownServantId",
          name = "crownServantName",
          classes = "crownServantClasses",
          attributes = """ foo="foo" """,
          value = "crownServantValue"
        ),
        britishCouncil = Field(
          id = "britishCouncilId",
          name = "britishCouncilName",
          classes = "britishCouncilClasses",
          attributes = """ foo="foo" """,
          value = "britishCouncilValue"
        ),
        notRegistered = Field(
          id = "notRegisteredId",
          name = "notRegisteredName",
          classes = "notRegisteredClasses",
          attributes = """ foo="foo" """,
          value = "notRegisteredValue"
        )
      )

      val html = Mustache.render("overseas/lastRegisteredToVote", data)
      val doc = Jsoup.parse(html.toString)


      val fieldset = doc.select("fieldset").first()
      fieldset.attr("class") should be("registeredTypeClasses")

      val ukResidentLabel = fieldset.select("label[for=ukResidentId]")
      ukResidentLabel.attr("for") should be("ukResidentId")

      val ukResidentInput = ukResidentLabel.select("input")
      ukResidentInput.attr("id") should be("ukResidentId")
      ukResidentInput.attr("name") should be("ukResidentName")
      ukResidentInput.attr("value") should be("ukResidentValue")
      ukResidentInput.attr("foo") should be("foo")

      val ukOverseasLabel = fieldset.select("label[for=ukOverseasId]")
      ukOverseasLabel.attr("for") should be("ukOverseasId")

      val ukOverseasInput = ukOverseasLabel.select("input")
      ukOverseasInput.attr("id") should be("ukOverseasId")
      ukOverseasInput.attr("name") should be("ukOverseasName")
      ukOverseasInput.attr("value") should be("ukOverseasValue")
      ukOverseasInput.attr("foo") should be("foo")

      val armedForcesLabel = fieldset.select("label[for=armedForcesId]")
      armedForcesLabel.attr("for") should be("armedForcesId")

      val armedForcesInput = armedForcesLabel.select("input")
      armedForcesInput.attr("id") should be("armedForcesId")
      armedForcesInput.attr("name") should be("armedForcesName")
      armedForcesInput.attr("value") should be("armedForcesValue")
      armedForcesInput.attr("foo") should be("foo")

      val crownServantLabel = fieldset.select("label[for=crownServantId]")
      crownServantLabel.attr("for") should be("crownServantId")

      val crownServantInput = crownServantLabel.select("input")
      crownServantInput.attr("id") should be("crownServantId")
      crownServantInput.attr("name") should be("crownServantName")
      crownServantInput.attr("value") should be("crownServantValue")
      crownServantInput.attr("foo") should be("foo")

      val britishCouncilLabel = fieldset.select("label[for=britishCouncilId]")
      britishCouncilLabel.attr("for") should be("britishCouncilId")

      val britishCouncilInput = britishCouncilLabel.select("input")
      britishCouncilInput.attr("id") should be("britishCouncilId")
      britishCouncilInput.attr("name") should be("britishCouncilName")
      britishCouncilInput.attr("value") should be("britishCouncilValue")
      britishCouncilInput.attr("foo") should be("foo")

      val notRegisteredLabel = fieldset.select("label[for=notRegisteredId]")
      notRegisteredLabel.attr("for") should be("notRegisteredId")

      val notRegisteredInput = notRegisteredLabel.select("input")
      notRegisteredInput.attr("id") should be("notRegisteredId")
      notRegisteredInput.attr("name") should be("notRegisteredName")
      notRegisteredInput.attr("value") should be("notRegisteredValue")
      notRegisteredInput.attr("foo") should be("foo")
    }
  }
}
