package helpers

import pdi.jwt.{JwtAlgorithm, JwtClaim, Jwt as _Jwt}
import upickle.default.*

import scala.util.Try

object Jwt:
  private val secretKey = "secretKey"

  def encode(payload: String): String =
    _Jwt.encode(payload, secretKey, JwtAlgorithm.HS384)

  def encode[T: Writer](payload: T): String =
    _Jwt.encode(write(payload), secretKey, JwtAlgorithm.HS384)


  def decode(token: String): Try[JwtClaim] =
    _Jwt.decode(token, secretKey, JwtAlgorithm.allHmac())


