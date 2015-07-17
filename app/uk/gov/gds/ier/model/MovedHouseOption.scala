package uk.gov.gds.ier.model

sealed case class MovedHouseOption(hasPreviousAddress:Boolean, name:String)

object MovedHouseOption extends ModelMapping {
  import playMappings._

  val Yes = MovedHouseOption(true, "yes")

  val MovedFromUk = MovedHouseOption(true, "from-uk")
  val MovedFromAbroad = MovedHouseOption(true, "from-abroad")
  val MovedFromAbroadRegistered = MovedHouseOption(true, "from-abroad-registered")
  val MovedFromAbroadNotRegistered = MovedHouseOption(true, "from-abroad-not-registered")

  val NotMoved = MovedHouseOption(false, "no")
  val DontKnow = MovedHouseOption(false, "dunno")

  def isValid(str:String):Boolean = {
    str match {
      case
        Yes.`name` |

        MovedFromUk.`name`|
        MovedFromAbroad.`name`|
        MovedFromAbroadRegistered.`name`|
        MovedFromAbroadNotRegistered.`name` |

        NotMoved.`name` => true

      case _ => false
    }
  }

  def parse(str:String):MovedHouseOption = {
    str match {
      case Yes.`name` => Yes

      case MovedFromUk.`name` => MovedFromUk
      case MovedFromAbroad.`name` => MovedFromAbroad
      case MovedFromAbroadRegistered.`name` => MovedFromAbroadRegistered
      case MovedFromAbroadNotRegistered.`name` => MovedFromAbroadNotRegistered

      case NotMoved.`name` => NotMoved

      case _ => DontKnow
    }
  }

  lazy val mapping = text.verifying(
    str => MovedHouseOption.isValid(str)
  ).transform[MovedHouseOption](
    str => MovedHouseOption.parse(str),
    option => option.name
  ).verifying(
    allPossibleMoveOptions
  )

  lazy val allPossibleMoveOptions = Constraint[MovedHouseOption]("movedHouse") {
    case MovedHouseOption.Yes => Valid

    case MovedHouseOption.MovedFromUk => Valid
    case MovedHouseOption.MovedFromAbroad => Valid
    case MovedHouseOption.MovedFromAbroadRegistered => Valid
    case MovedHouseOption.MovedFromAbroadNotRegistered => Valid

    case MovedHouseOption.NotMoved => Valid

    case _ => Invalid("Not a valid option")
  }
}
