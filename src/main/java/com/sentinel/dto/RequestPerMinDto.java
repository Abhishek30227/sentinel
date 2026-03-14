package com.sentinel.dto;

public class RequestPerMinDto {

    private String minute;
    private Long count;

    public RequestPerMinDto(String minute, Long count) {
        this.minute = minute;
        this.count = count;
    }
    public String getMinute() {
        return minute;
    }

    public Long getCount() {
        return count;
    }
}
