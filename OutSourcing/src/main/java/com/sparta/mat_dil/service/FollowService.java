package com.sparta.mat_dil.service;

import com.sparta.mat_dil.dto.ProfileResponseDto;
import com.sparta.mat_dil.dto.ResponseMessageDto;
import com.sparta.mat_dil.dto.RestaurantResponseDto;
import com.sparta.mat_dil.entity.Follow;
import com.sparta.mat_dil.entity.User;
import com.sparta.mat_dil.enums.ErrorType;
import com.sparta.mat_dil.enums.ResponseStatus;
import com.sparta.mat_dil.exception.CustomException;
import com.sparta.mat_dil.repository.FollowRepository;
import com.sparta.mat_dil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public ResponseMessageDto createFollowing(String accountId, User user) {
        //팔로우하려는 사용자가 있는지 확인
        User followedUser= userRepository.findByAccountId(accountId).orElseThrow(
                ()->new CustomException(ErrorType.NOT_FOUND_USER));

        //팔로우하려는 사용자의 객체와 나의 객체가 follow에 있는지 확인 -> dsl
        //없으면 팔로우 처리
        Follow follow = followRepository.findByFromUserAccountIdAndToUserAccountId(accountId, user.getAccountId()).orElse(new Follow(followedUser, user));

        follow.update(); //이미 팔로우된 상태면 팔로우 취소
        followRepository.save(follow);

        return new ResponseMessageDto(ResponseStatus.FOLLOW_CREATE_SUCCESS);
    }

    public Page<RestaurantResponseDto> getFollowingPost(int page, User user) {
        Pageable pageable = PageRequest.of(page, 5);

        return new PageImpl<>(followRepository.findAllByToUserAccountId(user.getAccountId(), pageable).stream().map(RestaurantResponseDto::new).collect(Collectors.toList()));
    }

    public List<ProfileResponseDto> findTop10ByOrderByFollowersCntDesc() {
        return userRepository.findTop10ByOrderByFollowersCntDesc();
    }
}

