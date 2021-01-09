package az.gdg.msauth.security.util;

import az.gdg.msauth.security.model.TokenType;
import az.gdg.msauth.security.model.dto.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    private final Clock clock = DefaultClock.INSTANCE;
    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @Value("${jwt.accessExpiration}")
    private Long accessExpiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Value("${jwt.verifyTokenExpiration}")
    private Long verifyTokenExpiration;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public UserInfo getUserInfoFromToken(String token) {
        logger.info("UtilLog.getUserInfoFromToken.start");
        String userId = getClaimFromToken(token, Claims::getId);
        String mail = getClaimFromToken(token, Claims::getSubject);
        String role = getAllClaimsFromToken(token).get("role").toString();
        String status = getAllClaimsFromToken(token).get("status").toString();
        logger.info("UtilLog.getUserInfoFromToken.stop.success");
        return UserInfo
                .builder()
                .role(role)
                .status(status)
                .userId(userId)
                .mail(mail)
                .build();
    }

    public String getMailFromToken(String token) {
        logger.info("UtilLog.getEmailFromToken.start");
        String mail = getClaimFromToken(token, Claims::getSubject);
        logger.info("UtilLog.getEmailFromToken.start.success");
        return mail;
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        logger.info("UtilLog.getClaimFromToken.start");
        Claims claims = getAllClaimsFromToken(token);
        logger.info("UtilLog.getClaimFromToken.stop.success");
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        logger.info("UtilLog.getAllClaimsFromToken.start");
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username, String userId, String role, String status, TokenType tokenType) {
        logger.info("UtilLog.generateToken.start.username : {}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("status", status);
        claims.put("tokenType", tokenType);
        logger.info("UtilLog.generateToken.stop.success.username : {}", username);
        return doGenerateToken(claims, username, userId, tokenType);
    }

    public String doGenerateToken(Map<String, Object> claims,
                                  String subject, String userId, TokenType tokenType) {
        logger.info("UtilLog.doGenerateToken.start.subject : {}", subject);
        Date createdDate = clock.now();
        Date expirationDate = calculateExpirationDate(createdDate, tokenType);
        logger.info("UtilLog.doGenerateToken.stop.success.subject : {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)//username
                .setId(userId)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // generated token will send via mail to user, then user clicks hiperlink and goes to verify-account endpoint
    public String generateTokenWithEmail(String mail) {
        logger.info("UtilLog.generateTokenWithEmail.start");
        Date createdDate = clock.now();
        Date expiration = new Date(createdDate.getTime() + verifyTokenExpiration * 100);
        logger.info("UtilLog.generateTokenWithEmail.stop.success");
        return Jwts.builder()
                .setSubject(mail)
                .setIssuedAt(createdDate)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Date calculateExpirationDate(Date createdDate, TokenType tokenType) {
        logger.info("UtilLog.calculateExpirationDate.start");
        long expiration = TokenType.ACCESS.equals(tokenType) ? accessExpiration : refreshExpiration;
        return new Date(createdDate.getTime() + expiration * 100);
    }

    public boolean isTokenValid(String token) {
        logger.info("UtilLog.isTokenValid.start");
        if (Objects.isNull(token)) {
            return false;
        }
        logger.info("UtilLog.isTokenValid.stop.success");
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        logger.info("UtilLog.isTokenExpired.start");
        Date expirationDate = getExpirationDateFromToken(token);
        logger.info("UtilLog.isTokenExpired.stop.success");
        return expirationDate.before(clock.now());
    }

    private Date getExpirationDateFromToken(String token) {
        logger.info("UtilLog.getExpirationDateFromToken.start");
        return getClaimFromToken(token, Claims::getExpiration);
    }


}