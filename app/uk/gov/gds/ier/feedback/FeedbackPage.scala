package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.session.CacheBust
import com.google.inject.Singleton

@Singleton
class FeedbackPage @Inject ()(
    val config: Config,
    val remoteAssets: RemoteAssets,
    val feedbackService: FeedbackService)
  extends Controller
  with FeedbackForm
  with FeedbackMustache
  with Logging
  with WithConfig
  with WithRemoteAssets {

  def get(sourcePath: Option[String]) = CacheBust {
    Action { implicit request =>
      Ok(FeedbackPage(
        postUrl = routes.FeedbackPage.post(sourcePath).url
      ))
    }
  }

  def post(sourcePath: Option[String]) = CacheBust {
    Action { implicit request =>
      feedbackForm.bindFromRequest().value.map{ feedback =>
        feedbackService.submit(feedback, sourcePath)
      }
      Redirect(routes.FeedbackPage.thankYou(sourcePath))
    }
  }

  def thankYou(sourcePath: Option[String]) = CacheBust {
    Action { implicit request =>
      Ok(ThankYouPage(
        sourcePath = sourcePath getOrElse config.ordinaryStartUrl
      ))
    }
  }
}

