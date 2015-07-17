package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import uk.gov.gds.ier.validation.constraints.overseas.LastRegisteredToVoteConstraints
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait LastRegisteredToVoteForms extends LastRegisteredToVoteConstraints {
  self: FormKeys
  with ErrorMessages =>

  val lastRegisteredToVoteForm = ErrorTransformForm(
    mapping(
      keys.lastRegisteredToVote.key -> optional(LastRegisteredToVote.mapping)
    ) (
      lastRegisteredToVoteObj => InprogressOverseas(
        lastRegisteredToVote = lastRegisteredToVoteObj
      )
    ) (
      inprogress => Some(inprogress.lastRegisteredToVote)
    ).verifying(lastRegisteredToVoteRequired)
  )
}
