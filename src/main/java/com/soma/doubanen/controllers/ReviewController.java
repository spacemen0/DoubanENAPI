package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.ReviewDto;
import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.ReviewService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  private final TokenService tokenService;
  private final UserService userService;
  private final Mapper<ReviewEntity, ReviewDto> reviewMapper;

  public ReviewController(
      ReviewService reviewService,
      TokenService tokenService,
      UserService userService,
      Mapper<ReviewEntity, ReviewDto> reviewMapper) {
    this.reviewService = reviewService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.reviewMapper = reviewMapper;
  }

  @PostMapping()
  public ResponseEntity<ReviewDto> createReview(
      @RequestBody ReviewDto reviewDto, @RequestHeader(name = "Authorization") String auth)
      throws Exception {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(reviewDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    ReviewEntity reviewEntity = reviewMapper.mapFrom(reviewDto);
    ReviewEntity result = reviewService.save(reviewEntity, null);
    return new ResponseEntity<>(reviewMapper.mapTo(result), HttpStatus.CREATED);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<ReviewDto> getReview(@PathVariable("id") Long id) {
    Optional<ReviewEntity> foundReview = reviewService.findOne(id);
    return foundReview
        .map(
            reviewEntity -> {
              ReviewDto reviewDto = reviewMapper.mapTo(reviewEntity);
              return new ResponseEntity<>(reviewDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<ReviewDto> updateReview(
      @PathVariable("id") Long id,
      @RequestBody ReviewDto reviewDto,
      @RequestHeader(name = "Authorization") String auth)
      throws Exception {
    if (reviewService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(reviewDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    ReviewEntity entity = reviewMapper.mapFrom(reviewDto);
    ReviewEntity result = reviewService.save(entity, id);
    return new ResponseEntity<>(reviewMapper.mapTo(result), HttpStatus.CREATED);
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<ReviewDto> partialUpdateReview(
      @PathVariable("id") Long id,
      @RequestBody ReviewDto reviewDto,
      @RequestHeader(name = "Authorization") String auth) {
    if (reviewService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(reviewDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    ReviewEntity reviewEntity = reviewMapper.mapFrom(reviewDto);
    ReviewEntity updatedReview = reviewService.partialUpdate(id, reviewEntity);
    return new ResponseEntity<>(reviewMapper.mapTo(updatedReview), HttpStatus.OK);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteReview(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    Optional<ReviewEntity> reviewDto = reviewService.findOne(id);
    if (reviewDto.isPresent()) {
      String token = auth.substring(7);
      String username = tokenService.extractUsername(token);
      if (!username.equals(userService.getUsernameById(reviewDto.get().getUser().getId())))
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    reviewService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping(params = {"userId", "mediaId"})
  public ResponseEntity<Void> deleteReviewByUserIdAndMediaId(
      @RequestParam Long userId,
      @RequestParam Long mediaId,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    reviewService.deleteByUserIdAndMediaId(userId, mediaId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(path = "/count", params = "mediaId")
  public ResponseEntity<Long> getReviewCount(@RequestParam Long mediaId) {
    return new ResponseEntity<>(reviewService.countAllByMediaId(mediaId), HttpStatus.OK);
  }

  @GetMapping(params = {"mediaId", "page", "size"})
  public ResponseEntity<Page<ReviewDto>> listReviews(
      @RequestParam Integer page, @RequestParam Integer size, @RequestParam Long mediaId) {
    Page<ReviewEntity> reviews =
        reviewService.findAllByMediaId(mediaId, PageRequest.of(page - 1, size));
    return new ResponseEntity<>(reviews.map(reviewMapper::mapTo), HttpStatus.OK);
  }
}
