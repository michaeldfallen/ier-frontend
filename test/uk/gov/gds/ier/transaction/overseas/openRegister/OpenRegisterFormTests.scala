package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.test.FormTestSuite

class OpenRegisterFormTests  
  extends FormTestSuite
  with OpenRegisterForms {

  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> "true"
      )
    )
    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }

  it should "successfully bind (false)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> "false"
      )
    )
    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(false))
      }
    )
  }

  it should "not error with empty json (this is a dark pattern)" in {
    val js = JsNull

    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }

  it should "not error with empty values (this is a dark pattern)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> ""
      )
    )

    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }
}
