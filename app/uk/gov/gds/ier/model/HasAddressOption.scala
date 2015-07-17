package uk.gov.gds.ier.model

sealed case class HasAddressOption(
  hasAddress: Boolean,
  name:String)

object HasAddressOption extends ModelMapping {
  import playMappings._

  val YesAndLivingThere = HasAddressOption(true, "yes-living-there")
  val YesAndNotLivingThere = HasAddressOption(true, "yes-not-living-there")
  val No = HasAddressOption(false, "no")

  val Unknown = HasAddressOption(false,"unknown")

  def isValid(str:String):Boolean = {
    str match {
      case
        YesAndLivingThere.`name` |
        YesAndNotLivingThere.`name` |
        No.`name` => true
      case _ => false
    }
  }

  def parse(str:String):HasAddressOption = {
    str match {
      case YesAndLivingThere.`name` => YesAndLivingThere
      case YesAndNotLivingThere.`name` => YesAndNotLivingThere
      case No.`name` => No

      case _ => Unknown
    }
  }

  lazy val mapping = text.verifying(
    str => HasAddressOption.isValid(str)
  ).transform[HasAddressOption](
    str => HasAddressOption.parse(str),
    option => option.name
  ).verifying(
    allPossibleOptions
  )

  lazy val allPossibleOptions = Constraint[HasAddressOption]("hasAddress") {
    case HasAddressOption.YesAndLivingThere => Valid
    case HasAddressOption.YesAndNotLivingThere => Valid
    case HasAddressOption.No => Valid
    case _ => Invalid("Not a valid option")
  }
}
