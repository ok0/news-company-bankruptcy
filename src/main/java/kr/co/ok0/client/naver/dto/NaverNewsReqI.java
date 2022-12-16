package kr.co.ok0.client.naver.dto;

public class NaverNewsReqI {
  private String where;
  private String query;

  public NaverNewsReqI(String where, String query) {
    this.where = where;
    this.query = query;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getWhere() {
    return where;
  }

  public String getQuery() {
    return query;
  }
}
