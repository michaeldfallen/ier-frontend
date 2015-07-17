package uk.gov.gds.ier.form

import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key}
import uk.gov.gds.ier.model.PartialManualAddress

trait AddressHelpers extends FormKeys {

  /**
   * Concatenate manual address, using comas, as one line of text or return None.
   * Wrap as Option to allow eventual further chaining.
   * @param form source of data
   * @param manualAddressKey example: keys.lastUkAddress.manualAddress
   */
  def manualAddressToOneLine(
      form: ErrorTransformForm[_],
      manualAddressKey: Key): Option[String] = {
    val maLines = List (
      form(manualAddressKey.lineOne).value,
      form(manualAddressKey.lineTwo).value,
      form(manualAddressKey.lineThree).value,
      form(manualAddressKey.city).value
    ).flatten
    if (maLines == Nil) return None else Some(maLines.mkString(", "))
  }

  def manualAddressToOneLine(
      manualAddress: PartialManualAddress): Option[String] = {
    val maLines = List (
      manualAddress.lineOne,
      manualAddress.lineTwo,
      manualAddress.lineThree,
      manualAddress.city
    ).flatten
    if (maLines == Nil) return None else Some(maLines.mkString(", "))
  }

  /**
   * Check if manual address is defined in form data
   * @param form source of data
   * @param manualAddressKey example: keys.lastUkAddress.manualAddress
   */
  def isManualAddressDefined(form: ErrorTransformForm[_], manualAddressKey: Key): Boolean = {
    form(manualAddressKey.lineOne).value.isDefined |
    form(manualAddressKey.lineTwo).value.isDefined |
    form(manualAddressKey.lineThree).value.isDefined
  }

  /**
   * Example:
   * List(Some("123 High Street"), None, Some("Newtown"), None) -> Some("123 High Street, Newtown")
   * List(None, None, None) -> None
   */
  def concatAddressToOneLine(
      form: ErrorTransformForm[_],
      contactAddressKey: Key): Option[String] = {
    concatListOfOptionalStrings(List(
      form(contactAddressKey.prependNamespace(keys.addressLine1)).value,
      form(contactAddressKey.prependNamespace(keys.addressLine2)).value,
      form(contactAddressKey.prependNamespace(keys.addressLine3)).value,
      form(contactAddressKey.prependNamespace(keys.addressLine4)).value,
      form(contactAddressKey.prependNamespace(keys.addressLine5)).value
    ))
  }

  /**
   * Example:
   * List(Some("123 High Street"), None, Some("Newtown"), None) -> Some("123 High Street, Newtown")
   * List(None, None, None) -> None
   */
  private[form] def concatListOfOptionalStrings(x: List[Option[String]]): Option[String] = {
    x.flatten.reduceLeftOption{(a, b) => a + ", " + b}
  }
}
