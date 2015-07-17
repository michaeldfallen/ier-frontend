package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.HasAddressOption

trait AddressLookupMustache extends StepTemplate[InprogressForces] {

  private def pageTitle(hasUkAddress: Option[String]): String = {
    hasUkAddress.map(HasAddressOption.parse) match {
      case Some(HasAddressOption.YesAndLivingThere) => "What is your UK address?"
      case Some(HasAddressOption.YesAndNotLivingThere) => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }


  case class LookupModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/addressLookup") { (form, postUrl) =>
    implicit val progressForm = form

    val title = pageTitle(form(keys.address.hasAddress).value)

    LookupModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.address.address.postcode.asId(),
        name = keys.address.address.postcode.key,
        value = form(keys.address.address.postcode).value.getOrElse(""),
        classes = if (form(keys.address.address.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      )
    )
  }
}

