package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait OpenRegisterForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val openRegisterOptInMapping = single(
    keys.optIn.key -> default(boolean, true)
  )

  val openRegisterForm = ErrorTransformForm(
    mapping(
      keys.openRegister.key -> default(openRegisterOptInMapping, true)
    ) (
      openRegister => InprogressCrown(openRegisterOptin = Some(openRegister))
    ) (
      inprogress => inprogress.openRegisterOptin
    )
  )
}

