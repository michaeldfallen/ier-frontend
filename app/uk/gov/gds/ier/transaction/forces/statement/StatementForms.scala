package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces


trait StatementForms extends StatementConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val statementMapping = mapping(
    keys.forcesMember.key -> optional(boolean),
    keys.partnerForcesMember.key -> optional(boolean)
  ) (
    Statement.apply
  ) (
    Statement.unapply
  )

  val statementForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping)
    ) (
      statement => InprogressForces(statement = statement)
    ) (
      inprogress => Some(inprogress.statement)
    ).verifying (atLeastOneStatementSelected)
  )
}


trait StatementConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val atLeastOneStatementSelected = Constraint[InprogressForces](keys.statement.key) {
    application =>
      application.statement match {
        case None => Invalid("Please answer this question", keys.statement)
        case _ => Valid
    }
  }
}

