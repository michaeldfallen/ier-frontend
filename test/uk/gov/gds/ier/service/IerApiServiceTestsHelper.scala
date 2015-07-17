package uk.gov.gds.ier.service

import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.{Address, Fail, ApiResponse}
import uk.gov.gds.ier.service.apiservice.{ConcreteIerApiService, IerApiService}
import uk.gov.gds.ier.client.IerApiClient
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.test.TestHelpers
import org.mockito.Mockito._

trait IerApiServiceTestsHelper extends TestHelpers with MockitoSugar {
  val mockLocateService = mock[LocateService]
  val addressService = new AddressService(mockLocateService, new MockConfig)
  val mockSha = mock[ShaHashProvider]
  val isoService = new IsoCountryService

  when(mockLocateService.lookupAddress("WR26NJ")).thenReturn(List(
    Address(
      lineOne = Some("2 The Cottages"),
      lineTwo = Some("Moseley Road"),
      lineThree = Some("Hallow"),
      city = Some("Worcester"),
      county = Some("Worcestershire"),
      postcode = "WR26NJ",
      uprn = Some("26742666")
    ),
    Address(
      lineOne = Some("Beaumont"),
      lineTwo = Some("Moseley Road"),
      lineThree = Some("Hallow"),
      city = Some("Worcester"),
      county = Some("Worcestershire"),
      postcode = "WR26NJ",
      uprn = Some("26742627")
    )
  ))

  class MockConfig extends Config with TestHelpers {
    override def ierApiUrl = "testUrl"
    override def ierApiToken = "123457890"
  }


  def fakeServiceCall(simulatedResponse: String => ApiResponse): IerApiService = {
    class FakeApiClient extends IerApiClient(new MockConfig) {
      override def post(url: String, content: String, headers: (String, String)*) : ApiResponse = {
        if (url.contains("testUrl")) {
          simulatedResponse(content)
        } else {
          Fail("Bad service URL",0)
        }
      }
    }
    new ConcreteIerApiService(new FakeApiClient, jsonSerialiser,
      new MockConfig, addressService, mockSha, isoService)
  }
}
