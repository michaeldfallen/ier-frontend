package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.{InprogressForces, WithForcesControllers}

trait PreviousAddressManualMustache extends StepTemplate[InprogressForces] {
  self: WithForcesControllers =>

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

  val mustache = MustacheTemplate("forces/previousAddressManual") {
    (form, postUrl) =>

    implicit val progressForm = form

    ManualModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = forces.PreviousAddressPostcodeStep.routing.get.url,
      postcode = TextField(keys.previousAddress.postcode),
      maLineOne = TextField(keys.previousAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.previousAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.previousAddress.manualAddress.lineThree),
      maCity = TextField(keys.previousAddress.manualAddress.city)
    )
  }
}

