package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.{InprogressCrown, WithCrownControllers}
import uk.gov.gds.ier.model.HasAddressOption

trait AddressManualMustache extends StepTemplate[InprogressCrown] {
  self: WithCrownControllers =>

  private def pageTitle(hasAddress: Option[String]): String = {
    HasAddressOption.parse(hasAddress.getOrElse("")) match{
      case HasAddressOption.YesAndLivingThere | HasAddressOption.YesAndNotLivingThere => "What is your UK address?"
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
    hasAddress: Field,
    maLines: FieldSet
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/addressManual") { (form, postUrl) =>
    implicit val progressForm = form

    val title = pageTitle(form(keys.hasAddress).value)

    ManualModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = crown.AddressStep.routing.get.url,
      postcode = TextField(keys.address.postcode),
      maLineOne = TextField(keys.address.manualAddress.lineOne),
      maLineTwo = TextField(keys.address.manualAddress.lineTwo),
      maLineThree = TextField(keys.address.manualAddress.lineThree),
      maCity = TextField(keys.address.manualAddress.city),
      hasAddress = HiddenField(
        key = keys.hasAddress,
        value = form(keys.hasAddress).value.getOrElse("")
      ),
      maLines = FieldSet(keys.address.manualAddress)
    )
  }
}

