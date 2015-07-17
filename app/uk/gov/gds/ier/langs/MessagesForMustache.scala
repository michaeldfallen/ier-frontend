package uk.gov.gds.ier.langs

import play.api.i18n.Lang

trait MessagesForMustache {
  val lang: Lang
  lazy val messages = Messages.messagesForLang(lang)
}
