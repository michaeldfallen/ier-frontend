package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.HasAddressOption

trait AddressFirstMustache extends StepTemplate[InprogressForces] {

  val pageTitle = "Do you have a UK address?"

  case class AddressFirstModel(
    question: Question,
    hasAddressYesLivingThere: Field,
    hasAddressYesNotLivingThere: Field,
    hasAddressNo: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/addressFirst") { (form, postUrl, backUrl) =>
    implicit val progressForm = form

    AddressFirstModel(
      question = Question(
        postUrl = postUrl.url,
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      hasAddressYesLivingThere = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.YesAndLivingThere.name
      ),
      hasAddressYesNotLivingThere = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.YesAndNotLivingThere.name
      ),
      hasAddressNo = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.No.name
      )
    )
  }
}

