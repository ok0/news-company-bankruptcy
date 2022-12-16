package kr.co.ok0.client.naver;

import kr.co.ok0.client.naver.configuration.NaverClientConfiguration;
import kr.co.ok0.client.naver.dto.NaverNewsReqI;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
  name = "naver-news",
  url = "${naver-news.host}",
  configuration = {NaverClientConfiguration.class}
)
public interface NaverClient {
  @GetMapping("/search.naver")
  public String getNews(@SpringQueryMap NaverNewsReqI reqI);
}
