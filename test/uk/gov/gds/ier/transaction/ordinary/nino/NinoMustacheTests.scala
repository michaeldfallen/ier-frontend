package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.test._
import play.api.libs.json.Json

class NinoMustacheTests
  extends MustacheTestSuite
  with NinoForms
  with NinoMustache {

  it should "successfully render to a valid nino" in runningApp {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "AB 12 34 56 D"
      )
    )
    val newNinoForm = ninoForm.bind(js)
    val model = mustache.data(
      newNinoForm,
      Call("POST", "/register-to-vote/nino"),
      InprogressOrdinary()
    ).asInstanceOf[NinoModel]
    model.nino.value should be("AB 12 34 56 D")
  }

  it should "successfully render to a valid 'no nino reason'" in runningApp{
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "Don't have any NINO"
      )
    )
    val newNinoForm = ninoForm.bind(js)
    val model = mustache.data(
        newNinoForm,
        Call("POST", "/register-to-vote/nino"),
        InprogressOrdinary()
    ).asInstanceOf[NinoModel]
    model.noNinoReason.value should be("Don't have any NINO")
  }
}
