package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary


trait PreviousAddressFirstMustache extends StepTemplate[InprogressOrdinary] {

  case class PreviousAddressFirstModel(
    question: Question,
    registeredAbroad: FieldSet,
    previousYesUk: Field,
    previousYesAbroad: Field,
    previousNo: Field,
    registeredAbroadYes: Field,
    registeredAbroadNo: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/previousAddressFirst") { implicit lang =>
    (form, post) =>

    implicit val progressForm = form

    PreviousAddressFirstModel(
      question = Question(
        postUrl = post.url,
        number = s"8 ${Messages("step_of")} 11",
        title = Messages("ordinary_previousAddress_title"),
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      registeredAbroad = FieldSet(
        classes = if (form(keys.previousAddress.wasRegisteredWhenAbroad).hasErrors) "invalid" else ""
      ),
      previousYesUk = RadioField(
        key = keys.previousAddress.movedRecently.movedRecently,
        value = MovedHouseOption.MovedFromUk.name
      ),
      previousYesAbroad = RadioField(
        key = keys.previousAddress.movedRecently.movedRecently,
        value = MovedHouseOption.MovedFromAbroad.name
      ),
      previousNo = RadioField(
        key = keys.previousAddress.movedRecently.movedRecently,
        value = MovedHouseOption.NotMoved.name
      ),
      registeredAbroadYes = RadioField(
        key = keys.previousAddress.movedRecently.wasRegisteredWhenAbroad,
        value = "true"
      ),
      registeredAbroadNo = RadioField(
        key = keys.previousAddress.movedRecently.wasRegisteredWhenAbroad,
        value = "false"
      )
    )
  }
}

