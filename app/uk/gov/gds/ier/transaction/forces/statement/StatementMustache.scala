package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait StatementMustache extends StepTemplate[InprogressForces] {

  case class StatementModel (
      question:Question,
      statementFieldSet: FieldSet,
      statementMemberForcesCheckbox: Field,
      statementPartnerForcesCheckbox: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/statement") { (form, postEndpoint) =>
    implicit val progressForm = form

    val title = "Which of these statements applies to you?"
    StatementModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      statementFieldSet = FieldSet(
        classes = if (progressForm(keys.contact).hasErrors) "invalid" else ""
      ),
      statementMemberForcesCheckbox = CheckboxField(
        key = keys.statement.forcesMember, value = "true"
      ),
      statementPartnerForcesCheckbox = CheckboxField(
        key = keys.statement.partnerForcesMember, value = "true"
      )
    )
  }
}
