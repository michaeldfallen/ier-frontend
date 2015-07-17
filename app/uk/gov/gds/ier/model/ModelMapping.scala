package uk.gov.gds.ier.model

import uk.gov.gds.ier.validation.FormKeys

trait ModelMapping extends FormKeys {
  private[model] lazy val playMappings = play.api.data.Forms

  private[model] lazy val Invalid = play.api.data.validation.Invalid
  private[model] lazy val Valid = play.api.data.validation.Valid
  private[model] lazy val Constraint = play.api.data.validation.Constraint

}
