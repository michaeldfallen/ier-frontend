package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait StatementMustache extends StepTemplate[InprogressCrown] {

  case class StatementModel(
      question: Question,
      crown: Field,
      crownServant: Field,
      crownPartner: Field,
      council: Field,
      councilEmployee: Field,
      councilPartner: Field
  ) extends MustacheData

  val title = "Which of these statements applies to you?"

  val mustache = MustacheTemplate("crown/statement") { (form, post) =>
    implicit val progressForm = form

    StatementModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map { _.message },
        title = title
      ),
      crown = Field(
        id = "crown" + keys.statement.key,
        classes = if (form(keys.statement).hasErrors) "invalid" else ""
      ),
      crownServant = CheckboxField(keys.statement.crownServant, "true"),
      crownPartner = CheckboxField(keys.statement.crownPartner, "true"),
      council = Field(
        id = "council" + keys.statement.key,
        classes = if (form(keys.statement).hasErrors) "invalid" else ""
      ),
      councilEmployee = CheckboxField(keys.statement.councilEmployee, "true"),
      councilPartner = CheckboxField(keys.statement.councilPartner, "true")
    )
  }
}

