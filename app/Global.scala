import uk.gov.gds.ier.client._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.feedback.{FeedbackClientImpl, FeedbackClient}
import uk.gov.gds.ier.filter.{AssetsCacheFilter, ResultFilter, StatsdFilter}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.service.apiservice.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.stubs.{FeedbackStubClient, LocateStubApiClient, IerStubApiClient, IerApiServiceWithStripNino}
import play.api.mvc._


object Global extends DynamicGlobal with Logging {

  override def bindings = {
    binder =>
      val config = new Config
      if (config.fakeIer) {
        logger.debug("Binding IerStubApiClient")
        binder.bind(classOf[IerApiClient]).to(classOf[IerStubApiClient])
      }
      if (config.fakeLocate) {
        logger.debug("Binding LocateStubApiClient")
        binder.bind(classOf[LocateApiClient]).to(classOf[LocateStubApiClient])
      }
      if (config.stripNino) {
        logger.debug("Binding IerApiServiceWithStripNino")
        binder.bind(classOf[IerApiService]).to(classOf[IerApiServiceWithStripNino])
      } else {
        logger.debug("Binding ConcreteIerApiService")
        binder.bind(classOf[IerApiService]).to(classOf[ConcreteIerApiService])
      }

      if (config.fakeFeedbackService) {
        logger.debug("Binding FeedbackStubClient")
        binder.bind(classOf[FeedbackClient]).to(classOf[FeedbackStubClient])
      } else {
        logger.debug("Binding FeedbackClientImpl")
        binder.bind(classOf[FeedbackClient]).to(classOf[FeedbackClientImpl])
      }
  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), StatsdFilter, ResultFilter, new AssetsCacheFilter(remoteAssets))
  }
}
