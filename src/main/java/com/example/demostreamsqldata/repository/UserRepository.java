package com.example.demostreamsqldata.repository;

import com.example.demostreamsqldata.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface UserRepository extends JpaRepository<User, Long> {
    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "100"))
    @Query(value = "select u from User u")
    Stream<User> streamAll();

    Slice<User> findAllBy(PageRequest request);
}
