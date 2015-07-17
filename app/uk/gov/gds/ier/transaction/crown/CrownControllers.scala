package uk.gov.gds.ier.transaction.crown

import com.google.inject.Inject
import uk.gov.gds.ier.guice.Injector

class CrownControllers @Inject()(
    injector: Injector
) {
  lazy val AddressFirstStep = injector.dependency[address.AddressFirstStep]
  lazy val AddressManualStep = injector.dependency[address.AddressManualStep]
  lazy val AddressSelectStep = injector.dependency[address.AddressSelectStep]
  lazy val AddressStep = injector.dependency[address.AddressStep]
  lazy val PostalVoteStep = injector.dependency[applicationFormVote.PostalVoteStep]
  lazy val ProxyVoteStep = injector.dependency[applicationFormVote.ProxyVoteStep]
  lazy val ConfirmationStep = injector.dependency[confirmation.ConfirmationStep]
  lazy val ContactStep = injector.dependency[contact.ContactStep]
  lazy val ContactAddressStep = injector.dependency[contactAddress.ContactAddressStep]
  lazy val DateOfBirthStep = injector.dependency[dateOfBirth.DateOfBirthStep]
  lazy val DeclarationPdfStep = injector.dependency[declaration.DeclarationPdfStep]
  lazy val JobStep = injector.dependency[job.JobStep]
  lazy val NameStep = injector.dependency[name.NameStep]
  lazy val NationalityStep = injector.dependency[nationality.NationalityStep]
  lazy val NinoStep = injector.dependency[nino.NinoStep]
  lazy val OpenRegisterStep = injector.dependency[openRegister.OpenRegisterStep]
  lazy val PreviousAddressFirstStep = injector.dependency[previousAddress.PreviousAddressFirstStep]
  lazy val PreviousAddressPostcodeStep = injector.dependency[previousAddress.PreviousAddressPostcodeStep]
  lazy val PreviousAddressSelectStep = injector.dependency[previousAddress.PreviousAddressSelectStep]
  lazy val PreviousAddressManualStep = injector.dependency[previousAddress.PreviousAddressManualStep]
  lazy val StatementStep = injector.dependency[statement.StatementStep]
  lazy val WaysToVoteStep = injector.dependency[waysToVote.WaysToVoteStep]

}
