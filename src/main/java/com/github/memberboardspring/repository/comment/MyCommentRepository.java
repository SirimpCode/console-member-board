package com.github.memberboardspring.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MyCommentRepository extends JpaRepository<MyComment, Long> {

}
