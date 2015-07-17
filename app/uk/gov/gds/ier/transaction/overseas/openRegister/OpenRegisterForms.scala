package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

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
      openRegister => InprogressOverseas(openRegisterOptin = Some(openRegister))
    ) (
      inprogress => inprogress.openRegisterOptin
    )
  )
}

