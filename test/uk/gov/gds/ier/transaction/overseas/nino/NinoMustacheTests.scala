package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class NinoMustacheTests
  extends MustacheTestSuite
  with NinoForms
  with NinoMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = ninoForm
    val ninoModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/overseas/nino"),
      InprogressOverseas()
    ).asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")

    ninoModel.nino.value should be("")
    ninoModel.noNinoReason.value should be("")
  }

  it should "progress form with filled applicant nino should produce Mustache Model with nino values present" in {
    val partiallyFilledApplicationForm = ninoForm.fill(InprogressOverseas(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/nino"),
      InprogressOverseas()
    ).asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")

    ninoModel.nino.value should be("AB123456C")
    ninoModel.noNinoReason.value should be("")
  }

  it should "progress form with filled applicant no nino reason should produce Mustache Model with no nino reason values present" in {
    val partiallyFilledApplicationForm = ninoForm.fill(InprogressOverseas(
      nino = Some(Nino(
        nino = None,
        noNinoReason = Some("dunno!")))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/nino"),
      InprogressOverseas()
    ).asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")

    ninoModel.nino.value should be("")
    ninoModel.noNinoReason.value should be("dunno!")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationForm = ninoForm.fillAndValidate(InprogressOverseas(
      nino = Some(Nino(
        nino = Some("ABCDE"),
        noNinoReason = None))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/nino"),
      InprogressOverseas()
    ).asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")

    ninoModel.nino.value should be("ABCDE")
    ninoModel.noNinoReason.value should be("")

    ninoModel.question.errorMessages.mkString(", ") should be("Your National Insurance number is not correct")
  }
}
