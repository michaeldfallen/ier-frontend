package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.guice.WithRemoteAssets

trait AddressManualMustache extends StepTemplate[InprogressOrdinary] {

    case class ManualModel (
        question: Question,
        lookupUrl: String,
        postcode: Field,
        maLineOne: Field,
        maLineTwo: Field,
        maLineThree: Field,
        maCity: Field,
        maLines: FieldSet
    ) extends MustacheData

    val mustache = MultilingualTemplate("ordinary/addressManual") { implicit lang => (form, post) =>

      implicit val progressForm = form

      ManualModel(
        question = Question(
          postUrl = post.url,
          number = Messages("step_a_of_b", 6, 11),
          title = Messages("ordinary_address_manual_title"),
          errorMessages = Messages.translatedGlobalErrors(form)
        ),
        lookupUrl = routes.AddressStep.get.url,
        postcode = TextField(keys.address.postcode),
        maLineOne = TextField(keys.address.manualAddress.lineOne),
        maLineTwo = TextField(keys.address.manualAddress.lineTwo),
        maLineThree = TextField(keys.address.manualAddress.lineThree),
        maCity = TextField(keys.address.manualAddress.city),
        maLines = FieldSet(keys.address.manualAddress)
      )
    }
}
