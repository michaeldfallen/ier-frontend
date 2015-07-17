package uk.gov.gds.ier.model

import scala.util.Try

case class WaysToVote (waysToVoteType: WaysToVoteType)

sealed case class WaysToVoteType(name:String)

object WaysToVoteType {
  val InPerson = WaysToVoteType("in-person")
  val ByPost = WaysToVoteType("by-post")
  val ByProxy = WaysToVoteType("by-proxy")

  def parse(str: String) = {
    str match {
      case "in-person" => InPerson
      case "by-proxy" => ByProxy
      case "by-post" => ByPost
    }
  }
  def isValid(str: String) = {
    Try{ parse(str) }.isSuccess
  }
}

