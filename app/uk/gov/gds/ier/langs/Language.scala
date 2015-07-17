package uk.gov.gds.ier.langs

import play.api.mvc.Request

object Language {
  val Lang = play.api.i18n.Lang
  type Lang = play.api.i18n.Lang
  def english = Lang("en-GB")

  def getLang(request:Request[Any]):Lang = {
    request.acceptLanguages.headOption.getOrElse(english)
  }
}
