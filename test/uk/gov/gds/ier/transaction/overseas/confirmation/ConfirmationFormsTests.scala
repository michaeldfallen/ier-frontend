package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class ConfirmationFormTests
  extends FormTestSuite
  with ConfirmationForms {

  behavior of "ConfirmationForms.validateBaseSetRequired"
  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => {
        hasErrors.globalErrorMessages.count(_ == "Base set criteria not met") should be(1)
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOverseas()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        hasErrors.globalErrorMessages.count(_ == "Base set criteria not met") should be(1)
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForms.validateYoungVoter"

  it should "error out on missing young voter fields" in {
    val youngVoter = incompleteYoungApplication

    confirmationForm.fillAndValidate(youngVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "parentsAddress" -> errorMessage,
          "lastRegisteredToVote" -> errorMessage,
          "overseasParentName.parentName" -> errorMessage,
          "overseasParentName.parentPreviousName" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage,
          "passport" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(13)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateNewVoter"

  it should "error out on missing new voter fields" in {
    val newVoter = incompleteNewApplication

    confirmationForm.fillAndValidate(newVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "dob" -> errorMessage,
          "lastUkAddress" -> errorMessage,
          "dateLeftUk" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage,
          "passport" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(12)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateSpecialVoter"

  it should "error out on missing crown voter fields" in {
    val specialVoter = incompleteCrownApplication

    confirmationForm.fillAndValidate(specialVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")

        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "dob" -> errorMessage,
          "lastUkAddress" -> errorMessage,
          "dateLeftSpecial" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage,
          "passport" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(12)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing council voter fields" in {
    val specialVoter = incompleteCouncilApplication

    confirmationForm.fillAndValidate(specialVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")

        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "dob" -> errorMessage,
          "lastUkAddress" -> errorMessage,
          "dateLeftSpecial" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage,
          "passport" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(12)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing force voter fields" in {
    val specialVoter = incompleteForcesApplication

    confirmationForm.fillAndValidate(specialVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")

        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "dob" -> errorMessage,
          "lastUkAddress" -> errorMessage,
          "dateLeftSpecial" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage,
          "passport" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(12)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateRenewerVoter"

  it should "error out on missing new voter fields" in {
    val renewerVoter = incompleteRenewerApplication

    confirmationForm.fillAndValidate(renewerVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")

        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name" -> errorMessage,
          "previousName" -> errorMessage,
          "dob" -> errorMessage,
          "dateLeftUk" -> errorMessage,
          "NINO" -> errorMessage,
          "overseasAddress" -> errorMessage,
          "lastUkAddress" -> errorMessage,
          "openRegister" -> errorMessage,
          "waysToVote" -> errorMessage,
          "contact" -> errorMessage
        ))
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(11)
      },
      success => fail("Should have errored out.")
    )
  }
}
