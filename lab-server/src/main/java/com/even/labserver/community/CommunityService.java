package com.even.labserver.community;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PagedModel<PostDto> getPosts(int page, String sort, boolean isAsc) {
        final int size = 10;

        List<Sort.Order> sorts = List.of(isAsc ? Sort.Order.asc(sort) : Sort.Order.desc(sort));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sorts));

        var ret = postRepository.findAllBy(pageable).map(PostDto::fromWithoutComments);
        return new PagedModel<>(ret);
    }

    @Transactional
    public PostDto getPost(int id) {
        var post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        // 조회때마다 조회수 증가
        post.increaseViewCount();
        postRepository.save(post);
        return PostDto.fromWithComments(post);
    }

    @Transactional
    public Integer addPost(PostDto postDto) {
        var post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(postDto.getAuthor())
                .viewCount(0)
                .likeCount(0)
                .build();
        return postRepository.save(post).getPostId();
    }

    @Transactional
    public Integer removePost(int id) {
        postRepository.deleteById(id);
        return id;
    }

    @Transactional
    public Integer updatePost(PostDto postDto) {
        var post = postRepository.findById(postDto.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }
//        if (!post.getAuthor().equals(postDto.getAuthor())) {
//            return null;
//        }
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return postRepository.save(post).getPostId();
    }

    @Transactional
    public Integer likePost(int id) {
        var post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        post.increaseLikeCount();
        postRepository.save(post);
        return post.getLikeCount();
    }

    @Transactional
    public Integer addComment(CommentDto commentDto) {
        var postId = commentDto.getPostId();
        var post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return null;
        }
        var comment = Comment.builder()
                .content(commentDto.getContent())
                .author(commentDto.getAuthor())
                .post(post)
                .build();
        post.getComments().add(comment);
        postRepository.save(post);
        return comment.getCommentId();
    }

    @Transactional
    public Integer removeComment(int postId, int commentId) {
        var post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return null;
        }
        commentRepository.deleteById(commentId);
        return commentId;
    }

    @Transactional
    public Integer updateComment(CommentDto commentDto) {
        var comment = commentRepository.findById(commentDto.getCommentId()).orElse(null);
        if (comment == null) {
            return null;
        }
        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment).getCommentId();
    }
}
