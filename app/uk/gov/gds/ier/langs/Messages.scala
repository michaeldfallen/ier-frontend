package uk.gov.gds.ier.langs

import play.api.i18n.Lang
import jsmessages.api.JsMessages
import uk.gov.gds.ier.validation.ErrorTransformForm

object Messages {
  import play.api.Play.current

  private val playErrorPrefix = "error."
  private lazy val messages = play.api.i18n.Messages.messages

  def messagesForLang(lang:Lang) = {
    messages.filterKeys(_ == lang.language).headOption.map(_._2).getOrElse(Map.empty)
  }

  def translatedGlobalErrors[T](
      form: ErrorTransformForm[T]
  )(implicit lang:Lang): Seq[String] = {
    form.globalErrors.map { error =>
      play.api.i18n.Messages(error.message)
    }
  }

  def apply(key: String)(implicit lang: Lang): String = {
    play.api.i18n.Messages(key)
  }

  def apply(key: String, args: Any*)(implicit lang: Lang): String = {
    play.api.i18n.Messages(key, args: _*)
  }

  lazy val jsMessages = JsMessages.filtering(!_.startsWith(playErrorPrefix))
}
