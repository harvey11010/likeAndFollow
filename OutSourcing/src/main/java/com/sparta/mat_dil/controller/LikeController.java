package com.sparta.mat_dil.controller;

import com.sparta.mat_dil.dto.*;
import com.sparta.mat_dil.enums.ContentTypeEnum;
import com.sparta.mat_dil.enums.ResponseStatus;
import com.sparta.mat_dil.exception.CustomException;
import com.sparta.mat_dil.security.UserDetailsImpl;
import com.sparta.mat_dil.service.CommentLikeService;
import com.sparta.mat_dil.service.LikeService;
import com.sparta.mat_dil.service.RestaurantLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final RestaurantLikeService restaurantLikeService;
    private final CommentLikeService commentLikeService;

    @PutMapping("/{contentType}/{contentId}")
    public ResponseEntity<ResponseDataDto<LikeResponseDto>> updatetLike(@PathVariable("contentType") ContentTypeEnum contentType, @PathVariable("contentId") Long contentId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {

        LikeResponseDto likeResponseDto;

        if (contentType.equals(ContentTypeEnum.RESTAURANT)) {
            likeResponseDto = likeService.updateRestaurantLike(contentId, userDetails.getUser());
        } else {
            likeResponseDto = likeService.updateCommentLike(contentId, userDetails.getUser());
        }

        if (likeResponseDto.isLiked()) {
            return ResponseEntity.ok(new ResponseDataDto<>(ResponseStatus.LIKE_CREATE_SUCCESS, likeResponseDto));
        } else {
            return ResponseEntity.ok(new ResponseDataDto<>(ResponseStatus.LIKE_DELETE_SUCCESS, likeResponseDto));
        }
    }


    @GetMapping("/restaurant")
    public ResponseEntity<ResponseDataDto<LikeRestaurantResponseDto>> getLikeRestaurantList(@RequestParam(value = "page") int page, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new ResponseDataDto<>(ResponseStatus.LIKE_SELECT_SUCCESS,restaurantLikeService.getLikeRestaurantList(page - 1, userDetails.getUser())));

    }

    //내가 좋아요한 댓글 목록 조회
    @GetMapping("/comment")
    public ResponseEntity<ResponseDataDto<LikeCommentResponseDto>> getLikeCommentList(@RequestParam(value = "page") int page, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new ResponseDataDto<>(ResponseStatus.LIKE_SELECT_SUCCESS,commentLikeService.getLikeCommentList(page - 1, userDetails.getUser())));
    }
}
