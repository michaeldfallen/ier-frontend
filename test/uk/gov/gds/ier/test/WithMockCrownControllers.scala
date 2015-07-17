package uk.gov.gds.ier.test

import uk.gov.gds.ier.transaction.crown._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.step.Routes
import play.api.mvc.Call

trait WithMockCrownControllers extends WithCrownControllers {
  private val mockito = new MockitoSugar {}
  import mockito._

  val crown = mock[CrownControllers]

  val mockAddressFirstStep = mock[address.AddressFirstStep]
  when(crown.AddressFirstStep).thenReturn(mockAddressFirstStep)
  val mockAddressManualStep = mock[address.AddressManualStep]
  when(crown.AddressManualStep).thenReturn(mockAddressManualStep)
  val mockAddressSelectStep = mock[address.AddressSelectStep]
  when(crown.AddressSelectStep).thenReturn(mockAddressSelectStep)
  val mockAddressStep = mock[address.AddressStep]
  when(crown.AddressStep).thenReturn(mockAddressStep)
  val mockPostalVoteStep = mock[applicationFormVote.PostalVoteStep]
  when(crown.PostalVoteStep).thenReturn(mockPostalVoteStep)
  val mockProxyVoteStep = mock[applicationFormVote.ProxyVoteStep]
  when(crown.ProxyVoteStep).thenReturn(mockProxyVoteStep)
  val mockConfirmationStep = mock[confirmation.ConfirmationStep]
  when(crown.ConfirmationStep).thenReturn(mockConfirmationStep)
  val mockContactStep = mock[contact.ContactStep]
  when(crown.ContactStep).thenReturn(mockContactStep)
  val mockContactAddressStep = mock[contactAddress.ContactAddressStep]
  when(crown.ContactAddressStep).thenReturn(mockContactAddressStep)
  val mockDateOfBirthStep = mock[dateOfBirth.DateOfBirthStep]
  when(crown.DateOfBirthStep).thenReturn(mockDateOfBirthStep)
  val mockDeclarationPdfStep = mock[declaration.DeclarationPdfStep]
  when(crown.DeclarationPdfStep).thenReturn(mockDeclarationPdfStep)
  val mockJobStep = mock[job.JobStep]
  when(crown.JobStep).thenReturn(mockJobStep)
  val mockNameStep = mock[name.NameStep]
  when(crown.NameStep).thenReturn(mockNameStep)
  val mockNationalityStep = mock[nationality.NationalityStep]
  when(crown.NationalityStep).thenReturn(mockNationalityStep)
  val mockNinoStep = mock[nino.NinoStep]
  when(crown.NinoStep).thenReturn(mockNinoStep)
  val mockOpenRegisterStep = mock[openRegister.OpenRegisterStep]
  when(crown.OpenRegisterStep).thenReturn(mockOpenRegisterStep)
  val mockPreviousAddressFirstStep = mock[previousAddress.PreviousAddressFirstStep]
  when(crown.PreviousAddressFirstStep).thenReturn(mockPreviousAddressFirstStep)
  val mockPreviousAddressPostcodeStep = mock[previousAddress.PreviousAddressPostcodeStep]
  when(crown.PreviousAddressPostcodeStep).thenReturn(mockPreviousAddressPostcodeStep)
  val mockPreviousAddressSelectStep = mock[previousAddress.PreviousAddressSelectStep]
  when(crown.PreviousAddressSelectStep).thenReturn(mockPreviousAddressSelectStep)
  val mockPreviousAddressManualStep = mock[previousAddress.PreviousAddressManualStep]
  when(crown.PreviousAddressManualStep).thenReturn(mockPreviousAddressManualStep)
  val mockStatementStep = mock[statement.StatementStep]
  when(crown.StatementStep).thenReturn(mockStatementStep)
  val mockWaysToVoteStep = mock[waysToVote.WaysToVoteStep]
  when(crown.WaysToVoteStep).thenReturn(mockWaysToVoteStep)

  def routes(url:String) = Routes(
    get = Call("GET", url),
    post = Call("POST", url),
    editGet = Call("GET", url),
    editPost = Call("POST", url)
  )
}
