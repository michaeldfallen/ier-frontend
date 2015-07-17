package uk.gov.gds.ier.test

import uk.gov.gds.ier.transaction.ordinary._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

trait WithMockOrdinaryControllers extends WithOrdinaryControllers {
  private val mockito = new MockitoSugar {}
  import mockito._

  val ordinary = mock[OrdinaryControllers]

  val mockAddressManualStep = mock[address.AddressManualStep]
  when(ordinary.AddressManualStep).thenReturn(mockAddressManualStep)
  val mockAddressStep = mock[address.AddressStep]
  when(ordinary.AddressStep).thenReturn(mockAddressStep)
  val mockAddressSelectStep = mock[address.AddressSelectStep]
  when(ordinary.AddressSelectStep).thenReturn(mockAddressSelectStep)

  val mockContactStep = mock[contact.ContactStep]
  when(ordinary.ContactStep).thenReturn(mockContactStep)
  val mockDateOfBirthStep = mock[dateOfBirth.DateOfBirthStep]
  when(ordinary.DateOfBirthStep).thenReturn(mockDateOfBirthStep)
  val mockNameStep = mock[name.NameStep]
  when(ordinary.NameStep).thenReturn(mockNameStep)
  val mockNationalityStep = mock[nationality.NationalityStep]
  when(ordinary.NationalityStep).thenReturn(mockNationalityStep)
  val mockNinoStep = mock[nino.NinoStep]
  when(ordinary.NinoStep).thenReturn(mockNinoStep)
  val mockOpenRegisterStep = mock[openRegister.OpenRegisterStep]
  when(ordinary.OpenRegisterStep).thenReturn(mockOpenRegisterStep)
  val mockOtherAddressStep = mock[otherAddress.OtherAddressStep]
  when(ordinary.OtherAddressStep).thenReturn(mockOtherAddressStep)
  val mockPostalVoteStep = mock[postalVote.PostalVoteStep]
  when(ordinary.PostalVoteStep).thenReturn(mockPostalVoteStep)

  //Previous Address Steps
  val mockPreviousAddressFirstStep = mock[previousAddress.PreviousAddressFirstStep]
  when(ordinary.PreviousAddressFirstStep).thenReturn(mockPreviousAddressFirstStep)
  val mockPreviousAddressPostcodeStep = mock[previousAddress.PreviousAddressPostcodeStep]
  when(ordinary.PreviousAddressPostcodeStep).thenReturn(mockPreviousAddressPostcodeStep)
  val mockPreviousAddressSelectStep = mock[previousAddress.PreviousAddressSelectStep]
  when(ordinary.PreviousAddressSelectStep).thenReturn(mockPreviousAddressSelectStep)
  val mockPreviousAddressManualStep = mock[previousAddress.PreviousAddressManualStep]
  when(ordinary.PreviousAddressManualStep).thenReturn(mockPreviousAddressManualStep)

  val mockConfirmationStep = mock[confirmation.ConfirmationStep]
  when(ordinary.ConfirmationStep).thenReturn(mockConfirmationStep)
}
