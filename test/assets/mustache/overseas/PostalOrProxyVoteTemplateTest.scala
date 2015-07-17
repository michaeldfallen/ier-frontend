package assets.mustache.overseas

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.overseas.applicationFormVote.PostalOrProxyVoteMustache
import uk.gov.gds.ier.model.WaysToVoteType

class PostalOrProxyVoteTemplateTest
  extends TemplateTestSuite
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByPost

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new PostalOrProxyVoteModel(
        question = Question(),
        description = Text("description text"),
        voteFieldSet = FieldSet(
          classes = "voteFieldSetClasses"
        ),
        voteOptInTrue = Field(
          id = "voteOptInTrueId",
          name = "voteOptInTrueName",
          attributes = "foo=foo"
        ),
        voteOptInFalse = Field(
          id = "voteOptInFalseId",
          name = "voteOptInFalseName",
          attributes = "foo=foo"
        ),
        voteDeliveryMethodFieldSet = FieldSet(
          classes = "voteDeliveryMethodFieldSetClasses"
        ),
        voteDeliveryMethodEmail = Field(
          id = "voteDeliveryMethodEmailId",
          name = "voteDeliveryMethodEmailName",
          classes = "voteDeliveryMethodEmailClasses",
          attributes = "foo=foo",
          value = "email"
        ),
        voteDeliveryMethodPost = Field(
          id = "voteDeliveryMethodPostId",
          name = "voteDeliveryMethodPostName",
          classes = "voteDeliveryMethodPostClasses",
          attributes = "foo=foo",
          value = "post"
        ),
        voteEmailAddress = Field(
          id = "voteEmailAddressId",
          name = "voteEmailAddressName",
          value = "voteEmailAddressValue",
          classes = "voteEmailAddressClasses"
        ),
        voteType = Field(
          id = "voteTypeId",
          name = "voteTypeName",
          value = "voteTypeValue"
        )
      )

      val html = Mustache.render("overseas/postalOrProxyVote", data)
      val doc = Jsoup.parse(html.toString)

      val fieldsetPostalVote = doc.select("fieldset[data-validation-name=postalVote]").first()
      fieldsetPostalVote.attr("class") should include("voteFieldSetClasses")

      val voteOptInTrueInput = fieldsetPostalVote.select("input[value=true]").first()
      voteOptInTrueInput.attr("id") should be("voteOptInTrueId")
      voteOptInTrueInput.attr("name") should be("voteOptInTrueName")
      voteOptInTrueInput.attr("foo") should be ("foo")

      val voteOptInFalseInput = fieldsetPostalVote.select("input[value=false]").first()
      voteOptInFalseInput.attr("id") should be("voteOptInFalseId")
      voteOptInFalseInput.attr("name") should be("voteOptInFalseName")
      voteOptInFalseInput.attr("foo") should be("foo")

      val helpDiv = doc.select("div[class=help]").first()
      helpDiv.select("p").first().text() should be("description text")

      val deliveryMethodFieldSet = doc.select("fieldset[data-validation-name=deliveryMethod]").first()
      deliveryMethodFieldSet.attr("class") should include("voteDeliveryMethodFieldSetClasses")

      val voteDeliveryMethodEmailInput = deliveryMethodFieldSet.select("input[value=email]").first()
      voteDeliveryMethodEmailInput.attr("id") should be("voteDeliveryMethodEmailId")
      voteDeliveryMethodEmailInput.attr("name") should be("voteDeliveryMethodEmailName")
      voteDeliveryMethodEmailInput.attr("foo") should be ("foo")

      val voteDeliveryMethodPostInput = deliveryMethodFieldSet.select("input[value=post]").first()
      voteDeliveryMethodPostInput.attr("id") should be("voteDeliveryMethodPostId")
      voteDeliveryMethodPostInput.attr("name") should be("voteDeliveryMethodPostName")
      voteDeliveryMethodPostInput.attr("foo") should be("foo")

      val voteEmailAddressInput = deliveryMethodFieldSet.select("input[type=email]").first()
      voteEmailAddressInput.attr("id") should be("voteEmailAddressId")
      voteEmailAddressInput.attr("name") should be("voteEmailAddressName")
      voteEmailAddressInput.attr("class") should include("voteEmailAddressClasses")
      voteEmailAddressInput.attr("value") should be ("voteEmailAddressValue")

      val voteTypeInput = doc.select("input[type=hidden]").first()
      voteTypeInput.attr("id") should be("voteTypeId")
      voteTypeInput.attr("name") should be("voteTypeName")
      voteTypeInput.attr("value") should be ("voteTypeValue")

    }
  }
}

