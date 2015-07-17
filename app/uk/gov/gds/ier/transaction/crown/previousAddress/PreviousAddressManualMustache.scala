package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.{InprogressCrown, WithCrownControllers}

trait PreviousAddressManualMustache extends StepTemplate[InprogressCrown] {
  self: WithCrownControllers =>

  val title = "What was your previous UK address?"

  case class ManualModel (
    question: Question,
    lookupUrl: String,
    postcode: Field,
    maLineOne: Field,
    maLineTwo: Field,
    maLineThree: Field,
    maCity: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/previousAddressManual") {
    (form, postUrl) =>

    implicit val progressForm = form

    ManualModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = crown.PreviousAddressPostcodeStep.routing.get.url,
      postcode = TextField(keys.previousAddress.previousAddress.postcode),
      maLineOne = TextField(keys.previousAddress.previousAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.previousAddress.previousAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.previousAddress.previousAddress.manualAddress.lineThree),
      maCity = TextField(keys.previousAddress.previousAddress.manualAddress.city)
    )
  }
}

