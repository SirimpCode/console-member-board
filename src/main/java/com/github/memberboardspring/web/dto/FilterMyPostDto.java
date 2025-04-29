package com.github.memberboardspring.web.dto;

import lombok.Getter;

@Getter
public class FilterMyPostDto {
    private long loginUserId;
    private boolean isFilter;
    private boolean cantMyPost;
    private String errorMessage;
    private FilterMyPostDto() {}
    public static FilterMyPostDto cantMyPost(long loginUserId, String message) {
        FilterMyPostDto filterMyPostDto = createTempDto(loginUserId, message);
        filterMyPostDto.cantMyPost = true;
        return filterMyPostDto;
    }
    public static FilterMyPostDto onlyAccessMyPost(long loginUserId,String message){
        return createTempDto(loginUserId, message);
    }
    private static FilterMyPostDto createTempDto(long loginUserId, String errorMessage) {
        FilterMyPostDto filterMyPostDto = new FilterMyPostDto();
        filterMyPostDto.loginUserId = loginUserId;
        filterMyPostDto.isFilter = true;
        filterMyPostDto.errorMessage = errorMessage;
        return filterMyPostDto;

    }

    public static FilterMyPostDto notIsFilter(){
        FilterMyPostDto filterMyPostDto = new FilterMyPostDto();
        filterMyPostDto.isFilter = false;
        return filterMyPostDto;
    }

}
