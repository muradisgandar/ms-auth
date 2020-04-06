package az.gdg.msauth.service

import az.gdg.msauth.dao.UserRepository
import az.gdg.msauth.exception.WrongDataException
import az.gdg.msauth.model.dto.UserDTO
import az.gdg.msauth.model.entity.UserEntity
import az.gdg.msauth.security.exception.AuthenticationException
import az.gdg.msauth.security.model.dto.UserInfo
import az.gdg.msauth.security.service.impl.AuthenticationServiceImpl
import az.gdg.msauth.service.impl.EmailServiceImpl
import az.gdg.msauth.service.impl.UserServiceImpl
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Title

@Title("Testing for user service")
class UserServiceImplTest extends Specification {

    UserRepository userRepository
    AuthenticationServiceImpl authenticationServiceImpl
    EmailServiceImpl emailServiceImpl;
    UserServiceImpl userService

    def setup() {
        userRepository = Mock()
        authenticationServiceImpl = Mock()
        emailServiceImpl = Mock()
        userService = new UserServiceImpl(userRepository, authenticationServiceImpl,emailServiceImpl)
    }

    def "doesn't throw exception in signUp() method if email doesn't exist in database"() {

        given:
            def userDto = new UserDTO()
            def entity = null
            userDto.setEmail("isgandarli_murad@mail.ru")
            userDto.setPassword("pw")
            1 * userRepository.findByEmail(userDto.getEmail()) >> entity

        when: "send dto object to service "
            userService.signUp(userDto)

        then: "duplicate e-mail address exception is not thrown"
            notThrown(WrongDataException)
    }

    def "throw exception in signUp() method if email  exists in database"() {

        given:
            def userDto = new UserDTO()
            def entity = new UserEntity()
            userDto.setEmail("isgandarli_murad@mail.ru")
            1 * userRepository.findByEmail(userDto.getEmail()) >> entity

        when: "send dto object to service "
            userService.signUp(userDto)

        then: "duplicate e-mail address exception is thrown"
            thrown(WrongDataException)
    }

    def "throw exception in getCustomerIdByEmail() method if user's role is not admin"() {
        given:
            def userInfo = new UserInfo("asdfghjkl", "ROLE_USER","CONFIRMED", "1", "user@mail.ru")
            def token = "asdfghjkl"
            def email = "user@mail.ru"

        when:
            userService.getCustomerIdByEmail(token, email)

        then:
            2 * authenticationServiceImpl.validateToken(token) >> userInfo
            authenticationServiceImpl.validateToken(token).getRole().equals("ROLE_ADMIN") >> false

            thrown(AuthenticationException)

    }

    def "don't throw exception in getCustomerIdByEmail() method if user's role is  admin"() {
        given:
            def userInfo = new UserInfo("asdfghjkl", "ROLE_ADMIN","CONFIRMED","1", "admin@mail.ru")
            def entity = new UserEntity(1, null, null, null, null, null, null, null, null,null)
            def token = "asdfghjkl"
            def email = "admin@mail.ru"


        when:
            userService.getCustomerIdByEmail(token, email)

        then:
            2 * authenticationServiceImpl.validateToken(token) >> userInfo
            authenticationServiceImpl.validateToken(token).getRole().equals("ROLE_ADMIN") >> true
            1 * userRepository.findByEmail(email) >> entity
            notThrown(AuthenticationException)
    }

    def "don't throw exception in getCustomerIdByEmail() method if email is found and return user's id"() {
        given:
            def userInfo = new UserInfo("admin@mail.ru", "ROLE_ADMIN","CONFIRMED","1","asdfghjkl" )
            def entity = new UserEntity(1, null, null, null, null, null, null, null, null,null)
            def token = "asdfghjkl"
            def email = "admin@mail.ru"


        when:
            userService.getCustomerIdByEmail(token, email)

        then:
            1 * authenticationServiceImpl.validateToken(token) >> userInfo
            2 * userRepository.findByEmail(email) >> entity
            userRepository.findByEmail(email).getId().toString() >> "1"
            notThrown(WrongDataException)
    }

    def "throw exception in getCustomerIdByEmail() method if email is not found"() {
        given:
            def userInfo = new UserInfo("asdfghjkl", "ROLE_ADMIN","CONFIRMED","1", "admin@mail.ru")
            def entity = null
            def token = "asdfghjkl"
            def email = "admin@mail.ru"


        when:
            userService.getCustomerIdByEmail(token, email)

        then:
            1 * authenticationServiceImpl.validateToken(token) >> userInfo
            1 * userRepository.findByEmail(email) >> entity
            thrown(WrongDataException)
    }


}
