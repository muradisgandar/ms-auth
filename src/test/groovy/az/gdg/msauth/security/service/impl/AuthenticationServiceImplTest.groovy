package az.gdg.msauth.security.service.impl

import az.gdg.msauth.dao.UserRepository
import az.gdg.msauth.exception.NotFoundException
import az.gdg.msauth.exception.WrongDataException
import az.gdg.msauth.model.entity.UserEntity
import az.gdg.msauth.security.exception.AuthenticationException
import az.gdg.msauth.security.model.Role
import az.gdg.msauth.security.model.Status
import az.gdg.msauth.security.model.dto.JwtAuthenticationRequest
import az.gdg.msauth.security.model.dto.JwtAuthenticationResponse
import az.gdg.msauth.security.model.dto.UserInfo
import az.gdg.msauth.security.service.impl.AuthenticationServiceImpl
import az.gdg.msauth.security.util.TokenUtil
import org.springframework.security.authentication.AuthenticationManager
import spock.lang.Specification
import spock.lang.Title

@Title("Testing for authentication service")
class AuthenticationServiceImplTest extends Specification {


    private TokenUtil tokenUtil
    private UserRepository userRepository
    private AuthenticationManager authenticationManager
    private AuthenticationServiceImpl authenticationServiceImp

    def setup() {
        tokenUtil = Mock()
        userRepository = Mock()
        authenticationManager = Mock()
        authenticationServiceImp = new AuthenticationServiceImpl(tokenUtil, userRepository, authenticationManager)
    }

    def "return userInfo if token is valid"() {
        given:
        def userInfo = new UserInfo()
        userInfo.setUserId("1")
        userInfo.setRole("ROLE_USER")
        userInfo.setMail("admin@mail.ru")
        userInfo.setStatus("CONFIRMED")
        String token = "asdfghjkl"

        when:
        authenticationServiceImp.validateToken(token)

        then:
        1 * tokenUtil.isTokenValid(token) >> true
        tokenUtil.getUserInfoFromToken(token) >> userInfo

    }

    def "don't return userInfo if token is invalid"() {
        given:
        String token = "asdfghjkl"

        when:
        authenticationServiceImp.validateToken(token)

        then:
        1 * tokenUtil.isTokenValid(token) >> false
        tokenUtil.getUserInfoFromToken(token) >> null

    }

    def "don't throw AuthenticationException and generate token if status is CONFIRMED"() {
        given:
        def request = new JwtAuthenticationRequest()
        request.setMail("example.com")
        request.setPassword("1234")
        def entity = new UserEntity()
        entity.setId(1)
        entity.setStatus(Status.CONFIRMED)
        entity.setRole(Role.ROLE_USER)
        def token = "asdfghjklyutryrwrtututu"

        when:
        authenticationServiceImp.createAuthenticationToken(request)

        then:
        1 * userRepository.findByMail(request.getMail()) >> entity
        new JwtAuthenticationResponse(token) >> token
        notThrown(AuthenticationException)

    }

    def "should throw AuthenticationException if status is REGISTERED"() {
        given:
        def request = new JwtAuthenticationRequest()
        request.setMail("example.com")
        request.setPassword("1234")
        def entity = new UserEntity()
        entity.setStatus(Status.REGISTERED)

        when:
        authenticationServiceImp.createAuthenticationToken(request)

        then:
        1 * userRepository.findByMail(request.getMail()) >> entity
        thrown(AuthenticationException)

    }

    def "should throw AuthenticationException if status is BLOCKED"() {
        given:
        def request = new JwtAuthenticationRequest()
        request.setMail("example.com")
        request.setPassword("1234")
        def entity = new UserEntity()
        entity.setStatus(Status.BLOCKED)

        when:
        authenticationServiceImp.createAuthenticationToken(request)

        then:
        1 * userRepository.findByMail(request.getMail()) >> entity
        thrown(AuthenticationException)

    }

    def "should throw NotFoundException if user is not found"() {
        given:
        def request = new JwtAuthenticationRequest()
        request.setMail("example.com")
        request.setPassword("1234")
        def entity = null

        when:
        authenticationServiceImp.createAuthenticationToken(request)

        then:
        1 * userRepository.findByMail(request.getMail()) >> entity
        thrown(NotFoundException)

    }

    def "should throw WrongDataException if username and password are null"() {
        given:
        def username = null
        def password = null
        when:
        authenticationServiceImp.authenticate(username, password)

        then:
        thrown(WrongDataException)

    }


    def "don't throw WrongDataException if password and username are not null"() {
        given:
        def username = "example@mail.ru"
        def password = "12345"

        when:
        authenticationServiceImp.authenticate(username, password)

        then:
        notThrown(WrongDataException)

    }


}
