package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.CommentDto;
import com.soma.doubanen.domains.entities.CommentEntity;
import com.soma.doubanen.domains.enums.CommentArea;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.CommentService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/comments")
public class CommentController {

  private final CommentService commentService;
  private final Mapper<CommentEntity, CommentDto> commentMapper;

  private final TokenService tokenService;
  private final UserService userService;

  public CommentController(
      CommentService commentService,
      Mapper<CommentEntity, CommentDto> commentMapper,
      TokenService tokenService,
      UserService userService) {
    this.commentService = commentService;
    this.commentMapper = commentMapper;
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @PostMapping()
  public ResponseEntity<CommentDto> createComment(
      @RequestBody CommentDto commentDto, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(commentDto.getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    CommentEntity comment = commentMapper.mapFrom(commentDto);
    CommentEntity result = commentService.save(null, comment);
    if (result != null) {
      return new ResponseEntity<>(commentMapper.mapTo(result), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<CommentDto> getComment(@PathVariable("id") Long id) {
    Optional<CommentEntity> foundComment = commentService.findById(id);
    return foundComment
        .map(
            commentEntity -> {
              CommentDto commentDto = commentMapper.mapTo(commentEntity);
              return new ResponseEntity<>(commentDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<CommentDto> updateComment(
      @PathVariable("id") Long id,
      @RequestBody CommentDto commentDto,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    Optional<CommentEntity> existingCommentOptional = commentService.findById(id);
    if (existingCommentOptional.isPresent()) {
      CommentEntity existingComment = existingCommentOptional.get();
      if (!username.equals(userService.getUsernameById(existingComment.getUserId()))) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      existingComment.setContent(commentDto.getContent());
      CommentEntity updatedComment = commentService.save(id, existingComment);
      if (updatedComment != null) {
        return new ResponseEntity<>(commentMapper.mapTo(updatedComment), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    Optional<CommentEntity> existingCommentOptional = commentService.findById(id);
    if (existingCommentOptional.isPresent()) {
      CommentEntity existingComment = existingCommentOptional.get();
      if (!username.equals(userService.getUsernameById(existingComment.getUserId()))) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      commentService.delete(id);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(params = {"userId", "page", "size"})
  public Page<CommentDto> getCommentsByUserId(
      @RequestParam Long userId, @RequestParam Integer page, @RequestParam Integer size) {
    Page<CommentEntity> comments =
        commentService.findAllByUserId(userId, PageRequest.of(page - 1, size));
    return comments.map(commentMapper::mapTo);
  }

  @GetMapping(params = {"area", "areaId", "page", "size"})
  public Page<CommentDto> getCommentsByAreaAndAreaId(
      @RequestParam CommentArea area,
      @RequestParam Long areaId,
      @RequestParam Integer page,
      @RequestParam Integer size) {
    Page<CommentEntity> comments =
        commentService.findAllByCommentAreaAndAreaId(areaId, area, PageRequest.of(page - 1, size));
    return comments.map(commentMapper::mapTo);
  }

  @GetMapping(
      path = "/count",
      params = {"userId"})
  public ResponseEntity<Long> countCommentsByUserId(@RequestParam Long userId) {
    Long count = commentService.countByUserId(userId);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }

  @GetMapping(
      path = "/count",
      params = {"area", "areaId"})
  public ResponseEntity<Long> countCommentsByAreaAndAreaId(
      @RequestParam CommentArea area, @RequestParam Long areaId) {
    Long count = commentService.countByCommentAreaAndAreaId(area, areaId);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }
}
