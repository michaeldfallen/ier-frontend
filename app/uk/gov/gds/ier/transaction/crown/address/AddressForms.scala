package uk.gov.gds.ier.transaction.crown.address

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait AddressForms
  extends AddressConstraints
  with CommonForms {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  // address mapping for select address page - the address part
    lazy val partialAddressMapping = 
      PartialAddress.mapping.verifying(postcodeIsValidPartialAddress, uprnOrManualDefined)

  // address mapping for manual address - the address individual lines part
    lazy val manualPartialAddressLinesMapping = 
      PartialManualAddress.mapping.verifying(atLeastOneLineIsRequired, cityIsRequired)

  lazy val lastAddressMapping = mapping(
    keys.hasAddress.key -> optional(hasAddressMapping),
    keys.address.key -> optional(partialAddressMapping)
  ) (
    LastAddress.apply
  ) (
    LastAddress.unapply
  ).verifying(isValidLastAddress)

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
  ).verifying(postcodeIsValidPartialAddress)



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
  ).verifying(postcodeIsValidPartialAddress)

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

  val lookupAddressForm = ErrorTransformForm(
    mapping (
      keys.hasAddress.key -> optional(hasAddressMapping),
      keys.address.key -> optional(postcodeLookupMapping)
    ) (
      (hasAddress, addr) => InprogressCrown(
        address = Some(LastAddress(hasAddress, addr))
      )
    ) (
      inprogress => inprogress.address.map(address => (address.hasAddress, address.address))
    ).verifying( postcodeIsNotEmpty )
  )

  val addressForm = ErrorTransformForm(
    mapping (
      keys.hasAddress.key -> optional(hasAddressMapping),
      keys.address.key -> optional(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (hasAddress, addr, possibleAddr) => InprogressCrown(
        address = Some(LastAddress(hasAddress, addr)),
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        if (inprogress.address.isDefined)
          inprogress.address.get.hasAddress
        else None,
        if (inprogress.address.isDefined)
          inprogress.address.get.address
        else None,
        inprogress.possibleAddresses
      )
    ).verifying( addressIsRequired )
  )

  val manualAddressForm = ErrorTransformForm(
    mapping(
      keys.hasAddress.key -> optional(hasAddressMapping),
      keys.address.key -> optional(manualPartialAddressMapping)
    ) (
      (hasAddress, addr) => InprogressCrown(
        address = Some(LastAddress(hasAddress, addr))
      )
    ) (
      inprogress => inprogress.address.map(address => (address.hasAddress, address.address))
    ).verifying( manualAddressIsRequired )
  )
}


trait AddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(lastUkAddress) => {
          if (lastUkAddress.address.isDefined) {
            val manualAddress = lastUkAddress.address.get.manualAddress
            if (manualAddress.isDefined) Valid
            else Invalid("Please answer this question", keys.address)
          }
          else Invalid("Please answer this question", keys.address)
        }
        case None => Invalid("Please answer this question", keys.address)
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(lastUkAddress) => {
          if (lastUkAddress.address.isDefined) {
            val postcode = lastUkAddress.address.get.postcode
            if (postcode == "") Invalid("Please enter your postcode", keys.address.postcode)
            else Valid
          }
          else Invalid("Please enter your postcode", keys.address.postcode)
        }
        case None => Invalid("Please enter your postcode", keys.address.postcode)
      }
  }

  lazy val addressIsRequired = Constraint[InprogressCrown](keys.address.key) {
    inprogress =>
      if (inprogress.address.isDefined) {
        val lastUkAddress = inprogress.address.get
        if (lastUkAddress.address.isDefined) Valid
        else Invalid("Please answer this question", keys.address)
      }
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

  lazy val postcodeIsValidPartialAddress = Constraint[PartialAddress](keys.address.key) {
    case PartialAddress(_, _, postcode, _, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.address.postcode)
  }

  lazy val postcodeIsValid = Constraint[LastAddress](keys.address.key) {
    case LastAddress(_,Some(PartialAddress(_, _, postcode, _, _)))
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.address.postcode)
  }

  lazy val atLeastOneLineIsRequired = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(None, None, None, _) => Invalid(
      atLeastOneLineIsRequiredError,
      keys.address.manualAddress
    )
    case _ => Valid
  }

  lazy val cityIsRequired = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid(cityIsRequiredError, keys.address.manualAddress.city)
  }

  lazy val isValidLastAddress = Constraint[LastAddress](keys.address.key) {
    case LastAddress(Some(_), Some(_)) => Valid
    case _ => Invalid("Please answer this question", keys.address)
  }
}

