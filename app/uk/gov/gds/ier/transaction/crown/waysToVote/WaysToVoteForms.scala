package uk.gov.gds.ier.transaction.crown.waysToVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model.{WaysToVoteType, WaysToVote}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait WaysToVoteForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val waysToVoteMapping = mapping(
    keys.wayType.key -> text.verifying("Unknown type", r => WaysToVoteType.isValid(r))
  ) (
    wayToVoteAsString => WaysToVote(WaysToVoteType.parse(wayToVoteAsString))
  ) (
    wayToVoteAsObj => Some(wayToVoteAsObj.waysToVoteType.name)
  )

  val waysToVoteForm = ErrorTransformForm(
    mapping(
      keys.waysToVote.key -> optional(waysToVoteMapping)
        .verifying("Please answer this question", waysToVote => waysToVote.isDefined)
    ) (
      waysToVote => InprogressCrown(waysToVote = waysToVote)
    ) (
      inprogressApplication => Some(inprogressApplication.waysToVote)
    )
  )
}

