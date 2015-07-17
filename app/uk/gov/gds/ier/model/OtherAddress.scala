package uk.gov.gds.ier.model

import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

case class OtherAddress(otherAddressOption: OtherAddressOption) {
  def toApiMap = {
    Map("oadr" -> otherAddressOption.name)
  }
}

sealed case class OtherAddressOption(hasOtherAddress: Boolean, name: String)

object OtherAddress extends ModelMapping {
  import playMappings._

  val NoOtherAddress = OtherAddressOption(false, "none")
  val StudentOtherAddress = OtherAddressOption(true, "student")
  val HomeOtherAddress = OtherAddressOption(true, "secondHome")

  def parse(str: String): OtherAddressOption = {
    str match {
      case "secondHome" => HomeOtherAddress
      case "student" => StudentOtherAddress
      case _ => NoOtherAddress
    }
  }

  lazy val otherAddressMapping = mapping(
    keys.hasOtherAddress.key -> otherAddressOptionMapping
  ) (
    OtherAddress.apply
  ) (
    OtherAddress.unapply
  )

  lazy val otherAddressOptionMapping = text.verifying(
    otherAddressIsValid
  ).transform[OtherAddressOption](
      str => OtherAddress.parse(str),
      option => option.name
    )

  lazy val atLeastOneContactOptionSelected = Constraint[InprogressOrdinary](keys.otherAddress.key) {
    application =>
      if (application.otherAddress.isDefined) Valid
      else Invalid("ordinary_otheraddr_error_pleaseAnswer", keys.otherAddress)
  }

  lazy val otherAddressIsValid = Constraint[String](keys.otherAddress.key) {
    addressKind =>
      if (
        addressKind == OtherAddress.NoOtherAddress.name ||
          addressKind == OtherAddress.StudentOtherAddress.name ||
          addressKind == OtherAddress.HomeOtherAddress.name
      ) {
        Valid
      } else {
        Invalid(
          // do not translate, it is not supposed to happen normally
          s"${addressKind} is not a valid other address type",
          keys.otherAddress.hasOtherAddress
        )
      }
  }
}


