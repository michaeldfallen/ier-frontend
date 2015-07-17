package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.test.FormTestSuite

class NinoFormTests
  extends FormTestSuite
  with NinoForms {

  it should "successfully bind to a valid nino" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "AB 12 34 56 D"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.nino.isDefined should be(true)
        val nino = success.nino.get
        nino.nino should be(Some("AB 12 34 56 D"))
        nino.noNinoReason should be(None)
      }
    )
  }

  it should "successfully bind to a valid no nino reason" in {
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "Uh, whuh, dunno!"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.nino.isDefined should be(true)
        val nino = success.nino.get
        nino.nino should be(None)
        nino.noNinoReason should be(Some("Uh, whuh, dunno!"))
      }
    )
  }

  it should "error out if no nino reason is over the max length" in {
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "a" * 1001
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(
           Map("NINO.NoNinoReason" -> Seq("ordinary_nino_error_maxLength"))
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(
           Map("NINO.NINO" -> Seq("ordinary_nino_error_noneEntered"))
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "",
        "NINO.NoNinoReason" -> ""
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        Map("NINO.NINO" -> Seq("ordinary_nino_error_noneEntered"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out invalid nino" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "bleurch"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        Map("NINO.NINO" -> Seq("ordinary_nino_error_noneEntered"))
      },
      success => fail("Should have errored out")
    )
  }
}
