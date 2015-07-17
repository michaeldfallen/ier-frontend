package uk.gov.gds.ier.transaction.forces.nino

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

  it should "error out on empty json" in {
    val js = JsNull

    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please enter your National Insurance number"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("Please enter your National Insurance number"))
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
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please enter your National Insurance number"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("Please enter your National Insurance number"))
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
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Your National Insurance number is not correct"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("Your National Insurance number is not correct"))
      },
      success => fail("Should have errored out")
    )
  }
}
