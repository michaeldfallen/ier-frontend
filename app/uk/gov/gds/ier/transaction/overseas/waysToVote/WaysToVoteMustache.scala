package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait WaysToVoteMustache extends StepTemplate[InprogressOverseas] {

  val title = "How do you want to vote?"

  case class WaysToVoteModel(
    question: Question,
    byPost: Field,
    byProxy: Field,
    inPerson: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/waysToVote") { (form, post) =>

    implicit val progressForm = form

    WaysToVoteModel(
      question = Question(
        postUrl = post.url,
        number = "",
        title = title,
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
