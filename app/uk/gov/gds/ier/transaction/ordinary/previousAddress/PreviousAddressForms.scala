package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.PartialManualAddress
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.service.AddressService

trait PreviousAddressForms extends PreviousAddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  val addressService: AddressService

  private[previousAddress] lazy val possibleAddressesMappingForPreviousAddress = mapping(
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

  private[previousAddress] val previousAddressForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
    ) (
      (previousAddress, possibleAddresses) => InprogressOrdinary(
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
  private[previousAddress] val postcodeStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      postcodeIsNotEmptyForPreviousAddress
    )
  )

  /** root validator - select page */
  private[previousAddress] val selectStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      selectedAddressIsRequiredForPreviousAddress
    )
  )

  /** root validator - manual address */
  private[previousAddress] val manualStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      manualAddressIsRequiredForPreviousAddress
    )
  )
}


trait PreviousAddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  val addressService: AddressService

  lazy val previousAddressRequiredIfMoved = Constraint[PartialPreviousAddress](keys.previousAddress.key) {
    previousAddress =>
      val postcode = previousAddress.previousAddress.map(_.postcode)
      val uprn = previousAddress.previousAddress.flatMap(_.uprn)
//      val manualAddressCity = previousAddress.previousAddress.flatMap(_.manualAddress.flatMap(_.city))

      val isAddressValid = (postcode.exists(addressService.isNothernIreland(_))) ||
          (uprn.exists(_.nonEmpty)) || previousAddress.previousAddress.exists(_.manualAddress.isDefined)
//          (manualAddressCity.exists(_.nonEmpty))

      previousAddress.movedRecently match {
        case Some(MovedHouseOption.MovedFromUk) | Some(MovedHouseOption.MovedFromAbroadRegistered)
          if (isAddressValid) => Valid
        case Some(MovedHouseOption.NotMoved) => Valid
        case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => Valid
        case _ => Invalid("ordinary_confirmation_error_completeThis", keys.previousAddress)
      }
  }

  lazy val manualAddressIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress.previousAddress
          .flatMap(_.manualAddress).isDefined && partialAddress
          .previousAddress.exists(_.postcode != "") => {
          Valid
        }
        case _ => {
          Invalid("ordinary_previousAddress_manual_error_answerThis", keys.previousAddress.previousAddress.manualAddress)
        }
      }
  }

  lazy val selectedAddressIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress)
          if partialAddress.previousAddress.exists(_.postcode != "")
          && (partialAddress.previousAddress.flatMap(_.uprn).isDefined
          ||  partialAddress.previousAddress.flatMap(_.manualAddress).isDefined) => {
          Valid
        }
        case _ => {
          Invalid("ordinary_previousAddress_select_error_answerThis", keys.previousAddress.previousAddress.uprn)
        }
      }
  }

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(PartialPreviousAddress(_,Some(partialAddress)))
          if (partialAddress.postcode == "") => {
            Invalid("ordinary_previousAddress_postcode_error_enterPostcode", keys.previousAddress.previousAddress.postcode)
        }
        case Some(PartialPreviousAddress(_,None)) => {
          Invalid("ordinary_previousAddress_postcode_error_enterPostcode", keys.previousAddress.previousAddress.postcode)
        }
        case None => {
          Invalid("ordinary_previousAddress_postcode_error_enterPostcode", keys.previousAddress.previousAddress.postcode)
        }
        case _ => {
          Valid
        }
      }
  }

  lazy val postcodeIsValidForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      val possiblePostcode = inprogress.previousAddress.flatMap(_.previousAddress).map(_.postcode)
      possiblePostcode match {
        case Some(postcode) if !PostcodeValidator.isValid(postcode) => Invalid(
          "ordinary_previousAddress_postcode_error_invalidPostcode",
          keys.previousAddress.previousAddress.postcode
        )
        case Some(postcode) if addressService.isNothernIreland(postcode) => Valid
        case _ => Valid
      }
  }

  lazy val manualAddressLineOneRequired = Constraint[InprogressOrdinary](
      keys.previousAddress.manualAddress.key) { inprogress =>

    inprogress.previousAddress.flatMap(_.previousAddress) match{
      case Some(PartialAddress(_, _, postcode, manualAddress, _)) => {
        if (addressService.isNothernIreland(postcode)) Valid
        else if (manualAddress.exists(!_.lineOne.isDefined)) Invalid(
          "ordinary_previousAddress_manual_error_oneAddressLineRequired",
          keys.previousAddress.previousAddress.manualAddress.lineOne
        )
        else Valid
      }
      case _ => Valid
    }
  }

  lazy val cityIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](
      keys.previousAddress.manualAddress.key) { inprogress =>

    inprogress.previousAddress.flatMap(_.previousAddress) match{
      case Some(PartialAddress(_, _, postcode, manualAddress, _)) => {
        if (addressService.isNothernIreland(postcode)) Valid
        else if (manualAddress.exists(!_.city.isDefined)) Invalid(
          "ordinary_previousAddress_manual_error_cityRequired",
          keys.previousAddress.previousAddress.manualAddress.city
        )
        else Valid
      }
      case _ => Valid
    }
  }
}
