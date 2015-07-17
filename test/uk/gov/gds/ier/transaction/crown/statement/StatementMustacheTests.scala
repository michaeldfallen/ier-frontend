package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.test._

class StatementMustacheTests
  extends MustacheTestSuite
  with StatementMustache
  with StatementForms {

  it should "render without any user input correctly" in {
    val application = statementForm
    val model = mustache.data(
      application,
      Call("POST", "http://postUrl"),
      InprogressCrown()
    ).asInstanceOf[StatementModel]

    model.question.postUrl should be("http://postUrl")
    model.question.errorMessages should be (Seq.empty)
    model.question.title should be("Which of these statements applies to you?")

    model.council.id should be("councilstatement")
    model.council.classes should be("")

    model.crown.id should be("crownstatement")
    model.crown.classes should be("")

    model.councilPartner.id should be("statement_councilPartner")
    model.councilPartner.name should be("statement.councilPartner")
    model.councilPartner.classes should be("")
    model.councilPartner.value should be("true")
    model.councilPartner.attributes should be("")

    model.councilEmployee.id should be("statement_councilEmployee")
    model.councilEmployee.name should be("statement.councilEmployee")
    model.councilEmployee.classes should be("")
    model.councilEmployee.value should be("true")
    model.councilEmployee.attributes should be("")

    model.crownPartner.id should be("statement_crownPartner")
    model.crownPartner.name should be("statement.crownPartner")
    model.crownPartner.classes should be("")
    model.crownPartner.value should be("true")
    model.crownPartner.attributes should be("")

    model.crownServant.id should be("statement_crownServant")
    model.crownServant.name should be("statement.crownServant")
    model.crownServant.classes should be("")
    model.crownServant.value should be("true")
    model.crownServant.attributes should be("")
  }

  it should "mark crown servant checked correctly" in {
    val application = statementForm.bind(
      Map(
        "statement.crownServant" -> "true",
        "statement.crownPartner" -> "false",
        "statement.councilEmployee" -> "false",
        "statement.councilPartner" -> "false"
      )
    )
    val model = mustache.data(
      application,
      Call("POST", "http://postUrl"),
      InprogressCrown()
    ).asInstanceOf[StatementModel]

    model.crownServant.attributes should be("checked=\"checked\"")
    model.crownPartner.attributes should be("")
    model.councilPartner.attributes should be("")
    model.councilEmployee.attributes should be("")
  }

  it should "mark crown partner checked correctly" in {
    val application = statementForm.bind(
      Map(
        "statement.crownServant" -> "false",
        "statement.crownPartner" -> "true",
        "statement.councilEmployee" -> "false",
        "statement.councilPartner" -> "false"
      )
    )
    val model = mustache.data(
      application,
      Call("POST", "http://postUrl"),
      InprogressCrown()
    ).asInstanceOf[StatementModel]

    model.crownServant.attributes should be("")
    model.crownPartner.attributes should be("checked=\"checked\"")
    model.councilPartner.attributes should be("")
    model.councilEmployee.attributes should be("")
  }

  it should "mark council employee checked correctly" in {
    val application = statementForm.bind(
      Map(
        "statement.crownServant" -> "false",
        "statement.crownPartner" -> "false",
        "statement.councilEmployee" -> "true",
        "statement.councilPartner" -> "false"
      )
    )
    val model = mustache.data(
      application,
      Call("POST", "http://postUrl"),
      InprogressCrown()
    ).asInstanceOf[StatementModel]

    model.crownServant.attributes should be("")
    model.crownPartner.attributes should be("")
    model.councilPartner.attributes should be("")
    model.councilEmployee.attributes should be("checked=\"checked\"")
  }

  it should "mark council partner checked correctly" in {
    val application = statementForm.bind(
      Map(
        "statement.crownServant" -> "false",
        "statement.crownPartner" -> "false",
        "statement.councilEmployee" -> "false",
        "statement.councilPartner" -> "true"
      )
    )
    val model = mustache.data(
      application,
      Call("POST", "http://postUrl"),
      InprogressCrown()
    ).asInstanceOf[StatementModel]

    model.crownServant.attributes should be("")
    model.crownPartner.attributes should be("")
    model.councilPartner.attributes should be("checked=\"checked\"")
    model.councilEmployee.attributes should be("")
  }
}
