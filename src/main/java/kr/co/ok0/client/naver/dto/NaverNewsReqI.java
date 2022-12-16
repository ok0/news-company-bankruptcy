package kr.co.ok0.client.naver.dto;

public class NaverNewsReqI {
  private String where;
  private String query;
  private String nso;

  public NaverNewsReqI(String where, String query, String nso) {
    this.where = where;
    this.query = query;
    this.nso = nso;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public void setNso(String nso) {
    this.nso = nso;
  }

  public String getWhere() {
    return where;
  }

  public String getQuery() {
    return query;
  }

  public String getNso() {
    return nso;
  }
}
