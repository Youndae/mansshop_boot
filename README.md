# Man's Shop

## 프로젝트 목적
> 기존 Man's Shop 프로젝트를 Spring Boot 3 이상 버전으로 구현.
> 자바 버전을 17로 올리고 record를 활용해 프로젝트 진행.
> react와 Spring이 분리된 환경이 아닌 하나의 프로젝트에 합쳐서 처리하는 경험.
> 게시판 같은 간단한 CRUD에서 벗어나 좀 더 다양하게 연결되어있는 데이터베이스 구조에서 JPA 사용 및 QueryDSL 사용의 경험.

## 환경
> Back End
>> Spring Boot 3.2.5   
>> JDK 17   
>> Lombok   
>> Spring Data JPA   
>> QueryDSL   
>> javax.mail   
>> OAuth2   
>> Spring Security   
>> JWT
> 
> Front End
>> React 18.3.1   
>> dayjs   
>> http-proxy-middleware   
>> react-dom   
>> react-router-dom   
>> styled-components   
> 
> DataBase
>> MySQL 8.3   
>> Redis(Docker)


## 기능

### 사용자
* 메인화면
  * 상품 분류별 목록 출력
  * 상품 리스트 페이징
  * 상품명 검색
  * 장바구니
    * 장바구니 상품 수량 증감
    * 장바구니 상품 선택 또는 전체 삭제
    * 장바구니 상품 선택 또는 전체 구매
  * 주문 조회(비 로그인시에만 출력. 로그인 시 마이페이지에서 조회 가능)
* 상품 상세
  * 상품 정보 출력
  * 상품 옵션 선택
  * 장바구니 담기
  * 찜하기
  * 선택 상품 결제
  * 상품 리뷰 리스트
  * 상품 문의 목록 및 작성
* 마이페이지
  * 주문 목록
  * 찜 목록
  * 문의 내역
  * 문의 상세
  * 작성한 리뷰 목록
  * 작성한 리뷰 상세 및 삭제
  * 리뷰 작성
  * 리뷰 수정
  * 정보 수정
* 로그인
  * 회원가입
  * 로컬 로그인 및 OAuth2 로그인(google, kakao, naver)
  * 아이디 및 비밀번호 찾기

<br/>

## 비회원
* 메인화면
  * 메인화면의 모든 기능 사용 가능
* 장바구니
  * 쿠키를 활용. 장바구니 모든 기능 사용 가능
* 상품 상세
  * 찜하기, 리뷰, 상품 문의를 제외한 모든 기능 사용 가능

<br />

## 관리자
* 상품 관리
  * 상품 목록
  * 상품 추가
  * 상품 상세
  * 상품 수정
  * 상품 옵션 추가
  * 상품 재고 관리
  * 상품 할인 설정
  * 상품 카테고리 설정
* 주문 관리
  * 미처리 주문조회(특정 시간 이전 주문건. ex) 오후 4시 이전 주문 당일 배송이라면 조회일 기준 4시 이전 주문건 중 미처리 주문의 목록을 조회)
  * 전체 주문 목록
* 문의 관리
  * 상품 문의 목록
  * 상품 문의 삭제
  * 회원 문의 목록
  * 회원 문의 상세
  * 회원 문의 카테고리 설정
* 회원 관리
  * 회원 목록
  * 회원 상세
    * 이름 또는 닉네임
    * 연락처
    * 주문 목록
    * 보유 포인트 및 포인트 지급
* 매출 관리
  * 기간별 매출
    * 일, 월, 연도별 매출 리스트(selectbox로 선택)
    * 기간별 매출 상세(해당 기간의 전체 주문 목록 리스트 및 요약)
  * 상품별 매출
    * 상품별, 분류별 매출 리스트(selectbox로 선택)
    * 상품별 매출 상세(상품 판매량, 옵션별 판매량)
    * 상품명 검색

---

## History

