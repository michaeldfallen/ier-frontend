package uk.gov.gds.ier.test

import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.assets.RemoteAssets
import org.scalatest.mock.MockitoSugar

trait WithMockRemoteAssets
    extends WithRemoteAssets {

  private val mockito = new MockitoSugar {}

  val remoteAssets = mockito.mock[RemoteAssets]

}
