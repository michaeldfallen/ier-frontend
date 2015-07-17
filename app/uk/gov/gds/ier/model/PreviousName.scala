package uk.gov.gds.ier.model

case class PreviousName(hasPreviousName: Boolean,
                        hasPreviousNameOption: String,
                        previousName: Option[Name],
                        reason: Option[String] = None) {
  def toApiMap(prefix:String = "p"):Map[String,String] = {
    val previousNameMap =
      if(hasPreviousName) previousName.map(pn =>
        pn.toApiMap(prefix + "fn", prefix + "mn", prefix + "ln") ++
        reason.map(reason => Map("nameChangeReason" -> reason)).getOrElse(Map.empty)
      )
      else None

    previousNameMap.getOrElse(Map.empty)
  }
}

object PreviousName extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.hasPreviousName.key -> boolean,
    keys.hasPreviousNameOption.key -> text,
    keys.previousName.key -> optional(Name.mapping),
    keys.reason.key -> optional(text)
  ) (
    (hasPreviousName, hasPreviousNameOption, name, reason) => {
      if (hasPreviousNameOption.equalsIgnoreCase("true") | hasPreviousNameOption.equalsIgnoreCase("other"))
        PreviousName(
          hasPreviousName = true,
          hasPreviousNameOption = hasPreviousNameOption,
          previousName = name,
          reason = reason)
      else
        PreviousName(
          hasPreviousName = false,
          hasPreviousNameOption = hasPreviousNameOption,
          previousName = None,
          reason = None)
    }
  ) (
    PreviousName.unapply
  )
}

