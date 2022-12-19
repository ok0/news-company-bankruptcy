package kr.co.ok0.job.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SearchKeywordsDto {
  @JsonProperty("keywords")
  public List<String> keywords;

  @JsonProperty("match_title")
  public Boolean isMatchTitle;

  public SearchKeywordsDto() {
    this.keywords = new ArrayList<>();
    this.isMatchTitle = true;
  }

  public SearchKeywordsDto(List<String> keywords, Boolean isMatchTitle) {
    this.keywords = keywords;
    this.isMatchTitle = isMatchTitle;
  }
}
