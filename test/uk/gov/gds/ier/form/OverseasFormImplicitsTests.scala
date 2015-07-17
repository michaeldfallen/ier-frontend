package uk.gov.gds.ier.form

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class OverseasFormImplicitsTests extends UnitTestSuite {

  val implicits = new OverseasFormImplicits with FormKeys {}
  import implicits._
  val forms = new ConfirmationForms {
    val serialiser = jsonSerialiser
  }

  behavior of "OverseasFormImplicits.InprogressOverseas.identifyApplication"
  it should "properly identify a young voter" in {
    val youngVoter = incompleteYoungApplication
    youngVoter.identifyApplication should be(ApplicationType.YoungVoter)
  }

  it should "properly identify a new voter whose been registered in uk" in {
    val newVoter = incompleteNewApplication
    newVoter.identifyApplication should be(ApplicationType.NewVoter)
  }

  it should "properly identify a renewer voter" in {
    val renewerVoter = incompleteRenewerApplication
    renewerVoter.identifyApplication should be(ApplicationType.RenewerVoter)
  }

  it should "properly identify a forces voter" in {
    val specialVoter = incompleteForcesApplication
    specialVoter.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a crown voter" in {
    val specialVoter = incompleteCrownApplication
    specialVoter.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a council voter" in {
    val specialVoter = incompleteCouncilApplication
    specialVoter.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a DontKnow case" in {
    val dunno = InprogressOverseas()
    dunno.identifyApplication should be(ApplicationType.DontKnow)
  }

  behavior of "OverseasFormImplicits.InProgressForm[InprogressOverseas].identifyApplication"
  it should "properly identify a young voter" in {
    val youngVoterForm = forms.confirmationForm.fillAndValidate(incompleteYoungApplication)
    youngVoterForm.identifyApplication should be(ApplicationType.YoungVoter)
  }

  it should "properly identify a new voter whose been registered in uk" in {
    val newVoterForm = forms.confirmationForm.fillAndValidate(incompleteNewApplication)
    newVoterForm.identifyApplication should be(ApplicationType.NewVoter)
  }

  it should "properly identify a renewer voter" in {
    val renewerVoterForm = forms.confirmationForm.fillAndValidate(incompleteRenewerApplication)
    renewerVoterForm.identifyApplication should be(ApplicationType.RenewerVoter)
  }

  it should "properly identify a forces voter" in {
    val specialVoterForm = forms.confirmationForm.fillAndValidate(incompleteForcesApplication)
    specialVoterForm.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a council voter" in {
    val specialVoterForm = forms.confirmationForm.fillAndValidate(incompleteCouncilApplication)
    specialVoterForm.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a crown voter" in {
    val specialVoterForm = forms.confirmationForm.fillAndValidate(incompleteCrownApplication)
    specialVoterForm.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a DontKnow case" in {
    val dunnoForm = forms.confirmationForm.fillAndValidate(InprogressOverseas())
    dunnoForm.identifyApplication should be(ApplicationType.DontKnow)
  }
}
