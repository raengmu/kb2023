# 블로그 검색 서비스

---

## 목차

* [결과물 다운로드](#결과물-다운로드)
* [개발환경](#개발환경)
* [빌드 및 실행](#빌드-및-실행)
* [구현 내역](#구현-내역)
  * 블로그 검색
  * 인기 검색어 목록
  * 기타
  * TODO:
  * TODO: FURTHER
* [API 상세](#API-상세)
* [오류코드](#오류코드)

---

## 결과물 다운로드

https://github.com/raengmu/kb2023/releases/tag/kb2023.1

다운로드
> $ wget https://github.com/raengmu/kb2023/releases/download/kb2023.1/kb2023-0.0.1-SNAPSHOT.jar

실행
> $ java -jar kb2023-0.0.1-SNAPSHOT.jar

WAS port : 8080

H2 : /h2

---

## 개발환경

* Kotlin
* JDK 17
* Spring Boot 2.7.10-SNAPSHOT
* JPA
* H2 (mem)
* Gradle
* Kotest

---

## 빌드 및 실행

테스트 및 빌드
```
$ ./gradlew clean test bootjar
```

실행
```
$ java -jar build/libs/kb2023-0.0.1-SNAPSHOT.jar
```

---

## 구현 내역
1. 블로그 검색
   * 키워드를 통해 블로그를 검색할 수 있어야 합니다.
     * 완료
   * 검색 결과에서 Sorting(정확도순, 최신순) 기능을 지원해야 합니다.
     * 완료
   * 검색 결과는 Pagination 형태로 제공해야 합니다.
     * 완료
   * 검색 소스는 카카오 API의 키워드로 블로그 검색(https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog)을 활용합니다.
     * 완료
   * 추후 카카오 API 이외에 새로운 검색 소스가 추가될 수 있음을 고려해야 합니다.
     * 완료
     * application.yml의 properties.search.sources   
     * v1/service/search/source/ 의 parser 및 source
     * v1/service/SearchController에 해당 parser 및 source들을 list로 injection  
   * (Optional) 트래픽이 많고, 저장되어 있는 데이터가 많음을 염두에 둔 구현
     * 완료 
     * 검색 요청시 필요한 페이지를 외부 검색 서비스로부터 cachePageSize 블럭으로 mem DB에 async caching
     * caching시 중복되는 페이지가 있는 경우 최신 블럭을 사용하므로 DB lock 없이 구현
     * cache 블럭들은 properties.search.cacheExpireSec(60초) 후 expire 되고, 이는 검색 결과에서 expire 가 발견되면 async 로 실행
     * properties.search.enableCache(true/false) 설정으로 caching 기능 켜고 끔
     * API 호출을 위해 WebClient의 request를 async 처리 
   * (Optional) 카카오 블로그 검색 API에 장애가 발생한 경우, 네이버 블로그 검색 API를 통해 데이터 제공
     * 완료
     * 카카오 API 로부터 검색 결과를 받지 못한 경우(접근 실패 또는 결과 parsing 실패) 네이버 블로그 검색 대체

2. 인기 검색어 목록
   * 사용자들이 많이 검색한 순서대로, 최대 10개의 검색 키워드를 제공합니다.
     * 완료
   * 검색어 별로 검색된 횟수도 함께 표기해 주세요.
     * 완료
   * (Optional) 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 구현 (예시. 키워드 별로 검색된 횟수의 정확도)
     * 완료
     * 검색시 DB row lock 걸고 update
     * 검색 process 에 방해되지 않도록 async 처리

3. 기타
   * (완료) 최대한 검색 기능 일반화 하여 글, 카페, 책 검색에도 공통으로 사용할 수 있도록 설계 및 구성
     * 예로 책 검색 추가

4. TODO:
   * 검색 기능 외부 서비스 API 접근 추가 최적화
     * WebClient의 connection pool 사용 고려
       이미 기본 connection pool 동작 - connection pool 설정 study
     * (완료) PerfJsonWebSearchRetriever 추가
       SimpleJsonWebSearchRetriever의 multiplexing에 parallelStream 이용한 동시 request 처리
       대략 50 cache page size로 300개 페이지 요청시 300/50=6번의 request를 하는데,
       localhost에서 Postman 사용시 Simple이 600~1200msec 의 시간이 걸리던 것을 400~700msec 이하로 개선 
       실 서비스에서는 source service 의 동시 connection 제약 확인 필요 
   * 검색 기능 cache page json string(raw) data를 parsing 된 serialized 데이타로 변경 고려
     단순 TC로 raw json object -> json reponse object ..vs.. serialized object -> json reponse object 측정하여 간접 비교 가능
   * (완료) countTop 성능 최적화
     * 검색이 요청될때마다 insert/select(update) 하도록 되어 있는데,
       async로 동작한다 하더라도 검색 횟수만큼 부하가 커지므로 부하가 발생시 문제가 될 수 있다.
     * 일반적으로 검색어 순위의 정확도는 시차를 두고 반영되어도 문제가 없을 것으로 보이므로,
       가능하다면 이러한 시차 제약사항을 이용해 각 WAS의 process들이 검색어를 집계해 일정 시간마다 한번에 DB에 반영하도록 한다.
       @EnableSchedule 로 cron 작업 설정
   * (완료) countTop 부문별 적용
     * 검색 기능과 마찬가지로 부문별 검색 count 집계 가능토록 재구조화 필요
   * 추후 검색 및 countTop 기능의 부하가 커져 감당할 수 없는 경우 고려
     * cache DB 및 WAS를 scale out 하는 것을 고려한 POJO 재설계 및 유지보수 필요
       * 1차 캐시를 WAS내 mem DB를 활용한 수준이며, scale out 을 위해서는 추가 DB 필요(2차 캐시로 볼 수 있음)
         따라서 2차 캐시 사용하는 DB의 operation을 위한 POJO의 template method 정의 또는 2차 캐시 루틴 injection 필요
     * 성능에 영향을 미치는 구성요소의 대체재들의 성능 및 안정성 PoC 및 benchmarking
   * TC 추가
     * SearchHelperImpl 및 기타 외부 연관부
   * error response 명확히 정리
   * 검색 기능의 datetime의 TZ 정책 결정 필요
   * 검색 기능에 page 접근시 오류 또는 다른 페이지의 중복 결과(API 서버 특성에 따라) 발생 그리고 마지막 페이지 처리 필요 

5. TODO: FURTHER
    * (완료) Impl 을 bean으로 IoC container에서 관리하도록 설정 필요
    * (완료) unused 삭제
    * (완료) SimpleWebSearchRetriever IoC container에서 관리 및 성능 검토
    * (완료) SimpleWebSearchRetriever의 파라미터 처리 일반화
        * Blog 뿐 아니라 다른 search 에서도 사용 가능토록(또는 일반적인 API 처리)
    * (완료) 구현부 구조 최적화
      -> searchjpa, api.v1.service.searchjpa 로 JPA 구현부 이동 및 api.v1.service.config 에서 injection 설정
    * (완료) search cache의 가변 길이 key를(source 정보 및 http fixed query params concate) md5로 16bytes로 고정하여 DB 접근 성능 및 용량 절약 
    * (완료) WebMvcConfigurer.addCorsMappings override to allow origins/paths/methods/headers
    * Kakao 나 Naver API key 은폐

---

## API 상세

---

### 블로그 검색

```agsl
GET /v1/search/blog HTTP/1.1
```
국내 블로그 검색 서비스 API들을 이용해 블로그 검색 결과를 출력합니다.

#### Request parameters
| Name     | Type    | Description                                  | Required |
|----------|---------|----------------------------------------------|----------|
| query    | String  | 검색을 원하는 질의어                                  | O        |
| order    | String  | 결과 문서 정렬 방식, 'A'(정확도순) 또는 'L'(최신순), 기본 값 'A' | X        |
| page     | Integer | 결과 페이지 번호, 1 이상, 기본 값 1                      | X        |
| pageSize | Integer | 한 페이지에 보여질 문서 수, 1 이상, 기본 값 10             | X        |

#### Response

| Name | Type    | Description |
|---|---------|-------------|
| page | Integer | 결과 페이지 번호   |
| pageSize | Integer | 결과 페이지 문서 수 |
| documents | Array   | 문서 객체 목록    |

##### documents

| Name | Type    | Description                                          |
|---|---------|------------------------------------------------------|
| title | String | 제목                                                   |
| content | String | 요약                                                   |
| url | String | URL                                                  |
| name | String | 블로그 이름                                               |
| thumbnailUrl | String | 썸네일 URL                                              |
| datetime | String | 작성시간(검색결과 그대로) `[YYYY]-[MM]-[DD]T[hh]:[mm]:[ss]` |

#### Sample

```agsl
curl -v -X GET "http://localhost:8080/v1/search/blog?query=daum&page=1&pageSize=20&order=L"
```
```
{
    "page": 1,
    "pageSize": 20,
    "documents": [
        {
            "title": "아로니아 효능과 부작용 먹는 방법",
            "content": "면역력을 강화하고 염증을 줄이는 데 도움이 됩니다. 또한 혈압과 혈당 수치를 낮추는 데도 효과적이라고 알려져 있습니다. 아로니아 효능 아로니아의 효능은 <b>다음</b>과 같습니다. 1. 항산화 작용 2. 면역력 강화 3. 혈압과 혈당 조절 4. 감염 예방 5. 소화기 건강 개선 6. 암 예방 아로니아는 다양한 건강 효능을 가진다고...",
            "url": "http://4your2day.tistory.com/99",
            "name": "슬기로운하루하루",
            "thumbnailUrl": "https://search3.kakaocdn.net/argon/130x130_85_c/4MsitMWYP6D",
            "datetime": "2023-03-22T10:13:28"
        },
        {
            "title": "청년도약계좌 글과 그림으로 보는 상세 질문과 답변 (Q&amp;A)",
            "content": "개인소득과 무관한 사유로 기여금 지급이 중단될 수 있으므로 가구소득은 유지심사에서 제외 ▶유지심사 결과 개인소득 6,000만 원(총 급여 기준) 초과 시 <b>다음</b> 유지심사 시까지 정부기여금 지급이 중단됩니다. ▶<b>다음</b> 유지심사에서 개인소득 6,000만 원(총 급여 기준) 이하일 경우 그 이후 유지심사 시까지 정부기여금...",
            "url": "http://1.sojamin.com/2",
            "name": "스마트한 일상들",
            "thumbnailUrl": "https://search3.kakaocdn.net/argon/130x130_85_c/EUgRmPfXx6X",
            "datetime": "2023-03-22T10:13:11"
        },
        ...
    ]
}
```
---

### 인기 검색어 목록

```agsl
GET /v1/search/blog/countTop HTTP/1.1
```
사용자들이 많이 검색한 순서대로, 기본 10개의 검색 키워드를 제공합니다.

#### Request parameters
| Name | Type    | Description                | Required |
|------|---------|----------------------------|----------|
| num  | Integer | 조회를 원하는 최대 키워드 개수, 기본 값 10 | X        |

#### Response

| Name  | Type    | Description |
|-------|---------|-------------|
| query | String  | 키워드         |
| count | Integer | 검색 횟수       |

#### Sample

```agsl
curl -v -X GET "http://localhost:8080/v1/search/blog/countTop"
```
```
[
    {
        "query": "daum",
        "count": 121
    },
    {
        "query": "naver",
        "count": 94
    },
    ...
]
```

---

### 서적 검색

```agsl
GET /v1/search/book HTTP/1.1
```
국내 서적 검색 서비스 API들을 이용해 서적 검색 결과를 출력합니다.

#### Request parameters
| Name     | Type    | Description                                  | Required |
|----------|---------|----------------------------------------------|----------|
| query    | String  | 검색을 원하는 질의어                                  | O        |
| order    | String  | 결과 문서 정렬 방식, 'A'(정확도순) 또는 'L'(최신순), 기본 값 'A' | X        |
| page     | Integer | 결과 페이지 번호, 1 이상, 기본 값 1                      | X        |
| pageSize | Integer | 한 페이지에 보여질 문서 수, 1 이상, 기본 값 10               | X        |
| target   | String  | 'T': 제목, 'I': ISBN, 'P': 출판사, 'E': 인명, 기본 값 'T'      | X        |

#### Response

| Name | Type    | Description |
|---|---------|-------------|
| page | Integer | 결과 페이지 번호   |
| pageSize | Integer | 결과 페이지 문서 수 |
| documents | Array   | 문서 객체 목록    |

##### documents

| Name         | Type     | Description                                      |
|--------------|----------|--------------------------------------------------|
| title        | String   | 제목                                               |
| contents     | String   | 소개                                               |
| url          | String   | 도서 상세 URL                                        |
| isbn         | String   | ISBN                                             |
| thumbnail | String   | 썸네일 URL                                          |
| datetime     | String   | 출판날짜(검색결과 그대로) `[YYYY]-[MM]-[DD]T[hh]:[mm]:[ss]` |
| authors | String[] | 저자                                               |
| publisher | String   | 출판사                                              |
| translators | String[] | 번역자                                              |
| price | Integer  | 정상가                                              |
| sale_price |    Integer      | 판매가                                              |
| status | String   | 판매 상태 정보 (정상, 품절, 절판 등)                          |

#### Sample

```agsl
curl -v -X GET "http://localhost:8080/v1/search/book/?query=test&page=2&pageSize=50&order=A&target=T"
```
```
{
    "page": 2,
    "pageSize": 50,
    "documents": [
        {
            "authors": [
                "Hammitt Gene M (EDT)/ Mouat Ricardo Gutierrez"
            ],
            "translators": [],
            "isbn": "0738602523 9780738602523",
            "priceKrw": 30250,
            "priceDiscountedKrw": 23310,
            "publisher": "Research & Education",
            "title": "Best Test Preparation for the SAT Subject Test Spanish, 5/e",
            "content": "",
            "url": "https://search.daum.net/search?w=bookpage&bookId=2222884&q=Best+Test+Preparation+for+the+SAT+Subject+Test+Spanish%2C+5%2Fe",
            "status": "정상판매",
            "thumbnailUrl": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F2222884%3Ftimestamp%3D20190213051936",
            "datetime": "2008-01-08T00:00:00"
        },
        {
            "authors": [
                "Hutton Rosalie"
            ],
            "translators": [],
            "isbn": "0857254855 9780857254856",
            "priceKrw": 35970,
            "priceDiscountedKrw": 34420,
            "publisher": "Learning Matters",
            "title": "Passing the National Admissions Test for Law (Lnat) Test for Law (Lnat)",
            "content": "",
            "url": "https://search.daum.net/search?w=bookpage&bookId=3165214&q=Passing+the+National+Admissions+Test+for+Law+%28Lnat%29+Test+for+Law+%28Lnat%29",
            "status": "정상판매",
            "thumbnailUrl": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3165214%3Ftimestamp%3D20190218050412",
            "datetime": "2014-04-23T00:00:00"
        },
        ...
    ]
}
```

---

## 오류코드
#### Response
| Name    | Type    | Description      |
|---------|---------|------------------|
| status  | Integer | HTTP status code |
| code    | Integer | 오류 코드         |
| message | String  | 사용자 오류 메시지       |
#### 오류 리스트
| status | code | message          | Description                           |
|--------|------|------------------|---------------------------------------|
| 400    | 1000 | invalid parameter | 파라미터 누락                               |
| 400    | 1001 | missing header | HTTP Header 누락                        |
| 500    | 1002 | internal server error | 서비스 내부 정의되지 않은 오류                     |
| 500    | 2000 | no data | 서비스 문제로 데이타를 출력하지 못함                  |
| 500    | 9001 | service not available temporarily | 검색시 서비스 내부 오류                         |
| 500    | 9002 | failed to retrieve resources | 검색시 외부 서비스 접근 오류                      |
| 500    | 9003 | failed to parse resources | 검색시 외부 서비스에서 받은 값을 parsing 하는 과정에서 오류 |
| 500    | 9004 | failed to process | 검색시 내부 로직 오류 assert fail              |
| 500    | 9005 | network or source not available | 검색시 외부 서비스 접근 불가                      |
| 500    | 9006 | too busy | 검색시 시스템 부하가 커서 처리가 되지 않은 것으로 보이는 경우   |

#### Sample

```agsl
{
    "status": 400,
    "code": 1000,
    "message": "invalid parameter"
}
```
