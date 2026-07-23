package com.pkos.backend.repository;

import com.pkos.backend.entity.Notebook;
import com.pkos.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    Optional<Notebook> findByIdAndUser(Long id, User user);

    Page<Notebook> findAllByUser(User user, Pageable pageable);

    boolean existsByNameIgnoreCaseAndUser(
        String name,
        User user
    );
    Optional<Notebook> findByUserAndName(User user, String name);
    boolean existsByNameIgnoreCaseAndUserAndIdNot(
        String name,
        User user,
        Long id
    );
}