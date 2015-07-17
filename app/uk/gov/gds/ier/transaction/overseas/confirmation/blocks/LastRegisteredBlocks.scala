package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.transaction.shared.{BlockError, BlockContent}

trait LastRegisteredToVoteBlocks {
  self: ConfirmationBlock =>

  def lastRegistered = {
    import LastRegisteredType._

    val iWas = "I was last registered as"

    val lastRegisteredContent = form.lastRegisteredType match {
      case Some(Overseas) => BlockContent(s"$iWas an overseas voter")
      case Some(Ordinary) => BlockContent(s"$iWas a UK resident")
      case Some(Forces) => BlockContent(s"$iWas a member of the armed forces")
      case Some(Crown) => BlockContent(s"$iWas a Crown servant")
      case Some(Council) => BlockContent(s"$iWas a British council employee")
      case Some(NotRegistered) => BlockContent("I have never been registered")
      case _ => BlockError(completeThisStepMessage)
    }

    ConfirmationQuestion(
      title = "Last registration",
      editLink = overseas.LastRegisteredToVoteStep.routing.editGet.url,
      changeName = "last registration",
      content = lastRegisteredContent
    )
  }
}
