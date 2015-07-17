package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.forces.InprogressForces

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
      openRegister => InprogressForces(openRegisterOptin = Some(openRegister))
    ) (
      inprogress => inprogress.openRegisterOptin
    )
  )
}

