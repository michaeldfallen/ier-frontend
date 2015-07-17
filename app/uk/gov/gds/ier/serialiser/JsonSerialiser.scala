package uk.gov.gds.ier.serialiser

import uk.gov.gds.common.json.JsonSerializer
import com.fasterxml.jackson.core.`type`.TypeReference
import java.lang.reflect.{ Type, ParameterizedType }

class JsonSerialiser extends JsonSerializer {
  mapper.registerModule(new JodaParseModule)

  override def toJson(obj: AnyRef) = try {
    mapper.writeValueAsString(obj)
  }
  override def fromJson[A](json: String)(implicit m: Manifest[A]): A = try {
    mapper.readValue(json, typeReference[A])
  }

  private[this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else {
      new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = null
      }
    }
  }
}

