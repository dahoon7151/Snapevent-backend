package com.example.snapEvent.board.service;

import com.example.snapEvent.board.dto.LikeResponseDto;
import com.example.snapEvent.board.dto.PostDto;
import com.example.snapEvent.board.dto.PostResponseDto;
import org.springframework.data.domain.Page;

public interface PostService {

    public Page<PostResponseDto> sortPostlist(int page, int postCount, String order);

    public PostResponseDto showPost(String username, Long id);

    public PostResponseDto writePost(String username, PostDto postDto);

    public LikeResponseDto like(String username, Long id);

    public PostResponseDto modifyPost(String username, Long id, PostDto postDto);

    public boolean deletePost(String username, Long id);

    public PostResponseDto showNearPost(Long id);
}
