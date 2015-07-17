package uk.gov.gds.ier.transaction.forces.address

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{
  LastAddress,
  PartialAddress,
  PartialManualAddress,
  PossibleAddress,
  Addresses}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait AddressForms extends AddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  lazy val possibleAddressesMapping = mapping(
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

  lazy val addressForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(LastAddress.mapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (addr, possibleAddr) => InprogressForces(
        address = addr,
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.address,
        inprogress.possibleAddresses
      )
    ).verifying(
      isPostcodeFormatValid,
      atLeastOneLineIsRequired,
      cityIsRequired
    )
  )

  private[address] lazy val lookupAddressForm = ErrorTransformForm(
    addressForm.mapping.verifying(
      postcodeIsNotEmpty
    )
  )

  private[address] lazy val manualAddressForm = ErrorTransformForm(
    addressForm.mapping.verifying(
      manualAddressIsRequired
    )
  )

  private[address] val selectStepForm = ErrorTransformForm(
    addressForm.mapping.verifying(
      selectedAddressIsRequired
    )
  )
}


trait AddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressForces](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress)
          if partialAddress.address.exists(addr => addr.manualAddress.isDefined && addr.postcode.trim.nonEmpty) => Valid
        case _ => Invalid("Please answer this question", keys.address)
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressForces](keys.address.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress) if (partialAddress.address.exists(_.postcode.trim.isEmpty)
            || !partialAddress.address.isDefined) => {
          Invalid("Please enter your postcode", keys.address.address.postcode)
        }
        case None => Invalid("Please enter your postcode", keys.address.address.postcode)
        case _ => Valid
      }
  }

  lazy val isPostcodeFormatValid = Constraint[InprogressForces](keys.address.address.key) {
    inprogress =>
      val possiblePostcode = inprogress.address.flatMap(_.address).map(_.postcode.trim).getOrElse("")
      if (!PostcodeValidator.isValid(possiblePostcode) && possiblePostcode.nonEmpty)
        Invalid( "Your postcode is not valid",keys.address.address.postcode)
      else Valid
  }

  lazy val addressIsRequired = Constraint[InprogressForces](keys.address.key) {
    inprogress =>
      if (inprogress.address.isDefined) Valid
      else Invalid("Please answer this question", keys.address)
  }

  lazy val uprnOrManualDefined = Constraint[PartialAddress](keys.address.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.address.uprn,
      keys.address.manualAddress,
      keys.address
    )
  }

  lazy val atLeastOneLineIsRequired = Constraint[InprogressForces](
    keys.address.address.manualAddress.key) { inprogress =>
    val manualAddress = inprogress.address.flatMap(_.address).flatMap(_.manualAddress)
    manualAddress match {
      case Some(PartialManualAddress(None, None, None, _)) => Invalid(
        atLeastOneLineIsRequiredError,
        keys.address.address.manualAddress
      )
      case _ => Valid
    }
  }

  lazy val cityIsRequired = Constraint[InprogressForces](
    keys.address.address.manualAddress.key) { inprogress =>
    val manualAddress = inprogress.address.flatMap(_.address).flatMap(_.manualAddress)
    manualAddress match {
      case Some(PartialManualAddress(_, _, _, None)) => Invalid(
        cityIsRequiredError,
        keys.address.address.manualAddress.city
      )
      case _ => Valid
    }
  }

  lazy val selectedAddressIsRequired = Constraint[InprogressForces](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress)
          if partialAddress.address.exists(_.postcode.nonEmpty)
          && (partialAddress.address.flatMap(_.uprn).isDefined
          ||  partialAddress.address.flatMap(_.manualAddress).isDefined) => {
          Valid
        }
        case _ => {
          Invalid("Please select your address", keys.address.address.uprn)
        }
      }
  }
}

