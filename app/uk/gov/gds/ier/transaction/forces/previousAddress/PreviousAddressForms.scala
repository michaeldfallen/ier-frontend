package uk.gov.gds.ier.transaction.forces.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.AddressService

trait PreviousAddressForms
    extends PreviousAddressConstraints
    with CommonForms {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  val addressService: AddressService

  // address mapping for select address page - the address part
    lazy val partialAddressMappingForPreviousAddress = 
      PartialAddress.mapping.verifying(
        postcodeIsValidForPreviousAddress, uprnOrManualDefinedForPreviousAddressIfNotFromNI)

  // address mapping for manual address - the address individual lines part
  lazy val manualPartialAddressLinesMappingForPreviousAddress = PartialManualAddress.mapping
    .verifying(lineOneIsRequredForPreviousAddress, cityIsRequiredForPreviousAddress)

  lazy val partialPreviousAddressMappingForPreviousAddress = mapping(
    keys.movedRecently.key -> optional(movedHouseMapping),
    keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  ).verifying(previousAddressRequiredIfMoved)

  // address mapping for manual address - the address parent wrapper part
  val manualPartialPreviousAddressMappingForPreviousAddress = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMappingForPreviousAddress)
  ) (
    (postcode, manualAddress) => PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = postcode,
      manualAddress = manualAddress
    )
  ) (
    partial => Some(
      partial.postcode,
      partial.manualAddress
    )
  ).verifying(postcodeIsValidForPreviousAddress)


  lazy val postcodeLookupMappingForPreviousAddress = mapping(
    keys.postcode.key -> nonEmptyText
  ) (
    postcode => PartialPreviousAddress(
      movedRecently = Some(MovedHouseOption.Yes),
      previousAddress = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = postcode,
        manualAddress = None)
      )
    )
  ) (
    partialPreviousAddress => partialPreviousAddress.previousAddress.map(_.postcode)
  ).verifying(postcodeIsValidForlookupForPreviousAddress)

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

  val postcodeAddressFormForPreviousAddress = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(postcodeLookupMappingForPreviousAddress)
    ) (
      previousAddress => InprogressForces(
        previousAddress = previousAddress
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying( postcodeIsNotEmptyForPreviousAddress )
  )

  val selectAddressFormForPreviousAddress = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
    ) (
      (previousAddress, possibleAddr) => InprogressForces(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
          previousAddress = previousAddress
        )),
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.previousAddress.flatMap(_.previousAddress),
        inprogress.possibleAddresses)
    ).verifying( selectedAddressIsRequiredForPreviousAddress )
  )

  val manualAddressFormForPreviousAddress = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(manualPartialPreviousAddressMappingForPreviousAddress)
    ) (
      previousAddress => InprogressForces(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
          previousAddress = previousAddress
      )))
    ) (
      inprogress => inprogress.previousAddress.map(_.previousAddress)
    ).verifying( manualAddressIsRequiredForPreviousAddress )
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

      previousAddress.movedRecently match {
        case Some(MovedHouseOption.Yes) if postcode.exists(addressService.isNothernIreland(_)) => Valid
        case Some(MovedHouseOption.Yes) if uprn.exists(_.nonEmpty) => Valid
        case Some(MovedHouseOption.Yes) if manualAddressCity.exists(_.nonEmpty) => Valid
        case Some(MovedHouseOption.NotMoved) => Valid
        case _ => Invalid("Please complete this step", keys.previousAddress)
      }
  }

  lazy val manualAddressIsRequiredForPreviousAddress = Constraint[InprogressForces](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress.previousAddress
          .flatMap(_.manualAddress).isDefined && partialAddress
          .previousAddress.exists(_.postcode != "") => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress.manualAddress)
        }
      }
  }

  lazy val selectedAddressIsRequiredForPreviousAddress = Constraint[InprogressForces](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress)
          if partialAddress.previousAddress.exists(_.postcode != "")
          && (partialAddress.previousAddress.flatMap(_.uprn).isDefined
          ||  partialAddress.previousAddress.flatMap(_.manualAddress).isDefined) => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress)
        }
      }
  }

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressForces](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress
          .previousAddress.exists(_.postcode == "") => {
          Invalid("Please enter your postcode", keys.previousAddress.postcode)
        }
        case None => {
          Invalid("Please enter your postcode", keys.previousAddress.postcode)
        }
        case _ => {
          Valid
        }
      }
  }

  lazy val uprnOrManualDefinedForPreviousAddressIfNotFromNI = Constraint[PartialAddress](keys.previousAddress.key) {
    case partialAddress if addressService.isNothernIreland(partialAddress.postcode) => Valid
    case partialAddress if partialAddress.uprn.exists(_.nonEmpty) => Valid
    case partialAddress if partialAddress.manualAddress.exists(_.city.exists(_.nonEmpty)) => Valid
    case _ => Invalid(
      "Please select your address",
      keys.previousAddress.uprn,
      keys.previousAddress.manualAddress,
      keys.previousAddress
    )
  }

  lazy val postcodeIsValidForPreviousAddress = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, postcode, _, _)
      if PostcodeValidator.isValid(postcode) => {
      Valid
    }
    case _ => {
      Invalid("Your postcode is not valid", keys.previousAddress.postcode)
    }
  }

  /**
   * Special version of 'postcodeIsValid' just for Postcode Step.
   * The input type here is different, it is PartialPreviousAddress, wrapping PartialAddress
   * containing the postcode.
   */
  lazy val postcodeIsValidForlookupForPreviousAddress = Constraint[PartialPreviousAddress](keys.previousAddress.key) {
    case PartialPreviousAddress(Some(MovedHouseOption.Yes), Some(PartialAddress(_, _, postcode, _, _)))
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.previousAddress.postcode)
  }

  lazy val lineOneIsRequredForPreviousAddress = Constraint[PartialManualAddress](
    keys.previousAddress.manualAddress.key) {
    case PartialManualAddress(Some(_), _, _, _) => Valid
    case _ => Invalid(atLeastOneLineIsRequiredError, keys.previousAddress.manualAddress.lineOne)
  }

  lazy val cityIsRequiredForPreviousAddress = Constraint[PartialManualAddress](
    keys.previousAddress.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid(cityIsRequiredError, keys.previousAddress.manualAddress.city)
  }
}
