package uk.gov.gds.ier.langs

import play.api.mvc._
import com.google.inject.Singleton

@Singleton
class MessagesController extends Controller {

  def all = Action {
    Ok(Messages.jsMessages.all(Some("GOVUK.registerToVote.messages")))
  }

  def forLang(langCode:String) = Action {
    implicit val lang = Language.Lang(langCode)
    Ok(Messages.jsMessages(Some("GOVUK.registerToVote.messages")))
  }
}
