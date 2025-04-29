package com.github.memberboardspring.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface MyRoleRepository extends JpaRepository<MyRole, Long> {
    MyRole findByRoleName(String name);

}
