package com.project.quiz.infrastructure.solving;

import com.project.quiz.application.solving.repository.ChapterRepository;
import com.project.quiz.infrastructure.persistence.repository.ChapterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChapterRepositoryImpl implements ChapterRepository {

    private final ChapterJpaRepository chapterJpaRepository;

    @Override
    public boolean existsById(Long chapterId) {
        return chapterJpaRepository.existsById(chapterId);
    }
}
