package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown


trait StatementForms extends StatementConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val statementMapping = mapping(
    keys.crownServant.key -> boolean,
    keys.crownPartner.key -> boolean,
    keys.councilEmployee.key -> boolean,
    keys.councilPartner.key -> boolean
  ) (
    CrownStatement.apply
  ) (
    CrownStatement.unapply
  )

  val statementForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping)
    ) (
      statement => InprogressCrown (statement = statement)
    ) (
      inprogress => Some(inprogress.statement)
    ).verifying(
      atLeastOneStatementSelected,
      partnerCantBeBoth,
      youCantBeBoth
    )
  )
}


trait StatementConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val partnerCantBeBoth = Constraint[InprogressCrown](keys.statement.key) {
    application =>
      application.statement match {
        case Some(CrownStatement(_, true, _, true)) => Invalid(
          "Please select only one of these answers",
          keys.statement.crownPartner,
          keys.statement.councilPartner
        )
        case _ => Valid
      }
  }

  lazy val youCantBeBoth = Constraint[InprogressCrown](keys.statement.key) {
    application =>
      application.statement match {
        case Some(CrownStatement(true, _, true, _)) => Invalid(
          "Please select only one of these answers",
          keys.statement.crownServant,
          keys.statement.councilEmployee
        )
        case _ => Valid
      }
  }

  lazy val atLeastOneStatementSelected = Constraint[InprogressCrown](keys.statement.key) {
    application =>
      application.statement match {
        case None => Invalid("Please answer this question", keys.statement)
        case _ => Valid
    }
  }
}

