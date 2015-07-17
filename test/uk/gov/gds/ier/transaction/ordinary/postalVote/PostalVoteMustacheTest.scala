package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{PostalVoteOption, PostalVote, PostalVoteDeliveryMethod}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PostalVoteMustacheTest
  extends MustacheTestSuite
  with PostalVoteForms
  with PostalVoteMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = postalVoteForm
    val postalVoteModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/foo/postal-vote"),
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/foo/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNoAndVoteInPerson.attributes should be("")
    postalVoteModel.postCheckboxNoAndAlreadyHave.attributes should be("")


    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")

  }

  it should "progress form with no and vote in person for postal vote" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
        deliveryMethod = None))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/postal-vote"),
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/foo/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNoAndVoteInPerson.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNoAndAlreadyHave.attributes should be("")


    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")
  }

  it should "progress form with no and already have a postal vote" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndAlreadyHave),
        deliveryMethod = None))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/postal-vote"),
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/foo/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("")
    postalVoteModel.postCheckboxNoAndVoteInPerson.attributes should be("")
    postalVoteModel.postCheckboxNoAndAlreadyHave.attributes should be("checked=\"checked\"")


    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("")
  }

  it should "progress form with yes for postal vote and post for delivery method " +
    "should produce form with values" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("post"), None))))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/postal-vote"),
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/foo/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNoAndVoteInPerson.attributes should be("")
    postalVoteModel.postCheckboxNoAndAlreadyHave.attributes should be("")


    postalVoteModel.deliveryByEmail.attributes should be("")
    postalVoteModel.deliveryByPost.attributes should be("checked=\"checked\"")
    postalVoteModel.emailField.value should be("")
  }

  it should "progress form with yes for postal vote and email for delivery method " +
    "should produce form with values" in runningApp {
    val partiallyFilledApplicationForm = postalVoteForm.fill(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(deliveryMethod = Some("email"),
            emailAddress = Some("test@test.com")))))))

    val postalVoteModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/postal-vote"),
      InprogressOrdinary()
    ).asInstanceOf[PostalVoteModel]

    postalVoteModel.question.title should be("Do you want to apply for a postal vote?")
    postalVoteModel.question.postUrl should be("/foo/postal-vote")

    postalVoteModel.postCheckboxYes.attributes should be("checked=\"checked\"")
    postalVoteModel.postCheckboxNoAndVoteInPerson.attributes should be("")
    postalVoteModel.postCheckboxNoAndAlreadyHave.attributes should be("")


    postalVoteModel.deliveryByEmail.attributes should be("checked=\"checked\"")
    postalVoteModel.deliveryByPost.attributes should be("")
    postalVoteModel.emailField.value should be("test@test.com")
  }
}
