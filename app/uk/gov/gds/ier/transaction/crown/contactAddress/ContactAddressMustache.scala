package uk.gov.gds.ier.transaction.crown.contactAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.model.{LastAddress, PartialAddress}
import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait ContactAddressMustache
  extends StepTemplate[InprogressCrown]
    with AddressHelpers {

  case class ContactAddressModel(
      question:Question,
      contactAddressFieldSet: FieldSet,
      ukAddress: UKContactAddressModel,
      bfpoAddress: BFPOContactAddressModel,
      otherAddress: OtherContactAddressModel
  ) extends MustacheData

  case class OtherContactAddressModel(
      otherAddressOption: Field,
      otherAddressLine1: Field,
      otherAddressLine2: Field,
      otherAddressLine3: Field,
      otherAddressLine4: Field,
      otherAddressLine5: Field,
      otherAddressPostcode: Field,
      otherAddressCountry: Field
  )

  case class BFPOContactAddressModel(
      BFPOAddressOption: Field,
      BFPOAddressLine1: Field,
      BFPOAddressLine2: Field,
      BFPOAddressLine3: Field,
      BFPOAddressLine4: Field,
      BFPOAddressLine5: Field,
      BFPOAddressPostcode: Field
  )

  case class UKContactAddressModel(
      ukAddressOption: Field,
      ukAddressLineText: Field
  )

  val mustache = MustacheTemplate("crown/contactAddress") {
    (form, post, application) =>

    implicit val progressForm = form

    val ukAddressToBeShown = extractUkAddressText(application.address, form)

    val ukContactAddressModel = UKContactAddressModel(
      ukAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "uk"
      ),
      ukAddressLineText = HiddenField (
        key = keys.contactAddress.ukAddressTextLine,
        value =  ukAddressToBeShown.getOrElse("")
      )
    )

    val bfpoContactAddressModel = BFPOContactAddressModel (
      BFPOAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "bfpo"
      ),
      BFPOAddressLine1 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine1
      ),
      BFPOAddressLine2 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine2
      ),
      BFPOAddressLine3 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine3
      ),
      BFPOAddressLine4 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine4
      ),
      BFPOAddressLine5 = TextField(
        key = keys.contactAddress.bfpoContactAddress.addressLine5
      ),
      BFPOAddressPostcode = TextField(
        key = keys.contactAddress.bfpoContactAddress.postcode
      )
    )

    val otherContactAddressModel = OtherContactAddressModel(
      otherAddressOption = RadioField(
        key = keys.contactAddress.contactAddressType,
        value = "other"
      ),
      otherAddressLine1 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine1
      ),
      otherAddressLine2 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine2
      ),
      otherAddressLine3 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine3
      ),
      otherAddressLine4 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine4
      ),
      otherAddressLine5 = TextField(
        key = keys.contactAddress.otherContactAddress.addressLine5
      ),
      otherAddressPostcode = TextField(
        key = keys.contactAddress.otherContactAddress.postcode
      ),
      otherAddressCountry = TextField(
        key = keys.contactAddress.otherContactAddress.country
      )
    )

    val title = "Where should we write to you about your registration?"

    ContactAddressModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map( _.message ),
        title = title
      ),
      contactAddressFieldSet = FieldSet (
        classes = if (form(keys.contactAddress).hasErrors) "invalid" else ""
      ),
      ukAddress = ukContactAddressModel,
      bfpoAddress = bfpoContactAddressModel,
      otherAddress = otherContactAddressModel
    )
  }


  private def extractUkAddressText(
      lastUkAddress: Option[LastAddress],
      form: ErrorTransformForm[InprogressCrown]): Option[String] = {
      val address = lastUkAddress flatMap { _.address }

      val addressLine = address flatMap { _.addressLine }
      val manualAddress = address flatMap { _.manualAddress } flatMap manualAddressToOneLine
      val addressFromForm = form(keys.contactAddress.ukAddressLine).value

      addressLine orElse manualAddress orElse addressFromForm
  }
}
