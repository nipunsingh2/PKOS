package com.pkos.backend.repository;


import com.pkos.backend.entity.Tag;
import com.pkos.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByIdAndUser(Long id, User user);

    List<Tag> findAllByUserOrderByNameAsc(User user);

    boolean existsByNameIgnoreCaseAndUser(String name, User user);

    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);

    boolean existsByNameIgnoreCaseAndUserAndIdNot(
            String name,
            User user,
            Long id
    );
    Page<Tag> findByUser(
            User user,
            Pageable pageable
    );
}