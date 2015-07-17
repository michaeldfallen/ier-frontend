package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.OtherAddress
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OtherAddressForms {
  self:  FormKeys
    with ErrorMessages =>

  val otherAddressForm = ErrorTransformForm(
    mapping(
      keys.otherAddress.key -> optional(OtherAddress.otherAddressMapping)
    ) (
      otherAddress => InprogressOrdinary(otherAddress = otherAddress)
    ) (
      inprogress => Some(inprogress.otherAddress)
    ).verifying(
      OtherAddress.atLeastOneContactOptionSelected
    )
  )
}