### 2024/05/20
> 프로젝트 설계   
> 테이블 설계 및 기능 정리.   
> 추가할 기능들 고민해서 몇가지 추가.   
> 추가된 기능
> * OAuth2 로그인
> * 회원이 포인트를 보유하고 사용할 수 있도록 기능 추가
> * 관리자
>   * 회원 문의 분류 추가
>   * 회원문의, 상품문의 분리되어 있던 것 문의 내역으로 통합하고 하위 카테고리 생성
>   * 회원 문의에서 문의 타입에 대한 카테고리를 관리자가 직접 생성할 수 있도록 처리
>   * 상품 관리 하나만 있던 탭을 하위 카테고리 생성
>   * 상품 관리에서 재고 관리 탭을 통해 10개 이하인 재고들 리스트를 확인할 수 있도록 추가
>   * 상품 할인 설정을 할 수 있도록 추가
>   * 상품 분류를 추가 및 삭제 할 수 있도록 기능 추가
>   * 주문 관리에서 최근 쇼핑몰 기능처럼 특정 시간까지 주문한 내역만 볼 수 있도록 기능 추가
>   * 회원 관리 중 회원 상세에서 포인트 지급을 할 수 있도록 기능 추가
>   * 기간별 매출을 일, 월, 연도별로 볼 수 있고, 상세 내역을 볼 수 있도록 기능 추가
>   * 상품별 매출과 분류별 매출을 선택해 볼 수 있도록 추가.
>   * 상품별 매출 상세 목록으로 상품 판매량, 상품의 옵션별 판매량을 확인할 수 있도록 기능 추가

<br />

### 2024/05/21
> 5/21 프로젝트 생성 및 간단 테스트   
> SpringBoot 프로젝트 생성 후 내부에 src/main 하위에 frontend Directory 생성 후 그 안에 CRA를 통한 React 프로젝트 생성.   
> TestComponent와 TestController 생성해서 1차 테스트 확인.   
> Build 이후 정상적으로 연동이 되었는지 2차 테스트 확인.

<br />

### 2024/05/22
> Gradle 의존성 추가.   
> Security, properties, redis, aop 설정 파일 생성   
> 세션 테스트 하기 위해 MemberController와 MemberRepository 생성   
> OAuth2 응답 처리할 interface와 record 생성   
> OAuthProvider, Role enum 생성   
> file 경로 담아둘 properties 생성   
> oauth.yml 내용 작성   

<br />

### 2024/05/24
> 인증 / 인가 JWT로 수정.   
> filePath 처럼 jwt 관련 값들에 대해서 properties 생성   
> TokenProvider 작성중   
> React, Spring 통합 빌드 중 발생했던 url 입력 시 404 white label 페이지 출력 문제 해결   
>> WebController에서 ErrorController 를 implements해서 해결하는 방법과 ErrorPageRegistrar Bean을 통해 처리하는 방법 두가지를 확인.   
>> 둘다 일단 작성은 해두고 ErrorPageRegistrar의 경우 주석처리 해둔 상태.   
>> 좀 더 테스트해보고 결정할 계획.   
> TokenProvider 처리 중 반환되는 결과에 대해 Result enum 생성. TokenProvider가 아니더라도 다른 곳에서 String 반환에 대한 대응이 필요한 경우 추가적으로 작성해서 사용할 계획.

<br />

