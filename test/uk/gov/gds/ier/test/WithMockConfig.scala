package uk.gov.gds.ier.test

import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.config.Config
import org.scalatest.mock.MockitoSugar

trait WithMockConfig
    extends WithConfig {

  private val mockito = new MockitoSugar {}

  val config = mockito.mock[Config]

}
