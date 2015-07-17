package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.gds.ier.model.HasAddressOption

trait CommonForms {
  self: FormKeys
  with ErrorMessages =>

  lazy val hasAddressMapping = HasAddressOption.mapping
    .verifying(
      hasAddressLivingThereOrNot
    )

  lazy val hasAddressLivingThereOrNot = Constraint[HasAddressOption]("hasAddress") {
    case HasAddressOption.YesAndLivingThere => Valid
    case HasAddressOption.YesAndNotLivingThere => Valid
    case HasAddressOption.No => Valid
    case _ => Invalid("Not a valid option")
  }
}
