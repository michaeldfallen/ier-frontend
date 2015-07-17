package uk.gov.gds.ier.stubs

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{Success, ApiResponse}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.service.apiservice.{EroAuthorityDetails, IerApiApplicationResponse}

@Singleton
class IerStubApiClient @Inject() (
    config: Config,
    serialiser: JsonSerialiser) extends IerApiClient(config) {

  override def post(url:String, content:String,headers: (String, String)*): ApiResponse = {
    if (url.contains("/citizen/application")) {
      Success(serialiser.toJson(
        IerApiApplicationResponse(
          id = Some("5360fe69036424d9ec0a1657"),
          localAuthority = EroAuthorityDetails(
            name = "Local authority name",
            urls = "url1" :: "url2" :: Nil,
            email = Some("some@email.com"),
            phone = Some("0123456789"),
            addressLine1 = Some("line one"),
            addressLine2 = Some("line two"),
            addressLine3 = Some("line three"),
            addressLine4 = Some("line four"),
            postcode = Some("SW112DR")
          )
        )
      ), 0)
    } else {
      super.get(url)
    }
  }
}
