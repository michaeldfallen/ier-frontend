package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.WaysToVoteType._
import uk.gov.gds.ier.model.WaysToVoteType

trait WaysToVoteBlocks {
  self: ConfirmationBlock =>

  def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map(WaysToVoteType.parse(_))
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "a postal vote"
        case Some(WaysToVoteType.ByProxy) => "a proxy vote"
        case _ => "an"
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
      val emailMe = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
      val optIn = form(keys.postalOrProxyVote.optIn).value
      val ways = way match {
        case Some(WaysToVoteType.ByPost) => List("I want to vote by post")
        case Some(WaysToVoteType.ByProxy) => List("I want to vote by proxy (someone else voting for me)")
        case Some(WaysToVoteType.InPerson) => List("I want to vote in person, at a polling station")
        case _ => List()
      }
      val postalOrProxyVote = (optIn, emailMe) match {
        case (Some("true"), true) => List("Send an application form to:", myEmail)
        case (Some("true"), false) => List("Send me an application form in the post")
        case (Some("false"), _) => List(s"I do not need ${prettyWayName} application form")
        case (_, _) => List()
      }

      ConfirmationQuestion(
        title = "Voting options",
        editLink = overseas.WaysToVoteStep.routing.editGet.url,
        changeName = "voting",
        content = ifComplete(keys.waysToVote) {
          ways ++ postalOrProxyVote
        }
      )
  }
}

