package uk.gov.gds.ier.test

import uk.gov.gds.ier.transaction.forces._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.step.Routes
import play.api.mvc.Call

trait WithMockForcesControllers extends WithForcesControllers {
  private val mockito = new MockitoSugar {}
  import mockito._

  val forces = mock[ForcesControllers]

  val mockAddressFirstStep = mock[address.AddressFirstStep]
  when(forces.AddressFirstStep).thenReturn(mockAddressFirstStep)
  val mockAddressManualStep = mock[address.AddressManualStep]
  when(forces.AddressManualStep).thenReturn(mockAddressManualStep)
  val mockAddressSelectStep = mock[address.AddressSelectStep]
  when(forces.AddressSelectStep).thenReturn(mockAddressSelectStep)
  val mockAddressStep = mock[address.AddressStep]
  when(forces.AddressStep).thenReturn(mockAddressStep)
  val mockPostalVoteStep = mock[applicationFormVote.PostalVoteStep]
  when(forces.PostalVoteStep).thenReturn(mockPostalVoteStep)
  val mockProxyVoteStep = mock[applicationFormVote.ProxyVoteStep]
  when(forces.ProxyVoteStep).thenReturn(mockProxyVoteStep)
  val mockConfirmationStep = mock[confirmation.ConfirmationStep]
  when(forces.ConfirmationStep).thenReturn(mockConfirmationStep)
  val mockContactStep = mock[contact.ContactStep]
  when(forces.ContactStep).thenReturn(mockContactStep)
  val mockContactAddressStep = mock[contactAddress.ContactAddressStep]
  when(forces.ContactAddressStep).thenReturn(mockContactAddressStep)
  val mockDateOfBirthStep = mock[dateOfBirth.DateOfBirthStep]
  when(forces.DateOfBirthStep).thenReturn(mockDateOfBirthStep)
  val mockNameStep = mock[name.NameStep]
  when(forces.NameStep).thenReturn(mockNameStep)
  val mockNationalityStep = mock[nationality.NationalityStep]
  when(forces.NationalityStep).thenReturn(mockNationalityStep)
  val mockNinoStep = mock[nino.NinoStep]
  when(forces.NinoStep).thenReturn(mockNinoStep)
  val mockOpenRegisterStep = mock[openRegister.OpenRegisterStep]
  when(forces.OpenRegisterStep).thenReturn(mockOpenRegisterStep)
  val mockPreviousAddressFirstStep = mock[previousAddress.PreviousAddressFirstStep]
  when(forces.PreviousAddressFirstStep).thenReturn(mockPreviousAddressFirstStep)
  val mockPreviousAddressPostcodeStep = mock[previousAddress.PreviousAddressPostcodeStep]
  when(forces.PreviousAddressPostcodeStep).thenReturn(mockPreviousAddressPostcodeStep)
  val mockPreviousAddressSelectStep = mock[previousAddress.PreviousAddressSelectStep]
  when(forces.PreviousAddressSelectStep).thenReturn(mockPreviousAddressSelectStep)
  val mockPreviousAddressManualStep = mock[previousAddress.PreviousAddressManualStep]
  when(forces.PreviousAddressManualStep).thenReturn(mockPreviousAddressManualStep)
  val mockRankStep = mock[rank.RankStep]
  when(forces.RankStep).thenReturn(mockRankStep)
  val mockServiceStep = mock[service.ServiceStep]
  when(forces.ServiceStep).thenReturn(mockServiceStep)
  val mockStatementStep = mock[statement.StatementStep]
  when(forces.StatementStep).thenReturn(mockStatementStep)
  val mockWaysToVoteStep = mock[waysToVote.WaysToVoteStep]
  when(forces.WaysToVoteStep).thenReturn(mockWaysToVoteStep)

  def routes(url:String) = Routes(
    get = Call("GET", url),
    post = Call("POST", url),
    editGet = Call("GET", url),
    editPost = Call("POST", url)
  )
}

