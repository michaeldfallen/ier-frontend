package uk.gov.gds.ier.transaction.crown.address

import play.api.data.Forms._
import uk.gov.gds.ier.model.{PartialAddress, LastAddress}
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait AddressFirstForms
  extends AddressFirstConstraints
  with CommonForms{
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val addressFirstMapping = mapping (
    keys.hasAddress.key -> optional(hasAddressMapping),
    keys.address.key -> optional(PartialAddress.mapping)
  ) (
    LastAddress.apply
  ) (
    LastAddress.unapply
  )


  val addressFirstForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(addressFirstMapping)
    ) (
      address => InprogressCrown(
        address = address
      )
    ) (
      inprogress => Some(inprogress.address)
    ).verifying(addressYesLivingThereOrNotButNotEmpty)
  )
}

trait AddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val addressYesLivingThereOrNotButNotEmpty = Constraint[InprogressCrown](keys.address.hasAddress.key) {
    inprogress => inprogress.address match {
      case Some(LastAddress(Some(_), _)) => {
        Valid
      }
      case _ => {
        Invalid("Please answer this question", keys.address.hasAddress)
      }
    }
  }
}
