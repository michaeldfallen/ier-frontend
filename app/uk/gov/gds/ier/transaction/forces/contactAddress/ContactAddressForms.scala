package uk.gov.gds.ier.transaction.forces.contactAddress

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{Key, ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PossibleContactAddresses, ContactAddress}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait ContactAddressForms extends ContactAddressConstraints {
    self: FormKeys with ErrorMessages =>

  private lazy val contactAddressMapping = mapping(
    keys.country.key -> optional(nonEmptyText),
    keys.postcode.key -> optional(nonEmptyText),
    keys.addressLine1.key -> optional(nonEmptyText),
    keys.addressLine2.key -> optional(nonEmptyText),
    keys.addressLine3.key -> optional(nonEmptyText),
    keys.addressLine4.key -> optional(nonEmptyText),
    keys.addressLine5.key -> optional(nonEmptyText)
  ) (
    ContactAddress.apply
  ) (
    ContactAddress.unapply
  )

  lazy val possibleContactAddressesMapping = mapping (
    keys.contactAddressType.key -> optional(nonEmptyText),
    keys.ukAddressLine.key -> optional(nonEmptyText),
    keys.bfpoContactAddress.key -> optional(contactAddressMapping),
    keys.otherContactAddress.key -> optional(contactAddressMapping)

  ) (
    PossibleContactAddresses.apply
  ) (
    PossibleContactAddresses.unapply
  )

  val contactAddressForm = ErrorTransformForm(
    mapping(
        keys.contactAddress.key -> optional(possibleContactAddressesMapping)
    )(
      contactAddress => InprogressForces(contactAddress = contactAddress)
    )(
      inprogressForces => Some(inprogressForces.contactAddress)
    ).verifying (contactAddressRequired)
  )
}

trait ContactAddressConstraints extends CommonConstraints {
    self: FormKeys
    with ErrorMessages =>

  lazy val contactAddressRequired = Constraint[InprogressForces](keys.contactAddress.key) {
    application =>

      application.contactAddress match {
        case Some(PossibleContactAddresses(contactAddressType,_,bfpoContactAddress,otherContactAddress)) =>
          contactAddressType match {
            case Some("bfpo") => validateBFPOAddressRequired (bfpoContactAddress)
            case Some("other") => validateOtherAddressRequired (otherContactAddress)
            case _ => Valid
          }
        case None => Invalid ("Please answer this question", keys.contactAddress.contactAddressType)
      }
  }

    def validateBFPOAddressRequired (bfpoContactAddress: Option[ContactAddress]) = {
      bfpoContactAddress match {
        case Some(contactAddress) => {

          val addressLine1Key:Option[Key] =
            if (List(contactAddress.addressLine1,
              contactAddress.addressLine2,
              contactAddress.addressLine3,
              contactAddress.addressLine4,
              contactAddress.addressLine5).forall(_.getOrElse("").trim.isEmpty))
              Some(keys.contactAddress.bfpoContactAddress.addressLine1) else None

          val postcodeKey:Option[Key] =
            if (contactAddress.postcode.getOrElse("").trim.isEmpty)
              Some(keys.contactAddress.bfpoContactAddress.postcode) else None

          val errorKeys = List(addressLine1Key, postcodeKey).flatten

          if (errorKeys.size == 0) {
            Valid
          } else {
            Invalid ("Please enter the address", errorKeys:_*)
          }
        }

        case None =>  Invalid (
          "Please enter the address",
          keys.contactAddress.bfpoContactAddress.addressLine1,
          keys.contactAddress.bfpoContactAddress.postcode)
      }
    }

  def validateOtherAddressRequired (otherContactAddress: Option[ContactAddress]) = {
    otherContactAddress match {
      case Some(contactAddress) => {

        val addressLine1Key:Option[Key] =
          if (List(contactAddress.addressLine1,
            contactAddress.addressLine2,
            contactAddress.addressLine3,
            contactAddress.addressLine4,
            contactAddress.addressLine5).forall(_.getOrElse("").trim.isEmpty))
            Some(keys.contactAddress.otherContactAddress.addressLine1) else None

        val countryKey:Option[Key] =
          if (contactAddress.country.getOrElse("").trim.isEmpty)
          Some(keys.contactAddress.otherContactAddress.country) else None

        val errorKeys = List(addressLine1Key, countryKey).flatten

        if (errorKeys.size == 0) {
          Valid
        } else {
          Invalid ("Please enter the address", errorKeys:_*)
        }
      }

      case None =>  Invalid (
        "Please enter the address",
        keys.contactAddress.otherContactAddress.addressLine1,
        keys.contactAddress.otherContactAddress.postcode,
        keys.contactAddress.otherContactAddress.country)
    }
  }
}