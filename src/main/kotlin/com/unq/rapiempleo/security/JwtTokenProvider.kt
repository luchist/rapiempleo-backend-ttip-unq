package com.unq.rapiempleo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component
import java.util.Date


@Component
class JwtTokenProvider {

    private val SECRET: String = "BANRASRLIAPOPPARHELHAPWORAFOWPASTTESMORICAEAM"
    private val SECRET_KEY = Keys.hmacShaKeyFor(SECRET.toByteArray())


    fun generateToken(email: String): String {
        val now = Date()
        val expiry = Date(System.currentTimeMillis() + (1000 * 60 * 120))

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getEmail(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
/*
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
*/
    // 🔍 Extract username
    fun extractUsername(token: String): String? {
        return extractAllClaims(token).subject
    }

    /* 🔍 Extract user ID
    fun extractUserId(token: String): Long {
        return extractAllClaims(token)["id"].toString().toLong()
    }
    */
    // 🔍 Validate token
    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = extractAllClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .body
    }

}

/*
    fun getAllClaims(token: String?): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(SECRET.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .getBody()
    }

    fun <T> getClaim(token: String?, claimsResolver: Function<Claims?, T?>): T? {
        val claims = getAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun getExpirationDate(token: String?): Date? {
        return getClaim<T?>(token, Function { obj: Claims? -> obj!!.getExpiration() })
    }

    fun isTokenExpired(token: String?): Boolean {
        return getExpirationDate(token).before(Date())
    }

 */