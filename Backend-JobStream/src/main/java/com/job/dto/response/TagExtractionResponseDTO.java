package com.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagExtractionResponseDTO {
    private String originalText;
    private List<String> extractedTags;
    private String tagsAsString;
    private int tagCount;
    private boolean hasTags;
}
