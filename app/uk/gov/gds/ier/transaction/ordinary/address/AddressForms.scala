package uk.gov.gds.ier.transaction.ordinary.address

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.PartialManualAddress
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressForms extends AddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  // address mapping for select address page - the address part
  lazy val partialAddressMapping =
    PartialAddress.mapping.verifying(postcodeIsValid, uprnOrManualDefined)

  // address mapping for manual address - the address individual lines part
  lazy val manualPartialAddressLinesMapping =
    PartialManualAddress.mapping.verifying(atLeastOneLineIsRequired, cityIsRequired)

  // address mapping for manual address - the address parent wrapper part
  lazy val manualPartialAddressMapping = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMapping)
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
  ).verifying(postcodeIsValid)

  lazy val postcodeLookupMapping = mapping(
    keys.postcode.key -> nonEmptyText
  ) (
    postcode => PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = postcode,
      manualAddress = None
    )
  ) (
    partial => Some(partial.postcode)
  ).verifying(postcodeIsValid)

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

  // postcodeAddressForm
  val lookupAddressForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(postcodeLookupMapping)
    ) (
      addr => InprogressOrdinary(
        address = addr
      )
    ) (
      inprogress => Some(inprogress.address)
    ).verifying( postcodeIsNotEmpty )
  )

  // selectedAddressForm
  val addressForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (addr, possibleAddr) => InprogressOrdinary(
        address = addr,
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.address,
        inprogress.possibleAddresses
      )
    ).verifying( addressIsRequired )
  )

  // manualAddressForm
  val manualAddressForm = ErrorTransformForm(
    mapping(
      keys.address.key -> optional(manualPartialAddressMapping)
    ) (
      addr => InprogressOrdinary(address = addr)
    ) (
      inprogress => Some(inprogress.address)
    ).verifying( manualAddressIsRequired )
  )
}


trait AddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress) if partialAddress.manualAddress.isDefined => Valid
        case _ => Invalid("ordinary_address_error_pleaseAnswer", keys.address)
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress) if partialAddress.postcode == "" => {
          Invalid("ordinary_address_error_pleaseEnterYourPostcode", keys.address.postcode)
        }
        case None => Invalid("ordinary_address_error_pleaseEnterYourPostcode", keys.address.postcode)
        case _ => Valid
      }
  }

  lazy val addressIsRequired = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      if (inprogress.address.isDefined) Valid
      else Invalid("ordinary_address_error_pleaseAnswer", keys.address)
  }

  lazy val uprnOrManualDefined = Constraint[PartialAddress](keys.address.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "ordinary_address_error_pleaseSelectYourAddress",
      keys.address.uprn,
      keys.address.manualAddress,
      keys.address
    )
  }

  lazy val postcodeIsValid = Constraint[PartialAddress](keys.address.key) {
    case PartialAddress(_, _, postcode, _, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("ordinary_address_error_postcodeIsNotValid", keys.address.postcode)
  }

  lazy val atLeastOneLineIsRequired = Constraint[PartialManualAddress](
      keys.address.manualAddress.key) {
    case PartialManualAddress(None, None, None, _) => Invalid("ordinary_address_error_atLeastOneLineIsRequired",
      keys.address.manualAddress
    )
    case _ => Valid
  }

  lazy val cityIsRequired = Constraint[PartialManualAddress](
      keys.address.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid("ordinary_address_error_cityIsRequired", keys.address.manualAddress.city)
  }
}
