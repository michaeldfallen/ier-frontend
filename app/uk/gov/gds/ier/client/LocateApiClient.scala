package uk.gov.gds.ier.client

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig

class LocateApiClient @Inject() (configuration: Config) extends ApiClient with WithConfig {
  val config = configuration
}