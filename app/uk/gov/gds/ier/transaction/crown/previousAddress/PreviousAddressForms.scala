package uk.gov.gds.ier.transaction.crown.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.service.AddressService

trait PreviousAddressForms
    extends PreviousAddressConstraints
    with CommonForms {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  val addressService: AddressService

  lazy val possibleAddressesMappingForPreviousAddress = mapping(
    keys.jsonList.key -> nonEmptyText,
    keys.postcode.key -> nonEmptyText
  ) (
    (json, postcode) => PossibleAddress(
      serialiser.fromJson[Addresses](json),
      postcode
    )
  ) (
    possibleAddress => Some(
      serialiser.toJson(possibleAddress.jsonList),
      possibleAddress.postcode
    )
  )


  val previousAddressForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
    ) (
      (previousAddress, possibleAddresses) => InprogressCrown(
        previousAddress = previousAddress,
        possibleAddresses = possibleAddresses
      )
    ) (
      inprogress => Some(
        inprogress.previousAddress,
        inprogress.possibleAddresses
      )
    ) verifying (
      postcodeIsValidForPreviousAddress,
      manualAddressLineOneRequired,
      cityIsRequiredForPreviousAddress
    )
  )

  /** root validator - postcode page */
  val postcodeStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      postcodeIsNotEmptyForPreviousAddress
    )
  )

  /** root validator - select page */
  val selectStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      selectedAddressIsRequiredForPreviousAddress
    )
  )

  /** root validator - manual address */
  val manualStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      manualAddressIsRequiredForPreviousAddress
    )
  )
}


trait PreviousAddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  // passed from PreviousAddressForms
  val addressService: AddressService

  lazy val previousAddressRequiredIfMoved = Constraint[PartialPreviousAddress](keys.previousAddress.key) {
    previousAddress =>
      val postcode = previousAddress.previousAddress.map(_.postcode)
      val uprn = previousAddress.previousAddress.flatMap(_.uprn)
      val manualAddressCity = previousAddress.previousAddress.flatMap(_.manualAddress.flatMap(_.city))

      val isAddressValid = postcode.exists(addressService.isNothernIreland(_)) | uprn.exists(_.nonEmpty) | manualAddressCity.exists(_.nonEmpty)

      previousAddress.movedRecently match {
        case Some(MovedHouseOption.Yes) if(isAddressValid) => Valid
        case Some(MovedHouseOption.NotMoved) => Valid
        case _ => Invalid("Please complete this step", keys.previousAddress)
      }
  }

  lazy val manualAddressIsRequiredForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress.previousAddress
          .flatMap(_.manualAddress).isDefined && partialAddress
          .previousAddress.exists(_.postcode != "") => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress.previousAddress.manualAddress)
        }
      }
  }

  lazy val selectedAddressIsRequiredForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress)
          if partialAddress.previousAddress.exists(_.postcode != "")
          && (partialAddress.previousAddress.flatMap(_.uprn).isDefined
          ||  partialAddress.previousAddress.flatMap(_.manualAddress).isDefined) => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress.previousAddress.uprn)
        }
      }
  }

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(PartialPreviousAddress(_,Some(partialAddress))) if (partialAddress.postcode.isEmpty) => {
          Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case Some(PartialPreviousAddress(_,None)) => {
          Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case None => {
          Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case _ => {
          Valid
        }
      }
  }

  lazy val uprnOrManualDefinedForPreviousAddressIfNotFromNI = Constraint[InprogressCrown](keys.previousAddress.key) {
    inprogress =>
      val previousAddress = inprogress.previousAddress.flatMap(_.previousAddress)

      val isPreviousAddressValid = previousAddress.exists( partialAddress =>
        addressService.isNothernIreland(partialAddress.postcode) |
        partialAddress.uprn.exists(_.nonEmpty) |
        partialAddress.manualAddress.exists(_.city.exists(_.nonEmpty))
      )

      if (isPreviousAddressValid) Valid
      else Invalid(
        "Please select your address",
        keys.previousAddress.previousAddress.uprn,
        keys.previousAddress.previousAddress.manualAddress,
        keys.previousAddress
      )
  }

  lazy val postcodeIsValidForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
    inprogress =>
      val possiblePostcode = inprogress.previousAddress.flatMap(_.previousAddress).map(_.postcode)
      possiblePostcode match {
        case Some(postcode) if !PostcodeValidator.isValid(postcode) => Invalid(
          "Your postcode is not valid",
          keys.previousAddress.previousAddress.postcode
        )
        case _ => Valid
      }
  }

  lazy val manualAddressLineOneRequired = Constraint[InprogressCrown](keys.previousAddress.manualAddress.key) {
    inprogress =>
      val manualAddress = inprogress.previousAddress.flatMap(_.previousAddress).flatMap(_.manualAddress)

      manualAddress match {
        case Some(PartialManualAddress(None, _, _, _)) => Invalid(
          atLeastOneLineIsRequiredError,
          keys.previousAddress.previousAddress.manualAddress.lineOne
        )
        case _ => Valid
      }
  }

  lazy val cityIsRequiredForPreviousAddress = Constraint[InprogressCrown](
    keys.previousAddress.manualAddress.key) { inprogress =>
    val manualAddress = inprogress.previousAddress.flatMap(_.previousAddress).flatMap(_.manualAddress)

    manualAddress match {
      case Some(PartialManualAddress(_, _, _, None)) => Invalid(
        cityIsRequiredError,
        keys.previousAddress.previousAddress.manualAddress.city
      )
      case _ => Valid
    }
  }

}
