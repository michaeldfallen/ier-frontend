package uk.gov.gds.ier.transaction.crown.applicationFormVote

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{
  PostalVoteDeliveryMethod,
  PostalOrProxyVote,
  WaysToVoteType}
import uk.gov.gds.ier.transaction.crown.InprogressCrown


class CrownPostalOrProxyVoteMustacheTest
  extends MustacheTestSuite
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByPost

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = postalOrProxyVoteForm
    val postalOrProxyVoteModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/postal-vote"),
      InprogressCrown()
    ).asInstanceOf[PostalOrProxyVoteModel]

    postalOrProxyVoteModel.question.title should be(
      "Do you want us to send you a postal vote application form?")
    postalOrProxyVoteModel.question.postUrl should be(
      "/register-to-vote/crown/postal-vote")

    postalOrProxyVoteModel.description.value should be(
      "If this is your first time using a postal vote,"+
      " or your details have changed, you need to sign and return an application form.")
    postalOrProxyVoteModel.voteType.value should be("by-post")

    postalOrProxyVoteModel.voteFieldSet.classes should be("")
    postalOrProxyVoteModel.voteOptInTrue.value should be("true")
    postalOrProxyVoteModel.voteOptInFalse.value should be("false")
    postalOrProxyVoteModel.voteDeliveryMethodFieldSet.classes should be("")
    postalOrProxyVoteModel.voteDeliveryMethodEmail.value should be("email")
    postalOrProxyVoteModel.voteDeliveryMethodPost.value should be("post")
    postalOrProxyVoteModel.voteEmailAddress.value should be("")
  }

  it should "progress form with filled applicant postal vote should produce Mustache Model with values present" in {
    val partiallyFilledApplicationForm = postalOrProxyVoteForm.fill(InprogressCrown(
      postalOrProxyVote = Some ( PostalOrProxyVote (
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some (PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("address@email.com")))))))

    val postalOrProxyVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/postal-vote"),
      InprogressCrown()
    ).asInstanceOf[PostalOrProxyVoteModel]

    postalOrProxyVoteModel.question.title should be(
      "Do you want us to send you a postal vote application form?")
    postalOrProxyVoteModel.question.postUrl should be(
      "/register-to-vote/crown/postal-vote")

    postalOrProxyVoteModel.description.value should be(
      "If this is your first time using a postal vote,"+
        " or your details have changed, you need to sign and return an application form.")
    postalOrProxyVoteModel.voteType.value should be("by-post")

    postalOrProxyVoteModel.voteFieldSet.classes should be("")
    postalOrProxyVoteModel.voteOptInTrue.attributes should be("checked=\"checked\"")
    postalOrProxyVoteModel.voteOptInFalse.attributes should be("")
    postalOrProxyVoteModel.voteDeliveryMethodFieldSet.classes should be("")
    postalOrProxyVoteModel.voteDeliveryMethodEmail.attributes should be("checked=\"checked\"")
    postalOrProxyVoteModel.voteDeliveryMethodPost.attributes should be("")
    postalOrProxyVoteModel.voteEmailAddress.value should be("address@email.com")

  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationFormWithErrors = postalOrProxyVoteForm.fillAndValidate(InprogressCrown(
      postalOrProxyVote = Some ( PostalOrProxyVote (
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some (PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = None))))))

    val postalOrProxyVoteModel = mustache.data(
      partiallyFilledApplicationFormWithErrors,
      Call("POST", "/register-to-vote/crown/postal-vote"),
      InprogressCrown()
    ).asInstanceOf[PostalOrProxyVoteModel]

    postalOrProxyVoteModel.question.title should be(
      "Do you want us to send you a postal vote application form?")
    postalOrProxyVoteModel.question.postUrl should be(
      "/register-to-vote/crown/postal-vote")

    postalOrProxyVoteModel.description.value should be(
      "If this is your first time using a postal vote,"+
        " or your details have changed, you need to sign and return an application form.")
    postalOrProxyVoteModel.voteType.value should be("by-post")

    postalOrProxyVoteModel.voteFieldSet.classes should be("")
    postalOrProxyVoteModel.voteOptInTrue.attributes should be("checked=\"checked\"")
    postalOrProxyVoteModel.voteOptInFalse.attributes should be("")
    postalOrProxyVoteModel.voteDeliveryMethodFieldSet.classes should be("")
    postalOrProxyVoteModel.voteDeliveryMethodEmail.attributes should be("checked=\"checked\"")
    postalOrProxyVoteModel.voteDeliveryMethodPost.attributes should be("")
    postalOrProxyVoteModel.voteEmailAddress.value should be("")

    postalOrProxyVoteModel.question.errorMessages.mkString(", ") should be("Please enter your email address")
  }
}
