package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.*;
import com.aendyear.komawatsir.entity.*;

public class Mapper {

    public static Design toEntity(DesignDto dto) {
        if (dto == null) {
            return null;
        }

        return Design.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .backgroundId(dto.getBackgroundId())
                .thumbnailId(dto.getThumbnailId())
                .fontId(dto.getFontId())
                .year(dto.getYear())
                .build();
    }

    public static DesignDto toDto(Design entity) {
        if (entity == null) {
            return null;
        }

        return DesignDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .backgroundId(entity.getBackgroundId())
                .thumbnailId(entity.getThumbnailId())
                .fontId(entity.getFontId())
                .year(entity.getYear())
                .build();
    }

    public static Draft toEntity(DraftDto dto) {
        return Draft.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .contents(dto.getContents())
                .build();
    }

    public static DraftDto toDto(Draft entity) {
        return DraftDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .build();
    }

    public static Font toEntity(FontDto dto) {
        return Font.builder()
                .id(dto.getId())
                .name(dto.getName())
                .size(dto.getSize())
                .url(dto.getUrl())
                .color(dto.getColor())
                .build();
    }

    public static FontDto toDto(Font entity) {
        return FontDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .size(entity.getSize())
                .url(entity.getUrl())
                .color(entity.getColor())
                .build();
    }

    public static Image toEntity(ImageDto dto) {
        return Image.builder()
                .id(dto.getId())
                .category(dto.getCategory())
                .name(dto.getName())
                .pic(dto.getPic())
                .isFront(dto.getIsFront())
                .sourceType(dto.getSourceType())
                .build();
    }

    public static ImageDto toDto(Image entity) {
        return ImageDto.builder()
                .id(entity.getId())
                .category(entity.getCategory())
                .name(entity.getName())
                .pic(entity.getPic())
                .isFront(entity.getIsFront())
                .sourceType(entity.getSourceType())
                .build();
    }

    public static Inquiry toEntity(InquiryDto dto) {
        return Inquiry.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .year(dto.getYear())
                .nickname(dto.getNickname())
                .build();
    }

    public static InquiryDto toDto(Inquiry entity) {
        return InquiryDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .year(entity.getYear())
                .nickname(entity.getNickname())
                .build();
    }

    public static InquiryItem toEntity(InquiryItemDto dto) {
        return InquiryItem.builder()
                .id(dto.getId())
                .inquiryId(dto.getInquiryId())
                .question(dto.getQuestion())
                .description(dto.getDescription())
                .build();
    }

    public static InquiryItemDto toDto(InquiryItem entity) {
        return InquiryItemDto.builder()
                .id(entity.getId())
                .inquiryId(entity.getInquiryId())
                .question(entity.getQuestion())
                .description(entity.getDescription())
                .build();
    }

    public static Post toEntity(PostDto dto) {
        return Post.builder()
                .id(dto.getId())
                .senderId(dto.getSenderId())
                .senderNickname(dto.getSenderNickname())
                .receiverId(dto.getReceiverId())
                .contents(dto.getContents())
                .status(dto.getStatus())
                .year(dto.getYear())
                .build();
    }

    public static PostDto toDto(Post entity) {
        return PostDto.builder()
                .id(entity.getId())
                .senderId(entity.getSenderId())
                .senderNickname(entity.getSenderNickname())
                .receiverId(entity.getReceiverId())
                .contents(entity.getContents())
                .status(entity.getStatus())
                .year(entity.getYear())
                .build();
    }

    public static Receiver toEntity(ReceiverDto dto) {
        return Receiver.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .tel(dto.getTel())
                .memo(dto.getMemo())
                .build();
    }

    public static ReceiverDto toDto(Receiver entity) {
        return ReceiverDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .tel(entity.getTel())
                .memo(entity.getMemo())
                .build();
    }

    public static ReceiverQuestion toEntity(ReceiverQuestionDto dto) {
        return ReceiverQuestion.builder()
                .id(dto.getId())
                .inquiryItemId(dto.getInquiryItemId())
                .receiverId(dto.getReceiverId())
                .answer(dto.getAnswer())
                .build();
    }

    public static ReceiverQuestionDto toDto(ReceiverQuestion entity) {
        return ReceiverQuestionDto.builder()
                .id(entity.getId())
                .inquiryItemId(entity.getInquiryItemId())
                .receiverId(entity.getReceiverId())
                .answer(entity.getAnswer())
                .build();
    }

    public static User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .tel(dto.getTel())
                .kakaoId(dto.getKakaoId())
                .isSmsAllowed(dto.getIsSmsAllowed())
                .build();
    }

    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tel(entity.getTel())
                .kakaoId(entity.getKakaoId())
                .isSmsAllowed(entity.getIsSmsAllowed())
                .build();
    }
}
