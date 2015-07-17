package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{OverseasParentName, Name, PreviousName}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.assets.RemoteAssets

class ParentNameStepTests
  extends MockingTestSuite
  with WithMockOverseasControllers {

  it should "reset the previous names if the has previous is false when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val parentNameStep = new ParentNameStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      overseas
    )

    val currentState = completeOverseasApplication.copy(overseasParentName = Some(OverseasParentName(
        name = None, previousName =
      Some(PreviousName(false, "false", Some(Name("john", None, "smith")))))))

    val transferedState = parentNameStep.resetParentName.apply(currentState, parentNameStep)
    transferedState._1.overseasParentName.get.previousName.isDefined should be (true)
    transferedState._1.overseasParentName.get.previousName.get.previousName should be (None)
  }
}
