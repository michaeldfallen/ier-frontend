package uk.gov.gds.ier.transaction.forces.dateOfBirth

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.noDOB
import uk.gov.gds.ier.model.DOB

/*
 * This test mock the Date of Birth.
 *
 * The app has to be running for Guice to initialise properly
 *
 */
class DateOfBirthStepMockedTests
  extends MockingTestSuite
  with WithMockForcesControllers {

  it should "clear dob reason if the date of birth is provided" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      forces
    )

    when(mockNameStep.routing).thenReturn(routes("/register-to-vote/name"))

    val currentState = completeForcesApplication.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(1988, 1, 1)),
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to70)
        ))
      ))
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(1988, 1, 1)),
      noDob = None)))
  }
}
