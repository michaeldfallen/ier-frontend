package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.controller.routes.ExitController
import uk.gov.gds.ier.step.Step
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

class AddressFirstStepMockedTests
  extends MockingTestSuite
  with WithMockForcesControllers {

  it should "clear the previous address if answer no to 'have uk address'" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val currentState = completeForcesApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"),
          "WR26NJ",
          None
        ))
      )),
      previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None))
    )
    val addressFirstStep = new AddressFirstStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets,
      forces
    )

    val result = addressFirstStep.clearPreviousAddress(currentState)

    result.previousAddress.isDefined should be (false)
  }
}
