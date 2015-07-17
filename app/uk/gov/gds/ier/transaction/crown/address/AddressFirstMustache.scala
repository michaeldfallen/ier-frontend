package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.HasAddressOption

trait AddressFirstMustache extends StepTemplate[InprogressCrown] {

  val pageTitle = "Do you have a UK address?"

  case class AddressFirstModel(
    question: Question,
    hasAddressYesAndLivingThere: Field,
    hasAddressYesAndNotLivingThere: Field,
    hasAddressNo: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/addressFirst") { (form, postUrl) =>
    implicit val progressForm = form

    AddressFirstModel(
      question = Question(
        postUrl = postUrl.url,
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      hasAddressYesAndLivingThere = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.YesAndLivingThere.name),
      hasAddressYesAndNotLivingThere = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.YesAndNotLivingThere.name),
      hasAddressNo = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.No.name)
    )
  }
}

