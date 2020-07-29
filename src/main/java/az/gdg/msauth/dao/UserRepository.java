package az.gdg.msauth.dao;

import az.gdg.msauth.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByMail(String mail);

    List<UserEntity> findFirst3ByOrderByPopularityDesc();

    List<UserEntity> findByIdIn(List<Integer> userIds);
}

