package az.gdg.msauth.dao;

import az.gdg.msauth.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    UserEntity findByEmail(String email);
}

