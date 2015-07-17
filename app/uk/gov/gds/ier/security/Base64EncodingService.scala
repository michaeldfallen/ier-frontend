package uk.gov.gds.ier.security


class Base64EncodingService {

  import org.apache.commons.codec.binary.Base64

  def encode(input: Array[Byte]): String = Base64.encodeBase64String(input)
  def decode(input: String): Array[Byte] = Base64.decodeBase64(input)
}
