package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class OpenRegisterMustacheTests
  extends MustacheTestSuite
  with OpenRegisterForms
  with OpenRegisterMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = openRegisterForm
    val openRegisterModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/open-register"),
      InprogressForces()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
      "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")

    openRegisterModel.openRegister.value should be("false")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (true)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressForces(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/open-register"),
      InprogressForces()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
      "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (false)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressForces(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/open-register"),
      InprogressForces()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
      "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}
