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

class AddressSelectStepMockedTests
  extends MockingTestSuite
  with WithMockForcesControllers {

  it should "clear the manual address if an address is selected" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedAddressService = mock[AddressService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val partialAddress = PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ",
        Some(PartialManualAddress(Some("line1"), Some("line2"), Some("line3"), Some("city"))))

    val currentState = completeForcesApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(partialAddress)
      ))
    )
    val addressSelectStep = new AddressSelectStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedAddressService,
      mockedRemoteAssets,
      forces
    )

    when (mockedAddressService.fillAddressLine(partialAddress)).thenReturn(partialAddress)

    val result = addressSelectStep.fillInAddressAndCleanManualAddress(currentState)
    val expected = result.address.exists{addr =>
      addr.address.exists(_.manualAddress == None)
    }

    expected should be (true)

  }
}
