package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.{InprogressForces, WithForcesControllers}
import uk.gov.gds.ier.model.HasAddressOption

trait AddressManualMustache extends StepTemplate[InprogressForces] {
  self: WithForcesControllers =>

  private def pageTitle(hasUkAddress: Option[String]): String = {
    hasUkAddress map HasAddressOption.parse match {
      case Some(HasAddressOption.YesAndLivingThere) => "What is your UK address?"
      case Some(HasAddressOption.YesAndNotLivingThere) => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

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


  val mustache = MustacheTemplate("forces/addressManual") { (form, postUrl) =>
    implicit val progressForm = form

    val title = pageTitle(form(keys.address.hasAddress).value)

    ManualModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = forces.AddressStep.routing.get.url,
      postcode = TextField(keys.address.address.postcode),
      maLineOne = TextField(keys.address.address.manualAddress.lineOne),
      maLineTwo = TextField(keys.address.address.manualAddress.lineTwo),
      maLineThree = TextField(keys.address.address.manualAddress.lineThree),
      maCity = TextField(keys.address.address.manualAddress.city),
      maLines = FieldSet(keys.address.address.manualAddress)
    )
  }
}

