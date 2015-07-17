package uk.gov.gds.ier.transaction.crown.waysToVote

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait WaysToVoteMustache extends StepTemplate[InprogressCrown] {

  val pageTitle = "How do you want to vote?"

  case class WaysToVoteModel(
    question: Question,
    byPost: Field,
    byProxy: Field,
    inPerson: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/waysToVote") { (form, post) =>
    implicit val progressForm = form

    WaysToVoteModel(
      question = Question(
        postUrl = post.url,
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      byPost = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-post"),
      byProxy = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-proxy"),
      inPerson = RadioField(
        key = keys.waysToVote.wayType,
        value = "in-person")
    )
  }
}

