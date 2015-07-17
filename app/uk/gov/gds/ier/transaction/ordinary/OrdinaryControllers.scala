package uk.gov.gds.ier.transaction.ordinary

import com.google.inject.Inject
import uk.gov.gds.ier.guice.Injector

class OrdinaryControllers @Inject()(
    injector: Injector
) {
  //Address steps
  lazy val AddressManualStep = injector.dependency[address.AddressManualStep]
  lazy val AddressStep = injector.dependency[address.AddressStep]
  lazy val AddressSelectStep = injector.dependency[address.AddressSelectStep]

  lazy val ContactStep = injector.dependency[contact.ContactStep]
  lazy val DateOfBirthStep = injector.dependency[dateOfBirth.DateOfBirthStep]
  lazy val NameStep = injector.dependency[name.NameStep]
  lazy val NationalityStep = injector.dependency[nationality.NationalityStep]
  lazy val NinoStep = injector.dependency[nino.NinoStep]
  lazy val OpenRegisterStep = injector.dependency[openRegister.OpenRegisterStep]
  lazy val OtherAddressStep = injector.dependency[otherAddress.OtherAddressStep]
  lazy val PostalVoteStep = injector.dependency[postalVote.PostalVoteStep]

  //Previous Address Steps
  lazy val PreviousAddressFirstStep = injector.dependency[previousAddress.PreviousAddressFirstStep]
  lazy val PreviousAddressPostcodeStep = injector.dependency[previousAddress.PreviousAddressPostcodeStep]
  lazy val PreviousAddressSelectStep = injector.dependency[previousAddress.PreviousAddressSelectStep]
  lazy val PreviousAddressManualStep = injector.dependency[previousAddress.PreviousAddressManualStep]

  lazy val ConfirmationStep = injector.dependency[confirmation.ConfirmationStep]

}
