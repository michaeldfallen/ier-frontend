package uk.gov.gds.ier.transaction.overseas

import com.google.inject.Inject
import uk.gov.gds.ier.guice.Injector

class OverseasControllers @Inject()(
    injector: Injector
) {
  lazy val AddressStep = injector.dependency[address.AddressStep]
  lazy val PostalVoteStep = injector.dependency[applicationFormVote.PostalVoteStep]
  lazy val ProxyVoteStep = injector.dependency[applicationFormVote.ProxyVoteStep]
  lazy val ConfirmationStep = injector.dependency[confirmation.ConfirmationStep]
  lazy val ContactStep = injector.dependency[contact.ContactStep]
  lazy val DateLeftCrownStep = injector.dependency[dateLeftSpecial.DateLeftCrownStep]
  lazy val DateLeftArmyStep = injector.dependency[dateLeftSpecial.DateLeftArmyStep]
  lazy val DateLeftCouncilStep = injector.dependency[dateLeftSpecial.DateLeftCouncilStep]
  lazy val DateLeftUkStep = injector.dependency[dateLeftUk.DateLeftUkStep]
  lazy val DateOfBirthStep = injector.dependency[dateOfBirth.DateOfBirthStep]
  lazy val LastRegisteredToVoteStep = injector.dependency[lastRegisteredToVote.LastRegisteredToVoteStep]
  lazy val LastUkAddressStep = injector.dependency[lastUkAddress.LastUkAddressStep]
  lazy val LastUkAddressManualStep = injector.dependency[lastUkAddress.LastUkAddressManualStep]
  lazy val LastUkAddressSelectStep = injector.dependency[lastUkAddress.LastUkAddressSelectStep]
  lazy val NameStep = injector.dependency[name.NameStep]
  lazy val NinoStep = injector.dependency[nino.NinoStep]
  lazy val OpenRegisterStep = injector.dependency[openRegister.OpenRegisterStep]
  lazy val ParentNameStep = injector.dependency[parentName.ParentNameStep]
  lazy val ParentsAddressStep = injector.dependency[parentsAddress.ParentsAddressStep]
  lazy val ParentsAddressManualStep = injector.dependency[parentsAddress.ParentsAddressManualStep]
  lazy val ParentsAddressSelectStep = injector.dependency[parentsAddress.ParentsAddressSelectStep]
  lazy val CitizenDetailsStep = injector.dependency[passport.CitizenDetailsStep]
  lazy val PassportCheckStep = injector.dependency[passport.PassportCheckStep]
  lazy val PassportDetailsStep = injector.dependency[passport.PassportDetailsStep]
  lazy val WaysToVoteStep = injector.dependency[waysToVote.WaysToVoteStep]
}
