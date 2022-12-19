package kr.co.ok0.client.naver.dto;

public class NaverNewsReqI {
  private String where;
  private String nso;
  private String query;

  public NaverNewsReqI(String where, String nso, String query) {
    this.where = where;
    this.nso = nso;
    this.query = query;
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
