package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressManualMustache extends StepTemplate[InprogressOrdinary] {

  case class ManualModel (
      question: Question,
      lookupUrl: String,
      postcode: Field,
      maLineOne: Field,
      maLineTwo: Field,
      maLineThree: Field,
      maCity: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/previousAddressManual") { implicit lang =>
    (form, post) =>
    implicit val progressForm = form

    val movedRecently = form(keys.previousAddress.movedRecently).value.map {
      str => MovedHouseOption.parse(str)
    }

      val title = movedRecently match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => Messages("ordinary_previousAddress_yesFromAbroadWasRegistered_title")
      case _ => Messages("ordinary_previousAddress_yesFromUk_title")
    }

    ManualModel(
      question = Question(
        postUrl = post.url,
        number = s"8 ${Messages("step_of")} 11",
        title = title,
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      lookupUrl = routes.PreviousAddressPostcodeStep.get.url,
      postcode = TextField(keys.previousAddress.previousAddress.postcode),
      maLineOne = TextField(keys.previousAddress.previousAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.previousAddress.previousAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.previousAddress.previousAddress.manualAddress.lineThree),
      maCity = TextField(keys.previousAddress.previousAddress.manualAddress.city)
    )
  }
}
