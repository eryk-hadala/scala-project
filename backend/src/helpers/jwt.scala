package helpers.jwt

import pdi.jwt.{Jwt, JwtAlgorithm}

object JWT:
  private val secretKey = "secretKey"

  def encode(payload: String): String =
    Jwt.encode(payload, secretKey, JwtAlgorithm.HS384)

  def decode(token: String) =
    Jwt.decode(token, secretKey, JwtAlgorithm.allHmac())


