package uk.gov.gds.ier.serialiser

import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}
import org.joda.time.DateTime
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.module.SimpleModule

class JodaParseModule extends SimpleModule {
  this.addDeserializer(classOf[DateTime], new JodaWithTimeZoneDeserializer())

  class JodaWithTimeZoneDeserializer extends JsonDeserializer[DateTime] {
    def deserialize(parser: JsonParser, context: DeserializationContext): DateTime = {
      val jsonStr = parser.readValueAs(classOf[String])
      DateTime.parse(jsonStr)
    }
  }
}
