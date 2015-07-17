package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.PostalVoteOption

trait PostalVoteMustache extends StepTemplate[InprogressOrdinary] {

  case class PostalVoteModel(
    question: Question,
    postCheckboxYes: Field,
    postCheckboxNoAndVoteInPerson: Field,
    postCheckboxNoAndAlreadyHave: Field,
    deliveryByEmail: Field,
    deliveryByPost: Field,
    emailField: Field,
    deliveryMethodValid: String
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/postalVote") { implicit lang =>
    (form, postUrl) =>

    implicit val progressForm = form

    val emailAddress = form(keys.contact.email.detail).value

    val deliveryMethodValidation =
      if (form(keys.postalVote.deliveryMethod.methodName).hasErrors) "invalid" else ""

    PostalVoteModel(
      question = Question(
        postUrl = postUrl.url,
        number = s"10 ${Messages("step_of")} 11",
        title = Messages("ordinary_postalVote_title"),
        errorMessages = Messages.translatedGlobalErrors(form)),

      postCheckboxYes = RadioField(
        key = keys.postalVote.optIn,
        value = PostalVoteOption.Yes.name),
      postCheckboxNoAndVoteInPerson = RadioField(
        key = keys.postalVote.optIn,
        value = PostalVoteOption.NoAndVoteInPerson.name),
      postCheckboxNoAndAlreadyHave = RadioField(
        key = keys.postalVote.optIn,
        value = PostalVoteOption.NoAndAlreadyHave.name),

      deliveryByEmail = RadioField(
        key = keys.postalVote.deliveryMethod.methodName,
        value = "email"),
      deliveryByPost = RadioField(
        key = keys.postalVote.deliveryMethod.methodName,
        value = "post"),
      emailField = TextField(
        key = keys.postalVote.deliveryMethod.emailAddress,
        default = emailAddress
      ),
      deliveryMethodValid = deliveryMethodValidation
    )
  }
}

