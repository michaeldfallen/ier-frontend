package uk.gov.gds.ier.test

import uk.gov.gds.ier.transaction.overseas._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.step.Routes
import play.api.mvc.Call

trait WithMockOverseasControllers extends WithOverseasControllers {
  private val mockito = new MockitoSugar {}
  import mockito._

  val overseas = mock[OverseasControllers]

  val mockAddressStep = mock[address.AddressStep]
  when(overseas.AddressStep).thenReturn(mockAddressStep)
  val mockPostalVoteStep = mock[applicationFormVote.PostalVoteStep]
  when(overseas.PostalVoteStep).thenReturn(mockPostalVoteStep)
  val mockProxyVoteStep = mock[applicationFormVote.ProxyVoteStep]
  when(overseas.ProxyVoteStep).thenReturn(mockProxyVoteStep)
  val mockConfirmationStep = mock[confirmation.ConfirmationStep]
  when(overseas.ConfirmationStep).thenReturn(mockConfirmationStep)
  val mockContactStep = mock[contact.ContactStep]
  when(overseas.ContactStep).thenReturn(mockContactStep)
  val mockDateLeftCrownStep = mock[dateLeftSpecial.DateLeftCrownStep]
  when(overseas.DateLeftCrownStep).thenReturn(mockDateLeftCrownStep)
  val mockDateLeftArmyStep = mock[dateLeftSpecial.DateLeftArmyStep]
  when(overseas.DateLeftArmyStep).thenReturn(mockDateLeftArmyStep)
  val mockDateLeftCouncilStep = mock[dateLeftSpecial.DateLeftCouncilStep]
  when(overseas.DateLeftCouncilStep).thenReturn(mockDateLeftCouncilStep)
  val mockDateLeftUkStep = mock[dateLeftUk.DateLeftUkStep]
  when(overseas.DateLeftUkStep).thenReturn(mockDateLeftUkStep)
  val mockDateOfBirthStep = mock[dateOfBirth.DateOfBirthStep]
  when(overseas.DateOfBirthStep).thenReturn(mockDateOfBirthStep)
  val mockLastRegisteredToVoteStep = mock[lastRegisteredToVote.LastRegisteredToVoteStep]
  when(overseas.LastRegisteredToVoteStep).thenReturn(mockLastRegisteredToVoteStep)
  val mockLastUkAddressStep = mock[lastUkAddress.LastUkAddressStep]
  when(overseas.LastUkAddressStep).thenReturn(mockLastUkAddressStep)
  val mockLastUkAddressManualStep = mock[lastUkAddress.LastUkAddressManualStep]
  when(overseas.LastUkAddressManualStep).thenReturn(mockLastUkAddressManualStep)
  val mockLastUkAddressSelectStep = mock[lastUkAddress.LastUkAddressSelectStep]
  when(overseas.LastUkAddressSelectStep).thenReturn(mockLastUkAddressSelectStep)
  val mockNameStep = mock[name.NameStep]
  when(overseas.NameStep).thenReturn(mockNameStep)
  val mockNinoStep = mock[nino.NinoStep]
  when(overseas.NinoStep).thenReturn(mockNinoStep)
  val mockOpenRegisterStep = mock[openRegister.OpenRegisterStep]
  when(overseas.OpenRegisterStep).thenReturn(mockOpenRegisterStep)
  val mockParentNameStep = mock[parentName.ParentNameStep]
  when(overseas.ParentNameStep).thenReturn(mockParentNameStep)
  val mockParentsAddressStep = mock[parentsAddress.ParentsAddressStep]
  when(overseas.ParentsAddressStep).thenReturn(mockParentsAddressStep)
  val mockParentsAddressManualStep = mock[parentsAddress.ParentsAddressManualStep]
  when(overseas.ParentsAddressManualStep).thenReturn(mockParentsAddressManualStep)
  val mockParentsAddressSelectStep = mock[parentsAddress.ParentsAddressSelectStep]
  when(overseas.ParentsAddressSelectStep).thenReturn(mockParentsAddressSelectStep)
  val mockCitizenDetailsStep = mock[passport.CitizenDetailsStep]
  when(overseas.CitizenDetailsStep).thenReturn(mockCitizenDetailsStep)
  val mockPassportCheckStep = mock[passport.PassportCheckStep]
  when(overseas.PassportCheckStep).thenReturn(mockPassportCheckStep)
  val mockPassportDetailsStep = mock[passport.PassportDetailsStep]
  when(overseas.PassportDetailsStep).thenReturn(mockPassportDetailsStep)
  val mockWaysToVoteStep = mock[waysToVote.WaysToVoteStep]
  when(overseas.WaysToVoteStep).thenReturn(mockWaysToVoteStep)

  def routes(url:String) = Routes(
    get = Call("GET", url),
    post = Call("POST", url),
    editGet = Call("GET", url),
    editPost = Call("POST", url)
  )
}
