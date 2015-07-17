package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.HasAddressOption

trait AddressLookupMustache extends StepTemplate[InprogressCrown] {

  private def pageTitle(hasAddress: Option[String]): String = {
    HasAddressOption.parse(hasAddress.getOrElse("")) match{
      case HasAddressOption.YesAndLivingThere | HasAddressOption.YesAndNotLivingThere => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

  case class LookupModel (
      question: Question,
      postcode: Field,
      hasUkAddress: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/addressLookup") { (form, postUrl) =>
    implicit val progressForm = form

    val title = pageTitle(form(keys.hasAddress).value)

    LookupModel(
      question = Question(
        postUrl = postUrl.url,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.address.postcode.asId(),
        name = keys.address.postcode.key,
        value = form(keys.address.postcode).value.getOrElse(""),
        classes = if (form(keys.address.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      ),
      hasUkAddress = HiddenField(
        key = keys.hasAddress,
        value = form(keys.hasAddress).value.getOrElse("")
      )
    )
  }
}

