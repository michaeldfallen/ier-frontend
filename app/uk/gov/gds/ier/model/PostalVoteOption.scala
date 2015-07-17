package uk.gov.gds.ier.model

sealed case class PostalVoteOption(postalVote:Boolean, name:String, apiValue:Boolean)

object PostalVoteOption extends ModelMapping {
  import playMappings._

  val Yes = PostalVoteOption(true, "yes", true)
  val NoAndVoteInPerson = PostalVoteOption(false, "no-vote-in-person", false)
  val NoAndAlreadyHave = PostalVoteOption(false, "no-already-have", true)

  val Unknown = PostalVoteOption(false, "unknown", false)

  def isValid(str:String):Boolean = {
    str match {
      case
        Yes.`name` |
        NoAndVoteInPerson.`name`|
        NoAndAlreadyHave.`name` |
        Unknown.`name` => true
      case _ => false
    }
  }

  def parse(str:String):PostalVoteOption = {
    str match {
      case Yes.`name` => Yes
      case NoAndVoteInPerson.`name` => NoAndVoteInPerson
      case NoAndAlreadyHave.`name` => NoAndAlreadyHave
      case _ => Unknown
    }
  }

  lazy val mapping = text.verifying(
    str => PostalVoteOption.isValid(str)
  ).transform[PostalVoteOption](
      str => PostalVoteOption.parse(str),
      option => option.name
    ).verifying(
      allPossibleMoveOptions
    )

  lazy val allPossibleMoveOptions = Constraint[PostalVoteOption]("postalVote") {
    case PostalVoteOption.Yes => Valid
    case PostalVoteOption.NoAndVoteInPerson => Valid
    case PostalVoteOption.NoAndAlreadyHave => Valid

    case _ => Invalid("Not a valid option")
  }
}
