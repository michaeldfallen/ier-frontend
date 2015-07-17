package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.test.FormTestSuite

class StatementFormTests
  extends FormTestSuite
  with StatementForms {

  it should "successfully bind flag of member to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.crownServant" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val Some(statement) = success.statement
        statement.crownServant should be(true)
        statement.crownPartner should be(false)
        statement.councilEmployee should be(false)
        statement.councilPartner should be(false)
      }
    )
  }

  it should "successfully bind flag of partner to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.crownPartner" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val Some(statement) = success.statement
        statement.crownServant should be(false)
        statement.crownPartner should be(true)
        statement.councilEmployee should be(false)
        statement.councilPartner should be(false)
      }
    )
  }

  it should "successfully bind member and partner to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.crownServant" -> "true",
        "statement.councilPartner" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val Some(statement) = success.statement
        statement.crownServant should be(true)
        statement.crownPartner should be(false)
        statement.councilEmployee should be(false)
        statement.councilPartner should be(true)
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("statement") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on two me answers selected" in {
    val js = Json.toJson(
      Map(
        "statement.crownServant" -> "true",
        "statement.councilEmployee" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.globalErrorMessages should be(
          Seq("Please select only one of these answers")
        )
        hasErrors.errorMessages("statement.crownServant") should be(
          Seq("Please select only one of these answers")
        )
        hasErrors.errorMessages("statement.councilEmployee") should be(
          Seq("Please select only one of these answers")
        )
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on two partner answers selected" in {
    val js = Json.toJson(
      Map(
        "statement.crownPartner" -> "true",
        "statement.councilPartner" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.globalErrorMessages should be(
          Seq("Please select only one of these answers")
        )
        hasErrors.errorMessages("statement.crownPartner") should be(
          Seq("Please select only one of these answers")
        )
        hasErrors.errorMessages("statement.councilPartner") should be(
          Seq("Please select only one of these answers")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "statement.crownServant" -> "",
        "statement.crownPartner" -> "",
        "statement.councilEmployee" -> "",
        "statement.councilPartner" -> ""
      )
    )
    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("statement") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

}
