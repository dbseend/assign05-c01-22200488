package com.webservice.week05.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieRequest(
        @NotBlank(message = "title은 비어 있을 수 없습니다.")
        String title,

        @NotBlank(message = "director는 비어 있을 수 없습니다.")
        String director,

        @NotBlank(message = "genre는 비어 있을 수 없습니다.")
        String genre,

        @NotNull(message = "releaseYear는 필수입니다.")
        @Min(value = 1888, message = "releaseYear는 1888 이상이어야 합니다.")
        @Max(value = 2100, message = "releaseYear는 2100 이하여야 합니다.")
        Integer releaseYear,

        @NotNull(message = "rating은 필수입니다.")
        @DecimalMin(value = "0.0", message = "rating은 0.0 이상이어야 합니다.")
        @DecimalMax(value = "10.0", message = "rating은 10.0 이하여야 합니다.")
        Double rating,

        @NotNull(message = "runningTime은 필수입니다.")
        @Positive(message = "runningTime은 1분 이상이어야 합니다.")
        Integer runningTime
) {}
