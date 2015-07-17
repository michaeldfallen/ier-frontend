package assets.mustache.ordinary

import uk.gov.gds.ier.transaction.ordinary.postalVote.PostalVoteMustache
import uk.gov.gds.ier.test._

class PostalVoteTemplateTest
  extends TemplateTestSuite
  with PostalVoteMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = PostalVoteModel(
        question = Question(postUrl = "/whatever-url",
        number = "1",
        title = "postal vote title"
        ),
        postCheckboxYes = Field(
          id = "postCheckboxYesId",
          name = "postCheckboxYesName",
          attributes = "foo=\"foo\""
        ),
        postCheckboxNoAndVoteInPerson = Field(
          id = "postCheckboxNoAndVoteInPersonId",
          name = "postCheckboxNoAndVoteInPersonName",
          attributes = ""
        ),
        postCheckboxNoAndAlreadyHave = Field(
          id = "postCheckboxNoAndAlreadyHaveId",
          name = "postCheckboxNoAndAlreadyHaveName",
          attributes = ""
        ),
        deliveryByEmail = Field(
          id = "deliveryByEmailId",
          name = "deliveryByEmailName",
          value = "deliveryByEmailValue",
          attributes = "foo=\"foo\""
        ),
        deliveryByPost = Field(
          id = "deliveryByPostId",
          name = "deliveryByPostName",
          value = "deliveryByPostValue",
          attributes = ""
        ),
        emailField = Field(
          id = "emailFieldId",
          name = "emailFieldName",
          value = "test@test.com"
        ),
        deliveryMethodValid = "valid"
      )

      val html = Mustache.render("ordinary/postalVote", data)
      val doc = Jsoup.parse(html.toString)

      val postCheckboxYesInput = doc.select("input[id=postCheckboxYesId]").first()
      postCheckboxYesInput.attr("id") should be("postCheckboxYesId")
      postCheckboxYesInput.attr("name") should be("postCheckboxYesName")
      postCheckboxYesInput.attr("foo") should include("foo")

      val postCheckboxNoAndVoteInPersonInput = doc.select("input[id=postCheckboxNoAndVoteInPersonId]").first()
      postCheckboxNoAndVoteInPersonInput.attr("id") should be("postCheckboxNoAndVoteInPersonId")
      postCheckboxNoAndVoteInPersonInput.attr("name") should be("postCheckboxNoAndVoteInPersonName")

      val postCheckboxNoAndAlreadyHaveInput = doc.select("input[id=postCheckboxNoAndAlreadyHaveId]").first()
      postCheckboxNoAndAlreadyHaveInput.attr("id") should be("postCheckboxNoAndAlreadyHaveId")
      postCheckboxNoAndAlreadyHaveInput.attr("name") should be("postCheckboxNoAndAlreadyHaveName")

      val deliveryByEmailInput = doc.select("input[id=deliveryByEmailId]").first()
      deliveryByEmailInput.attr("id") should be("deliveryByEmailId")
      deliveryByEmailInput.attr("name") should be("deliveryByEmailName")
      deliveryByEmailInput.attr("value") should include("deliveryByEmailValue")
      deliveryByEmailInput.attr("foo") should include("foo")

      val deliveryByPostInput = doc.select("input[id=deliveryByPostId]").first()
      deliveryByPostInput.attr("id") should be("deliveryByPostId")
      deliveryByPostInput.attr("name") should be("deliveryByPostName")

      val emailFieldInput = doc.select("input[id=emailFieldId]").first()
      emailFieldInput.attr("id") should be("emailFieldId")
      emailFieldInput.attr("name") should be("emailFieldName")
      emailFieldInput.attr("value") should be("test@test.com")
    }
  }
}
