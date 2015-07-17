package uk.gov.gds.ier.transaction.ordinary.openRegister

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class OpenRegisterMustacheTests
  extends MustacheTestSuite
  with OpenRegisterForms
  with OpenRegisterMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = openRegisterForm
    val openRegisterModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/foo"),
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/foo")

    openRegisterModel.openRegister.value should be("false")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (true)" in runningApp {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo"),
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/foo")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (false)" in runningApp {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo"),
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/foo")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}
