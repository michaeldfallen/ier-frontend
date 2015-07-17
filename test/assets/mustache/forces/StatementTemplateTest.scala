package assets.mustache.forces

import uk.gov.gds.ier.transaction.forces.statement.StatementMustache
import uk.gov.gds.ier.test._

class StatementTemplateTest
  extends TemplateTestSuite
  with StatementMustache {

  val data = new StatementModel(
    question = Question(),
    statementFieldSet = FieldSet(
      classes = "statementFieldSetClasses"

    ),
    statementMemberForcesCheckbox = Field (
      id = "statementMemberForcesCheckboxId",
      name = "statementMemberForcesCheckboxName",
      attributes = "foo=\"foo\""
    ),
    statementPartnerForcesCheckbox = Field (
      id = "statementPartnerForcesCheckboxId",
      name = "statementPartnerForcesCheckboxName",
      attributes = "foo=\"foo\""
    )
  )

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val html = Mustache.render("forces/statement", data)
      val doc = Jsoup.parse(html.toString)

      val statementMemberForcesCheckbox = doc.select("input[id=statementMemberForcesCheckboxId]").first()
      statementMemberForcesCheckbox.attr("id") should be("statementMemberForcesCheckboxId")
      statementMemberForcesCheckbox.attr("name") should be("statementMemberForcesCheckboxName")
      statementMemberForcesCheckbox.attr("foo") should include("foo")

      val statementPartnerForcesCheckbox = doc.select("input[id=statementPartnerForcesCheckboxId]").first()
      statementPartnerForcesCheckbox.attr("id") should be("statementPartnerForcesCheckboxId")
      statementPartnerForcesCheckbox.attr("name") should be("statementPartnerForcesCheckboxName")
      statementMemberForcesCheckbox.attr("foo") should include("foo")

    }
  }
}
