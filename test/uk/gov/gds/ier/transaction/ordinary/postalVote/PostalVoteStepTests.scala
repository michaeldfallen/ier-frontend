package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{PostalVoteOption, PostalVote, PostalVoteDeliveryMethod}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.MockingTestSuite
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.transaction.ordinary.OrdinaryControllers

class PostalVoteStepTests extends MockingTestSuite {

  it should "reset the delivery method if postval vote is no when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]

    val postalVoteStep = new PostalVoteStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers
    )

    val currentState = completeOrdinaryApplication.copy(postalVote = Some(PostalVote(
      postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
      deliveryMethod = Some(PostalVoteDeliveryMethod(
        deliveryMethod = Some("email"),
        emailAddress = Some("test@test.com")
      ))
    )))


    val (inprogressApp, _) =  postalVoteStep.resetPostalVote.apply(currentState, postalVoteStep)

    inprogressApp.postalVote.isDefined should be(true)

    val Some(postalVoteSUT) = inprogressApp.postalVote
    postalVoteSUT.deliveryMethod should be (None)
    postalVoteSUT.postalVoteOption should be (Some(PostalVoteOption.NoAndVoteInPerson))
  }
}
