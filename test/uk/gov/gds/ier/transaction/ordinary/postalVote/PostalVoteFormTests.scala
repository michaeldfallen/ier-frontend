package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PostalVoteOption, PostalVoteDeliveryMethod, PostalVote}

class PostalVoteFormTests
  extends FormTestSuite
  with PostalVoteForms {

  it should "bind successfully on postal vote true and delivery method post" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "post"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.Yes),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("post"),
            emailAddress = None
          ))
        )))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "test@mail.com"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.Yes),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("test@mail.com")
          ))
        )))
      }
    )
  }

  it should "bind successfully on postal vote true and delivery method email (including email with special characters)" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "o'fake._%+'-@fake._%+'-.co.uk"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.Yes),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("o'fake._%+'-@fake._%+'-.co.uk")
          ))
        )))
      }
    )
  }

  it should "error out on postal vote true and delivery method email with invalid email" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "email",
        "postalVote.deliveryMethod.emailAddress" -> "emailAddress"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.deliveryMethod.emailAddress") should be(Seq("ordinary_postalVote_error_enterValidEmail"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_enterValidEmail"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "bind successfully on postal vote no and in person" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "no-vote-in-person"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
          deliveryMethod = None)))
      }
    )
  }

  it should "bind successfully on postal vote no and already have one" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "no-already-have"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.postalVote should be(Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.NoAndAlreadyHave),
          deliveryMethod = None)))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("ordinary_postalVote_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> ""
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("postalVote.optIn") should be(Seq("ordinary_postalVote_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty method delivery when postalVote.optIn is yes" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "postalVote.deliveryMethod.methodName" -> Seq("ordinary_postalVote_error_answerThis")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_answerThis"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on postalVote.optIn true, method delivery email and empty email address" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "email"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "postalVote.deliveryMethod.emailAddress" -> Seq("ordinary_postalVote_error_enterYourEmail")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_postalVote_error_enterYourEmail"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "ignore a malformed email if other deliveryMethod is chosen" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "yes",
        "postalVote.deliveryMethod.methodName" -> "post",
        "postalVote.deliveryMethod.emailAddress" -> "malformedEmail"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(postalVote) = success.postalVote

        postalVote.postalVoteOption should be(Some(PostalVoteOption.Yes))
        val Some(deliveryMethod) = postalVote.deliveryMethod
        deliveryMethod should have(
          'deliveryMethod (Some("post")),
          'emailAddress (None)
        )
      }
    )
  }

  it should "ignore email/post option if optIn is no and in person" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "no-vote-in-person",
        "postalVote.deliveryMethod.methodName" -> "post",
        "postalVote.deliveryMethod.emailAddress" -> "malformedEmail"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        val Some(postalVote) = success.postalVote

        postalVote.postalVoteOption should be(Some(PostalVoteOption.NoAndVoteInPerson))
        postalVote.deliveryMethod should be(None)
      }
    )
  }

  it should "ignore email/post option if optIn is no and already have one" in {
    val js = Json.toJson(
      Map(
        "postalVote.optIn" -> "no-already-have",
        "postalVote.deliveryMethod.methodName" -> "post",
        "postalVote.deliveryMethod.emailAddress" -> "malformedEmail"
      )
    )
    postalVoteForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        val Some(postalVote) = success.postalVote

        postalVote.postalVoteOption should be(Some(PostalVoteOption.NoAndAlreadyHave))
        postalVote.deliveryMethod should be(None)
      }
    )
  }
}
