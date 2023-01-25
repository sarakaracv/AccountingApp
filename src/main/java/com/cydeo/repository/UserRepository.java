package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
    User findByUsername(String username);
    List<User> findAllByCompanyOrderByRoleDescription(Company company);
    List<User> findAllByRoleDescriptionOrderByCompanyTitle(String role);

    List<User> findAllByRoleDescriptionAndCompanyOrderByCompanyTitleAscRoleDescription(String admin, Company company);

}