### 2024/05/27
> JWT Filter 작성 및 설정 완료.   
> JWTAuthorizationFilter에서 Value Annotation null로 출력되는 문제 해결.   
> Component로 설정 후 Security Config에서 new를 통해 새로 생성하는 것이 아닌 bean 주입으로 처리하는 방법으로 문제 해결.   
> 발생한 문제로는 JWTAuthorizationFilter에 Component Annotation 붙여준 뒤 부터   
> java.lang.NullPointerException: Cannot invoke "org.apache.commons.logging.Log.isDebugEnabled()" because "this.logger" is null
> 이런 오류가 발생하면서 서버 실행이 불가. Annotation이나 설정 문제인 줄 알았으나 AOP 설정의 Around execution에서 발생하는 문제로 확인.      
> execution을 수정해주니 정상적으로 서버 실행 및 기능 수행.   
> 아직 남은 문제는 execution이 mansshop_boot 패키지 하위 모든 메소드에 대한 처리를 하도록 설정할 수 없다는 것.   
> 기존 설정처럼 execution(* com.example.mansshop_boot..*(..)) 이렇게 처리하는 경우 동일한 오류가 발생.      
> 여기서 execution(* com.example.mansshop_boot..*(*)) 이렇게 설정해 1개 이상의 매개변수를 갖는 메소드로 수정하게 되면 정상적으로 동작.   
> 또는 한단계 아래 패키지까지 작성해 controller나 service 패키지를 지정해주는 경우 정상적으로 처리가 되는 것을 확인.   
> execution 작성하는 방법에 대해 여러 방면으로 알아봤지만 모두 동일하게 처리해주고 있었고, Springboot 3으로 올라오면서 수정된 점이 있나 확인해봤으나 답을 찾지 못함.   
> 당장 꼭 필요한 설정은 아니라서 service와 repository, controller 정도만 걸쳐있으면 되는 상황이기 때문에 일단은 보류해두고 프로젝트 마무리한 뒤 테스트 프로젝트로 체크가 필요.   
> 또 다른 문제점으로는 로그가 제대로 찍히지 않는 문제가 발생.   
> log4j2를 gradle dependencies에 추가하는 것으로 문제는 해결했으나 좀 더 확인이 필요.   
> 
> react는 메인 페이지 작성 완료.   
> UI 구성 테스트를 위해 더미 데이터를 생성해 작성.

<br />

### 2024/05/29
> frontend
>> main(best list), 각 카테고리, 로그인, oAuth2 로그인, 메인페이지 상품 검색, 카테고리 리스트 페이징 구현 및 테스트 완료.   
>> 리스트 정상 출력 확인.   
>> 로그인 후 토큰 정상적으로 반환되는 것 확인.   
>> 상품 검색 정상 처리 확인.   
>> 페이징 정상 동작 확인.   
> 
> backend
>> main(best) 리스트 데이터, new, 카테고리 데이터, 로그인 처리, oAuth2 로그인 처리, 토큰 발급 처리, 통합 빌드 후 view 접근 구현 및 테스트 완료.   
>> 로컬 로그인 시 토큰 정상 응답 확인.   
>> OAuth2 로그인 시 임시 토큰 발행 이후 토큰 요청을 통한 토큰 발행 및 응답 확인.   
>> 메인 페이지 내 카테고리에 따른 리스트 데이터, 상품 검색에 따른 리스트 데이터 응답 확인.   
>> 통합 빌드 후 view 접근 확인. (발생 이슈 git issue에 추가 작성)
>> 상품 상세 페이지 반환 데이터 처리 로직 작성 및 Test class에서 Repository 조회 테스트 완료.

<br />

### 2024/05/30
> frontend
>> 상세 페이지 구현. 정상 출력 확인.   
>> 상세 페이지 옵션 select box onChange 처리 구현 및 테스트 완료.   
>> 썸네일에 마우스 올라가면 대표 썸네일 변경 구현 및 테스트 완료.   
>> 옵션 선택 후 수량 증감 구현. 수량 증감과 그에 따른 총 금액 변경 테스트 완료.   
> 
> backend
>> 상세페이지에서 리뷰, QnA 개수 출력을 위해 totalElements 반환하도록 수정.   
>> 리뷰와 QnA 답변에 대한 처리를 위해 groupId와 Step 필드 추가.   
>> 답변의 경우 많은 양이 작성되는 경우가 드물다고 생각해 테이블 분리를 하지 않고 계층형 처럼 같은 GroupId를 갖고 작성되는 순서대로 Step값을 갖도록 처리하는 것으로 설계.   
>> 해당 처리를 위한 Repository 및 쿼리 수정.