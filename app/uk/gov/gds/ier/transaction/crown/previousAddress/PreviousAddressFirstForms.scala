package uk.gov.gds.ier.transaction.crown.previousAddress

import play.api.data.Forms._
import uk.gov.gds.ier.model.{MovedHouseOption, PartialAddress, PartialPreviousAddress}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait PreviousAddressFirstForms
    extends PreviousAddressFirstConstraints
    with CommonForms {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>


  lazy val previousAddressMapping = mapping(
    keys.movedRecently.key -> optional(movedHouseMapping),
    keys.previousAddress.key -> optional(PartialAddress.mapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  val previousAddressFirstForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(previousAddressMapping)
    ) (
      previousAddress => InprogressCrown(
        previousAddress = previousAddress
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying(previousAddressMovedHouseNotEmpty)
  )
}

trait PreviousAddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val previousAddressMovedHouseNotEmpty = Constraint[InprogressCrown](
    keys.previousAddress.movedRecently.key) {
    inprogress => inprogress.previousAddress match {
      case Some(PartialPreviousAddress(Some(_), _)) => Valid
      case _ => Invalid("Please answer this question", keys.previousAddress.movedRecently)
    }
  }
}
