package com.job.service.impl;

import com.job.dto.response.TagExtractionResponseDTO;
import com.job.service.TagService;
import com.job.util.TagExtractor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Override
    public TagExtractionResponseDTO extractTags(String text) {
        List<String> tags = TagExtractor.extractTags(text);
        String tagsAsString = TagExtractor.extractTagsAsString(text);
        int tagCount = TagExtractor.countTags(text);
        boolean hasTags = TagExtractor.hasTags(text);

        TagExtractionResponseDTO response = new TagExtractionResponseDTO();
        response.setOriginalText(text);
        response.setExtractedTags(tags);
        response.setTagsAsString(tagsAsString);
        response.setTagCount(tagCount);
        response.setHasTags(hasTags);

        return response;
    }

    @Override
    public List<String> extractTagsAsList(String text) {
        return TagExtractor.extractTags(text);
    }

    @Override
    public String extractTagsAsString(String text) {
        return TagExtractor.extractTagsAsString(text);
    }

    @Override
    public boolean hasTags(String text) {
        return TagExtractor.hasTags(text);
    }

    @Override
    public int countTags(String text) {
        return TagExtractor.countTags(text);
    }
}
