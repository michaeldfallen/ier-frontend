package uk.gov.gds.ier.transaction.overseas.parentsAddress

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
  PartialAddress,
  PartialManualAddress,
  PossibleAddress,
  Addresses}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ParentsAddressForms extends ParentsAddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  // address mapping for select address page - the address part
    lazy val parentsPartialAddressMapping = 
      PartialAddress.mapping.verifying(parentsPostcodeIsValid, parentsUprnOrManualDefined)

  // address mapping for manual address - the address individual lines part
  lazy val parentsManualPartialAddressLinesMapping = PartialManualAddress.mapping
    .verifying(lineOneIsRequiredForParentsAddress, cityIsRequiredForParentsAddress)

  // address mapping for manual address - the address parent wrapper part
  lazy val parentsManualPartialAddressMapping = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(parentsManualPartialAddressLinesMapping)
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
  ).verifying(parentsPostcodeIsValid)

  lazy val parentsPostcodeLookupMapping = mapping(
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
  ).verifying(parentsPostcodeIsValid)

  lazy val parentsPossibleAddressesMapping = mapping(
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

  val parentsLookupAddressForm = ErrorTransformForm(
    mapping (
      keys.parentsAddress.key -> optional(parentsPostcodeLookupMapping)
    ) (
      parentsAddr => InprogressOverseas(
        parentsAddress = parentsAddr
      )
    ) (
      inprogress => Some(inprogress.parentsAddress)
    ).verifying( parentsPostcodeIsNotEmpty )
  )

  val parentsAddressForm = ErrorTransformForm(
    mapping (
      keys.parentsAddress.key -> optional(parentsPartialAddressMapping),
      keys.possibleAddresses.key -> optional(parentsPossibleAddressesMapping)
    ) (
      (parentsAddr, possibleAddr) => InprogressOverseas(
        parentsAddress = parentsAddr,
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.parentsAddress,
        inprogress.possibleAddresses
      )
    ).verifying( parentsAddressIsRequired )
  )

  val parentsManualAddressForm = ErrorTransformForm(
    mapping(
      keys.parentsAddress.key -> optional(parentsManualPartialAddressMapping)
    ) (
      parentsAddr => InprogressOverseas(parentsAddress = parentsAddr)
    ) (
      inprogress => Some(inprogress.parentsAddress)
    ).verifying( parentsManualAddressIsRequired )
  )
}


trait ParentsAddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val parentsManualAddressIsRequired = Constraint[InprogressOverseas](keys.parentsAddress.key) {
    inprogress =>
      inprogress.parentsAddress match {
        case Some(partialAddress) if partialAddress.manualAddress.isDefined => Valid
        case _ => Invalid("Please answer this question", keys.parentsAddress.manualAddress)
      }
  }

  lazy val parentsPostcodeIsNotEmpty = Constraint[InprogressOverseas](keys.parentsAddress.key) {
    inprogress =>
      inprogress.parentsAddress match {
        case Some(partialAddress) if partialAddress.postcode == "" => {
          Invalid("Please enter your postcode", keys.parentsAddress.postcode)
        }
        case None => Invalid("Please enter your postcode", keys.parentsAddress.postcode)
        case _ => Valid
      }
  }

  lazy val parentsAddressIsRequired = Constraint[InprogressOverseas](keys.parentsAddress.key) {
    inprogress =>
      if (inprogress.parentsAddress.isDefined) Valid
      else Invalid("Please answer this question", keys.parentsAddress)
  }

  lazy val parentsUprnOrManualDefined = Constraint[PartialAddress](keys.parentsAddress.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.parentsAddress.uprn,
      keys.parentsAddress.manualAddress,
      keys.parentsAddress
    )
  }

  lazy val parentsPostcodeIsValid = Constraint[PartialAddress](keys.parentsAddress.key) {
    case PartialAddress(_, _, postcode, _, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.parentsAddress.postcode)
  }

  lazy val lineOneIsRequiredForParentsAddress = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(Some(_), _, _, _) => Valid
    case _ => Invalid(atLeastOneLineIsRequiredError, keys.address.manualAddress.lineOne)
  }

  lazy val cityIsRequiredForParentsAddress = Constraint[PartialManualAddress](
    keys.address.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid(cityIsRequiredError, keys.address.manualAddress.city)
  }
}
