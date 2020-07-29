package az.gdg.msauth.service.impl;

import az.gdg.msauth.client.MsStorageClient;
import az.gdg.msauth.dao.UserRepository;
import az.gdg.msauth.exception.ExceedLimitException;
import az.gdg.msauth.exception.NotAllowedException;
import az.gdg.msauth.exception.NotFoundException;
import az.gdg.msauth.exception.StorageException;
import az.gdg.msauth.exception.WrongDataException;
import az.gdg.msauth.mapper.UserMapper;
import az.gdg.msauth.model.dto.MailDTO;
import az.gdg.msauth.model.dto.UserDTO;
import az.gdg.msauth.model.dto.UserDetail;
import az.gdg.msauth.model.entity.UserEntity;
import az.gdg.msauth.security.model.Role;
import az.gdg.msauth.security.model.Status;
import az.gdg.msauth.security.model.dto.UserInfo;
import az.gdg.msauth.security.util.TokenUtil;
import az.gdg.msauth.service.MailService;
import az.gdg.msauth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final MsStorageClient msStorageClient;
    private final MailService mailService;

    public UserServiceImpl(UserRepository userRepository, MsStorageClient msStorageClient,
                           MailService mailService, TokenUtil tokenUtil) {
        this.userRepository = userRepository;
        this.msStorageClient = msStorageClient;
        this.mailService = mailService;
        this.tokenUtil = tokenUtil;
    }

    public void signUp(UserDTO userDTO) {
        logger.info("ServiceLog.signUp user.start.email : {} ", userDTO.getMail());

        if (userDTO.getAreTermsAndConditionsConfirmed()) {
            UserEntity checkedEmail = userRepository.findByMail(userDTO.getMail());
            if (checkedEmail != null) {
                throw new WrongDataException("This email already exists");
            }

            String token = tokenUtil.generateTokenWithEmail(userDTO.getMail());
            String password = new BCryptPasswordEncoder().encode(userDTO.getPassword());
            UserEntity userEntity = UserEntity
                    .builder()
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .username(userDTO.getMail())
                    .mail(userDTO.getMail())
                    .remainingQuackCount(500)
                    .remainingHateCount(500)
                    .password(password)
                    .popularity(0)
                    .role(Role.ROLE_USER)
                    .status(Status.REGISTERED)
                    .build();

            userRepository.save(userEntity);

            MailDTO mail = MailDTO.builder()
                    .to(Collections.singletonList(userDTO.getMail()))
                    .subject("Your registration letter")
                    .body("<h2>" + "Verify Account" + "</h2>" + "</br>" +
                            "<a href=" +
                            "https://gdg-ms-auth.herokuapp.com/user/verify-account?token=" + token + ">" +
                            "https://gdg-ms-auth.herokuapp.com/user/verify-account?token=" + token + "</a>")
                    .build();

            mailService.sendToQueue(mail);


            logger.info("ServiceLog.signUp user.stop.success.email : {}", userDTO.getMail());
        } else {
            throw new NotAllowedException("Not allowed sign up operation, if you don't agree our terms and conditions");
        }


    }

    @Override
    public void verifyAccount(String token) {
        logger.info("ServiceLog.verifyAccount.start");
        String mail = tokenUtil.getMailFromToken(token);
        UserEntity user = userRepository.findByMail(mail);

        if (user != null) {
            user.setStatus(Status.CONFIRMED);
            userRepository.save(user);
        } else {
            throw new NotFoundException("Not found such user");
        }

        logger.info("ServiceLog.verifyAccount.stop.success");

    }

    @Override
    public void sendResetPasswordLinkToMail(String mail) {
        logger.info("ServiceLog.sendResetPasswordLinkToMail.start.mail : {}", mail);
        UserEntity user = userRepository.findByMail(mail);

        if (user != null) {

            String token = tokenUtil.generateTokenWithEmail(mail);

            MailDTO mailDTO = MailDTO.builder()
                    .to(Collections.singletonList(mail))
                    .subject("Your reset password letter")
                    .body("<h2>" + "Reset Password" + "</h2>" + "</br>" +
                            "<a href=" +
                            "http://virustat.org/reset.html?token=" + token + ">" +
                            "http://virustat.org/reset.html?token=" + token + "</a>")
                    .build();

            mailService.sendToQueue(mailDTO);

        } else {
            throw new NotFoundException("Not found such user!");
        }

        logger.info("ServiceLog.sendResetPasswordLinkToMail.stop.success.mail : {}", mail);

    }

    @Override
    public void changePassword(String token, String password) {
        logger.info("ServiceLog.changePassword.start");
        String mail = tokenUtil.getMailFromToken(token);

        UserEntity user = userRepository.findByMail(mail);

        if (user != null) {
            boolean check = new BCryptPasswordEncoder().matches(password, user.getPassword());

            if (!check) {
                String newPassword = new BCryptPasswordEncoder().encode(password);
                user.setPassword(newPassword);
                userRepository.save(user);
            } else {
                throw new WrongDataException("Please, enter the password different from last one");
            }


        } else {
            throw new NotFoundException("Not found such user!");
        }

        MailDTO mailDTO = MailDTO.builder()
                .to(Collections.singletonList(mail))
                .subject("Successfully Changed")
                .body("<h2>" + "Your password has been changed successfully" + "</h2>")
                .build();

        mailService.sendToQueue(mailDTO);

        logger.info("ServiceLog.changePassword.stop.success");

    }

    @Override
    public UserDetail getUserById(Long id) {
        logger.info("ServiceLog.getUserById.start.id : {}", id);

        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        logger.info("ServiceLog.getUserById.success.id : {}", id);

        return UserMapper.INSTANCE.entityToDto(user);

    }

    @Override
    public List<UserDetail> getUsersById(List<Long> userIds) {
        logger.info("ServiceLog.getUsersById.start.userIds : {}", userIds);

        List<UserDetail> userDetails = new ArrayList<>();
        for (Long userId : userIds) {
            userRepository.findById(userId)
                    .ifPresent(userEntity -> userDetails.add(UserMapper.INSTANCE.entityToDto(userEntity)));
        }

        logger.info("ServiceLog.getUsersById.stop.success");

        return userDetails;
    }

    @Override
    public void addPopularity(Long userId) {
        logger.info("ServiceLog.addPopularity.start.userId : {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        user.setPopularity(user.getPopularity() + 1);
        userRepository.save(user);

        logger.info("ServiceLog.addPopularity.stop.success.userId : {}", userId);
    }

    @Override
    @Cacheable(value = "populars")
    public List<UserDetail> getPopularUsers() {
        logger.info("ServiceLog.getPopularUsers.start");
        List<UserEntity> users = userRepository.findFirst3ByOrderByPopularityDesc();

        List<UserDetail> populars = UserMapper.INSTANCE.entityToDtoList(users);
        logger.info("ServiceLog.getPopularUsers.stop.success");
        return populars;
    }

    @Override
    public void updateRemainingQuackCount(String token) {
        logger.info("ServiceLog.updateRemainingQuackCount.start");
        UserInfo userInfo = tokenUtil.getUserInfoFromToken(token);
        Long userId = Long.parseLong(userInfo.getUserId());

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        if (userEntity.getRemainingQuackCount() > 0) {
            userEntity.setRemainingQuackCount(userEntity.getRemainingQuackCount() - 1);
            userRepository.save(userEntity);
        } else {
            throw new ExceedLimitException("You've already used your daily quacks!");
        }

        logger.info("ServiceLog.updateRemainingQuackCount.stop.success");


    }

    @Override
    public void updateRemainingHateCount(String token) {
        logger.info("ServiceLog.updateRemainingHateCount.start");
        UserInfo userInfo = tokenUtil.getUserInfoFromToken(token);
        Long userId = Long.parseLong(userInfo.getUserId());

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        if (userEntity.getRemainingHateCount() > 0) {
            userEntity.setRemainingHateCount(userEntity.getRemainingHateCount() - 1);
            userRepository.save(userEntity);
        } else {
            throw new ExceedLimitException("You've already used your daily hates!");
        }


        logger.info("ServiceLog.updateRemainingHateCount.stop.success");

    }

    @Override
    public Integer getRemainingQuackCount(String token) {
        logger.info("ServiceLog.getRemainingQuackCount.start");
        UserInfo userInfo = tokenUtil.getUserInfoFromToken(token);
        Long userId = Long.parseLong(userInfo.getUserId());

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        logger.info("ServiceLog.getRemainingQuackCount.stop.success");
        return userEntity.getRemainingQuackCount();
    }

    @Override
    public Integer getRemainingHateCount(String token) {
        logger.info("ServiceLog.getRemainingHateCount.start");
        UserInfo userInfo = tokenUtil.getUserInfoFromToken(token);
        Long userId = Long.parseLong(userInfo.getUserId());

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        logger.info("ServiceLog.getRemainingHateCount.stop.success");
        return userEntity.getRemainingHateCount();
    }

    @Override
    @Scheduled(cron = "0 52 23 * * ?")  // at 23:59 every day
    public void refreshRemainingQuackAndHateCount() {
        logger.info("ServiceLog.refreshRemainingQuackAndHateCount.start");
        List<UserEntity> users = userRepository.findAll();
        if (!users.isEmpty()) {
            users
                    .forEach(userEntity -> {
                        userEntity.setRemainingQuackCount(500);
                        userEntity.setRemainingHateCount(500);
                        userRepository.save(userEntity);
                    });
        } else {
            throw new NotFoundException("There isn't any user in database");
        }

        logger.info("ServiceLog.refreshRemainingQuackAndHateCount.stop.success");
    }

    @Override
    public void updateImage(String token, List<MultipartFile> multipartFile) {
        logger.info("ServiceLog.updateImage.start");
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.get(0).getOriginalFilename(),
                "File content must not be null!"));

        logger.info("ServiceLog.updateImage.fileName : {} ", fileName);

        UserInfo userInfo = tokenUtil.getUserInfoFromToken(token);
        UserEntity userEntity = userRepository.findById(Long.parseLong(userInfo.getUserId())).orElseThrow(
                () -> new NotFoundException("Not found such user")
        );

        if (fileName.contains("..")) {
            throw new StorageException("Cannot store file with relative path outside current directory " +
                    fileName);
        }

        String imageUrl = msStorageClient.uploadFile("Users", multipartFile);
        userEntity.setImageUrl(imageUrl);

        userRepository.save(userEntity);

        logger.info("ServiceLog.updateImage.stop.success.fileName : {} ", multipartFile.get(0).getOriginalFilename());
    }


}
