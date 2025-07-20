package com.example.spot.story.application.port.in.command.impl;

import com.example.spot.story.application.port.in.command.EditStoryCommentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EditStoryCommentService implements EditStoryCommentUseCase {
}
