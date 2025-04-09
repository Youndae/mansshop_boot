# Man's Shop

---

# 목적
* 기존 Man's Shop 프로젝트를 Spring Boot 3 이상 버전으로 구현.
* 자바 버전을 17로 올리고 record를 활용해 프로젝트 진행.
* Java Stream을 최대한 활용
* react와 Spring이 분리된 환경이 아닌 하나의 프로젝트에 합쳐서 처리하는 경험.
* 게시판 같은 간단한 CRUD에서 벗어나 좀 더 다양하게 연결되어있는 데이터베이스 구조에서 JPA 사용 및 QueryDSL 사용의 경험.

## JSP 버전 github
* https://github.com/Youndae/mansShop

<br />

# 프로젝트 정보

<br />

## 구조
* 통합빌드로 처리 된 하나의 서버
* FrontEnd 위치
  * src/main/frontend

<br />

## 환경
* BackEnd Server
  * Spring Boot 3.2.5
  * JDK 17
  * Gradle
  * Spring Security
  * JWT
  * Spring Data JPA
  * QueryDSL
  * OAuth2 (Google, Kakao, Naver)
  * Lombok
  * iamport-rest-client (아임포트 결제 API)
  * Java Mail
  * commons-io
  * RabbitMQ 3.12 Management (Docker)
  * Swagger ( springdoc-openapi 2.6.0)
* FrontEnd
  * react 18.3.1
  * react-cookie
  * react-dom
  * react-router-dom
  * react-redux
  * redux
  * redux-persist
  * styled-components
  * http-proxy-middleware
  * dayjs
  * Axios
  * react-daum-postcode (Kakao 우편번호 서비스 API)
* DataBase
  * MySQL 8.3.0 (Docker)
  * Redis(Docker)

<br />

## 배포 환경
* AWS
  * EC2
  * ALB
  * RDS(MySQL)
  * S3
  * IAM
  * ElastiCache
  * ACM
  * Route53
* Nginx
* Jenkins
* GitHub webhook

<br />

## ERD
<img src="src/main/resources/README_image/new_ERD.jpg">

<br/>

## workflow
<img src="src/main/resources/README_image/architecture_flow.jpg">

<br />

## 기능
* 메인화면
  * BEST, NEW, 상품 분류별 목록 출력
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
  * 관심상품 등록
  * 선택 상품 수량 증감
  * 선택 상품 결제
  * 상품 리뷰 리스트
  * 상품 문의 목록 출력 및 작성
* 마이페이지
  * 주문 목록
    * 배송완료 상품 리뷰 작성
  * 관심 상품 목록
  * 문의 내역
    * 상품 문의 내역
      * 문의 상세 정보
      * 문의 삭제
    * 회원 문의 내역
      * 문의 작성
      * 문의 상세 정보
        * 답변 작성
        * 문의 삭제
  * 작성한 리뷰 목록
    * 작성한 리뷰 상세 및 삭제
    * 리뷰 수정
  * 정보 수정
* 로그인
  * 회원가입
  * 로컬 로그인 및 OAuth2 로그인(google, kakao, naver)
  * 아이디 및 비밀번호 찾기
  * 로그아웃
* 관리자
  * 상품관리
    * 상품 목록
    * 상품 추가
    * 상품 상세 정보
    * 상품 수정
    * 재고 관리
    * 할인 설정
  * 주문 관리
    * 미처리 주문 목록
      * 주문 상세 정보
      * 미처리 주문 확인 처리
      * 미처리 주문 검색(받는사람, 아이디)
    * 전체 주문 목록
      * 주문 상세 정보
      * 전체 주문 검색(받는사람, 아이디)
  * 문의 관리
    * 상품 문의
      * 미답변 문의 목록
      * 전체 문의 목록
      * 문의 상세 정보
      * 문의 답변 작성
      * 문의 답변 처리
      * 문의 검색(닉네임)
    * 회원 문의
      * 미답변 문의 목록
      * 전체 문의 목록
      * 문의 상세 정보
      * 문의 답변 작성
      * 문의 답변 처리
      * 문의 검색(닉네임)
    * 문의 카테고리 설정
      * 카테고리 추가
      * 카테고리 삭제
    * 회원 관리
      * 회원 목록
      * 회원 상세 정보
      * 회원 주문 목록 조회
      * 회원 상품 문의 내역 조회
      * 회원 문의 내역 조회
      * 포인트 지급
    * 매출 관리
      * 기간별 매출
        * 당해 월별 매출 목록
        * 당해 매출, 판매량, 주문량 출력
        * 월 매출 상세 정보
        * 상품 분류별 월 매출 내역
        * 일별 매출 내역
        * 일별 주문 목록
      * 상품별 매출
        * 상품별 매출 정보 리스트
        * 상품 매출 상세 정보
        * 옵션별 매출 내역
        * 검색(상품명)
* 비회원
  * 메인 화면 모든 기능
  * 장바구니 사용(쿠키 활용)
  * 상품 주문
  * 주문 내역 조회(받는사람, 연락처)

<br/>

## 기능 및 개선 내역

### 목차
* 백엔드
  * JMeter 테스트 수행 및 결과
  * OAuth2 처리
  * 인증 / 인가
  * 주문 데이터 처리 개선 ( RabbitMQ )
  * 쿼리 튜닝
  * count 쿼리 캐싱
  * 관리자의 상품 추가 및 수정, 파일관리
  * S3 연결로 인한 이미지 출력 처리
  * 비밀번호 찾기
* 프론트 엔드
  * 상품 구매 요청 시 데이터 처리
  * Axios Interceptor
  * 상품 옵션 추가 폼 처리

<br/>

## 백엔드 기능

<br/>

### JMeter 테스트 수행 및 결과
<br />

리팩토링 이후 JMeter를 통한 테스트를 진행했습니다.   
테스트 목적은 기능에 대한 성능 최적화입니다.   
사용하지 않는 구형 노트북에 Ubuntu 24.04.01을 설치하고 Docker를 통한 MySQL, Redis, RabbitMQ를 사용하는 환경에서 서버를 실행했습니다.   
JMeter는 데스크탑 환경에서 실행해 테스트를 진행했습니다.   
테스트 데이터로는 사용자 및 상품 데이터 2000개 가량, 그 외 테이블은 250만개 이상의 더미데이터를 담아두고 테스트했습니다.   

애플리케이션 기획에 맞게 관리자를 제외한 기능은 Thread 500, Ramp-up 5, Loop count 1 환경에서 테스트를 진행했으며, 관리자는 사용자 기능에 비해 요청이 적다는 점을 감안해 Thread 20, Ramp-up 1 환경에서 테스트했습니다.   
500 Thread로 설정한 이유는 테스트 이전 수치를 계속 바꿔가며 테스트해본 결과 500 Thread를 넘어가는 경우 비정상적으로 테스트 시간이 증가되는 것을 확인했기에 가장 적합한 최대치라고 생각했기 때문입니다.   

하단의 이미지는 테스트에서 사용한 테스트 목록과 결과를 정리한 엑셀 파일 중 일부입니다.   
대부분 관리자 기능에서 큰 지연시간이 많이 발생했기 때문에 관리자 기능 위주로 자료를 준비했습니다.   

테스트 통과 기준은 평균 500ms대 까지로 잡았고, 그에 따른 최적화를 수행했습니다.   
최적화는 데이터베이스 인덱스 설정, 쿼리 튜닝, 코드 레벨에서의 데이터 파싱 개선을 중점적으로 수행했고, 모든 요청에 대해 목표를 달성할 수 있었습니다.   

이번 JMeter 테스트를 통해 브라우저 및 테스트 코드를 통한 단일 테스트에서 정상적이더라도 운영 서버에서는 그렇지 않을 수 있따는 점을 알 수 있었고, 쿼리 튜닝과 쿼리 작성에 대해 더 깊게 고민할 수 있는 좋은 기회가 되었습니다.

<img src="src/main/resources/README_image/jmeter_2.jpg">

<br />

### OAuth2 처리
<br />

<img src="src/main/resources/README_image/login_view.jpg">

로그인 처리로는 페이지에서 회원 가입후 로그인하는 로컬 로그인과 OAuth2 로그인으로 처리했습니다.   
Google, Naver, Kakao 세가지로 처리했으며 요청은 window.location.href를 통해 프론트에서 요청하도록 했습니다.

```javascript
const handleOAuth = (e) => {
    const OAuthClient = e.target.name;
    
    window.sessionStorage.setItem('prev', state.toString());
    
    if(oAuthClient === 'google')
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    else if(oAuthClient === 'naver')
        window.location.href = 'http://localhost:8080/oauth2/authorization/naver';
    else if(oAuthClient === 'kakao')
        window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
}
```

클라이언트에서는 로그인 페이지 접근 시 처리 후 이동할 페이지의 정보를 state에 담아 전달합니다.   
로컬로그인이라면 요청 응답을 받은 뒤 해당 state의 값으로 바로 이동하도록 처리할 수 있지만 OAuth2 로그인의 경우 href로 요청하기 때문에 응답을 받을 수 없어 sessionStorage에 담아두었다가 추후 처리하게 됩니다.

서버에서는 OAuth2 인증 정보들을 application-oauth.yml에 저장해두었고 SimpleUrlAuthenticationSuccessHandler, OAuth2User, DefaultOAuth2UserService를 Custom해 처리할 수 있도록 했습니다.   
SecurityFilterChain Bean에서는 oauth2Login 설정을 통해 OAuth2UserService와 SuccessHandler를 처리할 수 있도록 했습니다.   
CustomOAuth2User의 경우 현재 받을 수 있는 사용자 이메일, 이름(카카오의 경우 닉네임)을 받아 처리합니다.

```java
//CustomOAuth2UserService
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response oAuth2Response = null;

    if(registrationId.equals(OAuthProvider.GOOGLE.getKey()))
      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
    else if(registrationId.equals(OAuthProvider.NAVER.getKey()))
      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
    else if(registrationId.equals(OAuthProvider.KAKAO.getKey()))
      oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

    String userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    Member existsData = memberRepository.findById(userId).orElse(null);

    if(existsData == null) {
      Member member = OAuth2ResponseEntityConverter.toEntity(oAuth2Response, userId);
      member.addMemberAuth(
              Auth.builder()
                      .auth(Role.MEMBER.getKey())
                      .build()
      );

      memberRepository.save(member);

      existsData = member;
    }else {
      existsData.setUserEmail(oAuth2Response.getEmail());
      existsData.setUserName(oAuth2Response.getName());

      memberRepository.save(existsData);
    }

    OAuth2DTO oAuth2DTO = new OAuth2DTO(existsData);

    return new CustomOAuth2User(oAuth2DTO);
  }
}
```

Authorization Server 요청과 응답은 SpringSecurity를 통해 요청을 보내고 응답을 받아 OAuth2UserRequest로 Service에서 사용하는 방법을 택했습니다.   
응답받은 데이터는 각 Provider에 맞는 OAuth2Response record 인스턴스를 생성하게 되고 해당 데이터를 통해 데이터베이스에 사용자 정보를 저장합니다.   
이후 OAuth2DTO에 데이터를 담아 CustomOAuth2User를 생성하며 처리가 완료된다면 CustomOAuth2SuccessHandler에 접근합니다.

```java
// CustomOAuth2SuccessHandler

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JWTTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request
                                      , HttpServletResponse response
                                      , Authentication authentication) 
                                                  throws IOException, ServletException {
    CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String userId = customOAuth2User.getUserId();
    jwtTokenProvider.createTemporaryToken(userId, response);

    response.sendRedirect("/oAuth");
  }
}
```

SuccessHandler에서는 전달받은 OAuth2User를 통해 사용자 아이디를 알아내고 임시 토큰을 생성한 뒤 응답 쿠키에 담아 /oAuth로 Redirect 하게 됩니다.   
/oAuth에 해당하는 컴포넌트 내부에서는 임시 토큰을 보내 토큰 발급을 요청하게 되고 서버에서는 임시 토큰 검증과 토큰 발급 처리 후 응답합니다.   
정상적인 응답이 도달한다면 AccessToken은 LocalStorage에 저장되고 OAuth2 로그인 요청 시 SessionStorage에 저장해두었던 이전 페이지 정보를 가져와 해당 경로로 이동할 수 있도록 처리했습니다.   
SessionStorage에 저장해두었던 이전 페이지 정보는 페이지 이동 전 삭제하도록 처리했습니다.

OAuth2를 적용하면서 아쉬웠던 점은 href를 통한 요청이기 때문에 응답을 받을 수 없다는 점이었습니다.   
서버의 특정 주소로 요청을 보내도록 해 처리하는 방법으로도 테스트를 해봤지만 원하는 방향으로 해결할 수 없어 비어있는 컴포넌트를 하나 생성해 그 안에서 처리하게 된 점이 아쉽습니다.

<br />

### 인증 / 인가
<br />

인증 / 인가에 대한 처리는 JWT와 SpringSecurity를 같이 사용했습니다.   
Spring Security는 SecurityContextHolder를 통해 인증 객체를 담아주고 컨트롤러 혹은 메소드 단위로 @PreAuthorize Annotation을 사용해 처리했습니다.   
JWT의 경우 AccessToken과 RefreshToken을 사용했으며 Refresh Token Rotation 방식으로 재발급시마다 두 토큰을 모두 재발급하도록 설계했습니다.   
추가적으로 ino라는 쿠키를 하나 같이 생성해서 클라이언트에게 전달하는데 다중 디바이스 로그인을 처리하기 위해 만들게 되었습니다.
Identifier Number라는 의미로 식별자로서의 역할을 하며 토큰 탈취를 감별하는데도 사용합니다.   
ino를 통해 탈취에 대응하는 방법으로는 9999일이라는 아주 긴 만료시간을 갖는 쿠키로 전달되기 때문에 사용자가 로그아웃을 하지 않는 이상 삭제되지 않습니다.   
만약 탈취된 토큰이 재발급이 수행되었다고 하더라도 사용자가 재 접근 했을 때 ino가 동일한 다른 데이터가 존재하는 것을 체크하기 때문에 일치하지 않는 데이터라면 탈취로 판단할 수 있어 대응할 수 있다고 생각해 설계했습니다.   
토큰 관리는 클라이언트의 경우 AccessToken은 Local Storage에 저장하며, RefreshToken과 ino는 Cookie에 저장하게 되며, 서버에서는 Redis에 AccessToken과 RefreshToken을 저장해 관리하게 됩니다.

```java
@Override
protected void doFilterInternal(HttpServletRequest request
                                , HttpServletResponse response
                                , FilterChain chain) throws ServletException, IOException {

    String accessToken = request.getHeader(accessHeader);
    Cookie refreshToken = WebUtils.getCookie(request, refreshHeader);
    Cookie inoToken = WebUtils.getCookie(request, inoHeader);
    String username = null; // Authentication 객체 생성 시 필요한 사용자 아이디

    if(inoToken != null){
        String inoValue = inoToken.getValue();
        if(accessToken != null && refreshToken != null) {
            String refreshTokenValue = refreshToken.getValue();
            String accessTokenValue = accessToken.replace(tokenPrefix, "");

            if(!jwtTokenProvider.checkTokenPrefix(accessToken)
                    || !jwtTokenProvider.checkTokenPrefix(refreshTokenValue)){
                chain.doFilter(request, response);
                return;
            }else {
                String claimByAccessToken = jwtTokenProvider.verifyAccessToken(accessTokenValue, inoValue);

                if(claimByAccessToken.equals(Result.WRONG_TOKEN.getResultKey())
                    || claimByAccessToken.equals(Result.TOKEN_STEALING.getResultKey())){
                    jwtTokenService.deleteCookieAndThrowException(response);
                    return;
                }else if(claimByAccessToken.equals(Result.TOKEN_EXPIRATION.getResultKey())){
                    if(request.getRequestURI().equals("/api/reissue")) {
                        chain.doFilter(request, response);
                    }else
                        jwtTokenService.tokenExpirationResponse(response);

                    return;
                }else {
                    username = claimByAccessToken;
                }
            }
        }else if(accessToken != null && refreshToken == null){
            String decodeTokenClaim = jwtTokenProvider.decodeToken(accessToken.replace(tokenPrefix, ""));

            jwtTokenService.deleteTokenAndCookieAndThrowException(decodeTokenClaim, inoValue, response);
            return;
        }else {
            chain.doFilter(request, response);
            return;
        }
    }

    if(username != null){
        Member memberEntity = memberRepository.findById(username).get();
        String userId;
        Collection<? extends GrantedAuthority> authorities;
        CustomUserDetails userDetails;

        if(memberEntity.getProvider().equals("local"))
            userDetails = new CustomUser(memberEntity);
        else
            userDetails = new CustomOAuth2User(
                                memberEntity.toOAuth2DTOUseFilter()
                            );

        userId = userDetails.getUserId();
        authorities = userDetails.getAuthorities();

        Authentication authentication =
        new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
}
```

SecurityFilterChain 설정을 통해 JWTAuthorizationFilter를 먼저 거치게 되는데 가장 먼저 ino의 존재 여부를 체크합니다.   
Token 데이터는 Redis에 저장하고 있는데 Redis 데이터의 키값 구조는 각 토큰의 약자인 'at 또는 rt' + ino + userId 구조로 저장합니다.   
그래서 ino가 존재하지 않는다면 Redis 데이터를 조회할 수 없고 토큰이 유효한지만 체크할 수 있기 때문에 검증을 수행하지 않습니다.

ino가 존재한다면 그 다음 체크하는 것은 토큰이 모두 존재하는지, RefreshToken만 존재하는지, 아니면 전부 다 없거나 AccessToken만 존재하는지를 체크합니다.   
모두 존재한다면 모든 토큰의 prefix 체크 후 AccessToken만을 검증하게 되고 Claim에 담겨있는 사용자 아이디와 메소드 호출시 같이 전달된 ino의 값을 통해 Redis 데이터와 비교합니다.   
이때 검증 시 TokenDecodeException이 발생한다면 이후 처리를 진행하지 않고 잘못된 토큰이라는 응답을 클라이언트에게 보내며 모든 응답 쿠키에 대해 만료기간을 0으로 처리해 삭제되도록 합니다.   
토큰 검증은 정상적으로 처리되었으나 Redis 데이터와 일치하지 않거나 데이터가 존재하지 않는 경우에는 탈취로 판단해 해당 ino의 Redis 데이터와 응답 쿠키를 모두 삭제하도록 처리하고 탈취 응답을 클라이언트에게 반환합니다.

AccessToken은 존재하지 않지만 RefreshToken만 존재하는 경우는 수정된 코드입니다.   
프로젝트 기능이 마무리되고 통합빌드 이후 테스트 과정에서 새로고침이나 url 입력을 통한 페이지 이동에서 탈취 응답이 계속 반환되는 문제가 있었습니다.   
통합빌드 이전에는 프론트 엔드 서버의 포트로 접근해 요청을 보내다보니 컴포넌트에 먼저 접근해 문제가 없었지만 빌드 이후에는 백엔드 서버 포트로 접근하다보니 최초 접근 위치가 백엔드 서버가 되어 쿠키에 있는 RefreshToken과 ino는 알아서 넘어왔지만 LocalStorage에 저장된 AccessToken이 전달되지 않아 발생한 문제였습니다.   
이 문제를 어떻게 해결할지 여러 방향으로 생각해보며 고민을 해봤는데 이미 설계된 상태에서 토큰 여부 체크만 바꿔주면 간단하게 해결할 수 있었습니다.   
현재 코드처럼 AccessToken만 존재하지 않는 경우이더라도 오류를 반환하는 것이 아니라 검증을 하지 않고 이후 처리를 진행하도록 하는 것이었습니다.

탈취에 대응하기 위해 JWT 설계는 다음과 같이 진행했습니다.
1. Redis에 저장되는 AccessToken 데이터는 RefreshToken의 만료기간과 같은 기간을 갖고 보관한다.
2. 클라이언트에서는 재발급을 요청할 때 RefreshToken과 ino만 전달하는 것이 아닌 AccessToken도 같이 전달한다.
3. 이때, AccessToken의 만료로 인해 잘못된 토큰이라는 응답이 올 것을 고려해 요청 url이 재발급인 경우 이후 처리를 수행할 수 있도록 한다.
4. 모든 요청에 대한 검증은 AccessToken만 검증하며 다른 토큰은 존재 여부와 prefix만 체크한다.

이러한 설계 구조에서 RefreshToken과 ino만 전달되었다고 하더라도 권한이 필요한 페이지에 접근이 불가능하고 재발급 역시도 처리될 수 없기 때문에 문제될 점이 없었습니다.   
이 문제가 발생한 이유는 처음 설계하고 검증 코드를 계획할 때 여러 상황에 대한 고려를 제대로 하지 못했기 때문이라고 생각합니다.   
JWT를 사용하게 되면서 탈취에 충분하게 대응할 수 있는 방법을 계속 고려하며 설계했는데 충분한 대응을 할 수 있는 것만을 바라보고 다양한 상황에 대한 변수는 체크하지 못해 발생한 문제라고 생각이 들었습니다.


<br />

### 주문 데이터 처리 개선 ( RabbitMQ )
<br />

상품의 결제 API로 I'mport 결제 API를 사용했으며, 주소지 API는 Kakao 우편번호 서비스 API를 사용했습니다.
주문 데이터 처리 과정은 주문 및 상세 데이터 저장, 장바구니를 통한 결제인 경우 장바구니에서 해당 상품 데이터 삭제, 상품 판매량 수정, 구매 상품 옵션의 재고 수정, 집계 테이블 수정으로 이루어져 있습니다.   
기존 JSP 버전에서는 매출 집계 테이블이 1개였고, 비교적 간결한 구조로 되어있었는데 Spring Boot 버전으로 재구현하면서 다양한 매출 데이터를 보여주도록 수정하며 필요성이 사라졌습니다.   
그래서 주문 및 상세 테이블에서의 집계만으로 충분할 것이라고 생각해 집계 테이블이 없는 구조로 개선했는데 단일 요청에서는 정상적인 성능을 보였으나, JMeter를 통한 다중 요청 테스트에서는 큰 지연시간이 발생되는 것을 볼 수 있었습니다.   
심한 경우 몇십분 간의 락이 걸리는 큰 문제였기 때문에 다시 집계 테이블을 만들게 되었고 이번에는 일별, 상품 옵션별 집계 테이블로 나눠 추가했습니다.   

이렇게 처리하는 것으로 1차적인 문제는 해결했으나 주문 데이터 처리 과정이 너무 많아진다는 단점이 있었습니다.   
또한, 사용자에게 필요하지 않은 판매량, 재고, 집계 처리가 같이 처리되어야 한다는 점에서 비효율적이라고 생각했고 분리할 방법에 대해 고민하게 되었습니다.   
해결 방안으로 배치 처리와 비동기 처리가 있다고 생각하게 되었고, 배치 처리는 매출을 실시간으로 확인할 수 있어야 한다는 기능 설계에 맞지 않다고 생각해 제외했습니다.   
비동기 처리로 전환하는 방법으로는 @Async Annotation을 사용하는 방법과 Meesage Queue를 사용하는 방법으로 나눌 수 있다고 생각했기에 Message Queue에 대해 알아보게 되었습니다.   
알아보고 테스트 해본 결과 @Async를 사용하게 되면 의존성 추가나 설정등이 비교적 간단하기 때문에 손쉽게 사용할 수 있있다는 점이 장점이었으나, 비동기 처리 과정 중 발생하는 오류에 대해 대응하는 것이 Message Queue 방식에 비해 복잡하다고 생각했습니다.   
처리가 실패한 경우 재시도를 수행하도록 할 수 있으나, 일정 시간 이상의 오류 상태가 지속된 경우 결국 로그를 확인해서 처리해야 한다는 점이 아쉬웠습니다.   
Message Queue 중에서 RabbitMQ를 중점적으로 알아보고 결정하게 되었는데 RabbitMQ의 경우 의존성 추가 및 큐 관리가 필요하다는 단점이 존재하지만, 실패 메시지를 따로 보관해 장기적인 오류에도 재시도를 수행할 수 있다는 점이 장점이었습니다.   
특히, 실패 메시지를 개발자가 직접 로그 확인 후 실행하지 않아도 애플리케이션 UI를 통해 현재 실패 메시지 상태와 재시도 요청을 처리할 수 있도록 할 수 있다는 것이 선택에 큰 이유가 되었습니다.   

이러한 이유로 아래 코드처럼 RabbitMQ를 도입해 주문 처리에 적용했고, 주문 및 상세 데이터만 처리한 뒤 나머지 처리 과정은 RabbitMQ에게 맡겨 사용자는 더욱 빠른 응답을 받을 수 있도록 개선할 수 있었습니다.

```java
// 주문 처리
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
  private final ProductOrderRepository productOrderRepository;

  private final RabbitTemplate rabbitTemplate;

  private final RabbitMQProperties rabbitMQProperties;
    
  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
      ProductOrderDataDTO productOrderDataDTO = createOrderDataDTO(paymentDTO, cartMemberDTO);
      ProductOrder order = productOrderDataDTO.productOrder();
      productOrderRepository.save(order);
      
      String orderExchange = rabbitMQProperties.getExchange()
                                              .get(RabbitMQPrefix.EXCHANGE_ORDER.getKey())
                                              .getName();

      //주문 타입이 cart인 경우 장바구니에서 선택한 상품 또는 전체 상품 주문이므로 해당 상품을 장바구니에서 삭제.
      if(paymentDTO.orderType().equals("cart"))
          rabbitTemplate.convertAndSend(
                  orderExchange,
                  getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_CART),
                  new OrderCartDTO(cartMemberDTO, productOrderDataDTO.orderOptionIds())
          );

      // ProductOption의 재고 수정
      rabbitTemplate.convertAndSend(
              orderExchange,
              getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_PRODUCT_OPTION),
              productOrderDataDTO.orderProductList()
      );
  
      // Product의 salesQuantity 수정
      rabbitTemplate.convertAndSend(
              orderExchange,
              getQueueRoutingKey(RabbitMQPrefix.QUEUE_ORDER_PRODUCT),
              productOrderDataDTO.orderProductList()
      );
  
      // Period Summary 처리
      rabbitTemplate.convertAndSend(
              orderExchange,
              getQueueRoutingKey(RabbitMQPrefix.QUEUE_PERIOD_SUMMARY),
              new PeriodSummaryQueueDTO(order)
      );
  
      // Product Summary 처리
      rabbitTemplate.convertAndSend(
              orderExchange,
              getQueueRoutingKey(RabbitMQPrefix.QUEUE_PRODUCT_SUMMARY),
              new OrderProductSummaryDTO(productOrderDataDTO)
      );
  }

  private String getQueueRoutingKey(RabbitMQPrefix rabbitMQPrefix) {
    return rabbitMQProperties.getQueue()
            .get(rabbitMQPrefix.getKey())
            .getRouting();
  }

  public ProductOrderDataDTO createOrderDataDTO(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO) {
    ProductOrder productOrder = paymentDTO.toOrderEntity(cartMemberDTO.uid());
    List<OrderProductDTO> orderProductList = paymentDTO.orderProduct();
    List<String> orderProductIds = new ArrayList<>();// 주문한 상품 옵션 아이디를 담아줄 리스트
    List<Long> orderOptionIds = new ArrayList<>();
    int totalProductCount = 0;// 총 판매량
    //옵션 정보 리스트에서 각 객체를 OrderDetail Entity로 Entity화 해서 ProductOrder Entity에 담아준다.
    //주문한 옵션 번호는 추후 더 사용하기 때문에 리스트화 한다.
    //총 판매량은 기간별 매출에 필요하기 때문에 이때 같이 총 판매량을 계산한다.
    for(OrderProductDTO data : paymentDTO.orderProduct()) {
      productOrder.addDetail(data.toOrderDetailEntity());
      if(!orderProductIds.contains(data.productId()))
        orderProductIds.add(data.productId());
      orderOptionIds.add(data.optionId());
      totalProductCount += data.detailCount();
    }
    productOrder.setProductCount(totalProductCount);

    return new ProductOrderDataDTO(productOrder, orderProductList, orderProductIds, orderOptionIds);
  }
}
```

RabbitMQ를 사용하면서 필요한 exchange와 Queue, DLQ 의 이름과 Routing Key는 모두 rabbitMQ.yml에 작성해두고 RabbitMQProperties 객체에 담아 사용하며, RabbitMQPrefix라는 Enum 클래스를 같이 활용해 직접 작성하는 경우를 최소화했습니다.   
yml은 git Ignore에 등록했지만, RabbitMQProperties와 config, consumer는 아래 각 링크들을 통해 확인하실 수 있습니다.

<a href="https://github.com/Youndae/mansshop_boot/blob/master/src/main/java/com/example/mansshop_boot/domain/dto/rabbitMQ/RabbitMQProperties.java">RabbitMQProperties</a>   
<a href="https://github.com/Youndae/mansshop_boot/blob/master/src/main/java/com/example/mansshop_boot/config/rabbitMQ/config/RabbitMQConfig.java#L18">RabbitMQConfig</a>   
<a href="https://github.com/Youndae/mansshop_boot/blob/master/src/main/java/com/example/mansshop_boot/config/rabbitMQ/consumer/OrderConsumer.java">OrderConsumer</a>

<br />

### 쿼리 튜닝
<br />

다중 요청 테스트 이후 쿼리 최적화를 수행하다보니 인덱스로는 한계가 있는 쿼리들이 존재했습니다.   
이 쿼리들의 공통점은 Pagination을 위한 LIMIT이 붙거나, 여러 테이블과 JOIN이 발생한다는 점이었습니다.   
대부분 이 쿼리들은 관리자 기능에 존재했는데 사용자는 자신과 관련된 데이터만 조회하는 것 처럼 조건이 붙어있지만, 관리자는 전체를 조회하는 경우가 많기 때문이었습니다.   

문제점 파악을 위해 EXPLAIN ANALYZE를 통해 확인했을 때 기준이 되는 테이블의 모든 데이터에 대해 JOIN이 처리된다는 점이 가장 큰 문제점이라는 것을 알 수 있었습니다.   
이 문제를 해결하기 위해 몇가지 테스트를 해봤는데 그 중 FROM 절에서 Sub Query를 사용해 JOIN되는 양을 줄이는 것이 가장 효과적인 방법이라는 것을 알 수 있었습니다.   
모든 데이터베이스 요청에 대해 QueryDSL을 사용하고 있었는데 QueryDSL에서는 FROM 절의 Sub Query를 사용하는 것이 어렵다는 것을 알게 되었습니다.   
그래서 이 쿼리들에 대해서만 Native Query를 사용해 문제를 해결했습니다.   

```java
@Repository
@RequiredArgsConstructor
public class ProductReviewDSLRepositoryImpl implements ProductReviewDSLRepository {
    
  @PersistenceContext
  private EntityManager em;
    
  @Override
  public List<AdminReviewDTO> findAllByAdminReviewList(AdminOrderPageDTO pageDTO, String listType) {
    StringBuilder queryBuilder = new StringBuilder();

    queryBuilder.append("SELECT r.id, ")
            .append(reviewDynamicFieldQuery(pageDTO))
            .append("r.updatedAt, ")
            .append("r.status ")
            .append("FROM ")
            .append(reviewDynamicSubQuery(pageDTO, listType))
            .append(reviewDynamicJoinQuery(pageDTO));

    Query query = em.createNativeQuery(queryBuilder.toString());

    query.setParameter("offset", pageDTO.offset());
    query.setParameter("amount", pageDTO.amount());

    if(pageDTO.keyword() != null)
      query.setParameter("keyword", "%" + pageDTO.keyword() + "%");

    List<Object[]> resultList = query.getResultList();

    return resultList.stream()
            .map(val -> new AdminReviewDTO(
                    ((Number) val[0]).longValue(),
                    (String) val[1],
                    (String) val[2],
                    ((Timestamp) val[3]).toLocalDateTime(),
                    (Boolean) val[4]
            ))
            .toList();
  }
  
  private String reviewDynamicFieldQuery(AdminOrderPageDTO pageDTO) {
    StringBuilder queryBuilder = new StringBuilder();

    if(pageDTO.searchType() == null) {
      queryBuilder.append("p.productName, ")
              .append(memberCaseWhenQuery());
    }else if(pageDTO.searchType().equals("product")){
      queryBuilder.append("r.productName, ")
              .append(memberCaseWhenQuery());
    }else if(pageDTO.searchType().equals("user")) {
      queryBuilder.append("p.productName, ")
              .append("r.userId, ");
    }

    return queryBuilder.toString();
  }

  private String reviewDynamicJoinQuery(AdminOrderPageDTO pageDTO) {
    StringBuilder queryBuilder = new StringBuilder();
    String productJoin = "INNER JOIN product p ON p.id = r.productId ";
    String memberJoin = "INNER JOIN member m ON m.userId = r.userId ";

    if(pageDTO.searchType() == null){
      queryBuilder.append(productJoin)
              .append(memberJoin);
    }else if(pageDTO.searchType().equals("product")){
      queryBuilder.append(memberJoin);
    }else if(pageDTO.searchType().equals("user")){
      queryBuilder.append(productJoin);
    }

    return queryBuilder.toString();
  }

  private String reviewDynamicSubQuery(AdminOrderPageDTO pageDTO, String listType) {
    StringBuilder queryBuilder = new StringBuilder();
    String listTypeCondition = listType.equals("NEW") ? "AND pr.status = 0 " : "";

    queryBuilder.append("( SELECT ")
            .append("pr.id, ")
            .append("pr.updatedAt, ")
            .append("pr.status, ");

    if(pageDTO.searchType() == null) {
      queryBuilder.append("pr.productId, ")
              .append("pr.userId ")
              .append("FROM productReview pr ")
              .append("WHERE 1=1 ");
    }else if(pageDTO.searchType().equals("product")) {
      queryBuilder.append("p.productName, ")
              .append("pr.userId ")
              .append("FROM productReview pr ")
              .append("INNER JOIN product p ")
              .append("ON p.id = pr.productId ")
              .append("WHERE p.productName LIKE :keyword ");
    }else if(pageDTO.searchType().equals("user")) {
      queryBuilder.append(memberCaseWhenQuery())
              .append("pr.productId ")
              .append("FROM productReview pr ")
              .append("INNER JOIN member m ")
              .append("ON m.userId = pr.userId ")
              .append("WHERE (m.userName LIKE :keyword OR m.nickname LIKE :keyword) ");
    }

    queryBuilder.append(listTypeCondition)
            .append("ORDER BY pr.updatedAt DESC ")
            .append("LIMIT :offset, :amount) AS r ");

    return queryBuilder.toString();
  }

  private String memberCaseWhenQuery() {
    return "CASE WHEN (m.nickname is null) THEN m.userName ELSE m.nickname END AS userId, ";
  }
}
```

Native Query를 사용하면서 가장 크게 신경쓰였던 부분은 가독성 부분이었습니다.   
이 문제를 개선하기 위해 StringBuilder를 사용해 최대한 가독성을 높일 수 있도록 노력했습니다.   
조건에 따른 동적 처리를 위해서는 메소드를 분리해 중복되는 코드를 최소화하고 추후 수정시에도 편하도록 유연성을 확보하고자 노력했습니다.

다중 요청 시 리뷰, 문의 등 기본 리스트 조회 성능이 20 ~ 40초 가량 걸렸었는데 이렇게 Sub Query로 쿼리 튜닝을 수행 한 이후 최대 평균 20ms 까지 줄어드는 결과를 확인할 수 있었습니다.   
특히, 관리자 매출 기능에서는 최악의 경우 수십분의 락이 걸리는 경우도 있었는데 평균 500ms 정도로 줄어드는 효율성을 볼 수 있었습니다.


<br />

### count 쿼리 캐싱
<br />

Sub Query 사용을 통해 조회 쿼리의 성능을 높이는데는 성공했지만, Pagination에 사용될 count 쿼리는 여전히 문제였습니다.   
Full scan을 해야 하다보니 상품 문의 테이블 기준 300만개의 데이터가 있음에도 600ms 의 시간이 걸렸습니다.   
여러 인덱스를 추가해보기도 하고 DISTINCT를 사용해보기도 했지만, 이것보다 더 빠른 속도를 볼 수는 없었고 결국 캐싱으로 처리하기로 결정했습니다.   

JWT의 관리로 인해 Redis를 사용하고 있었기 때문에 Redis를 통한 캐싱을 처리하기로 결정했고, 전략은 주기적인 갱신이 아닌 요청 발생시에 확인 후 갱신하는 방법을 택했습니다.   
관리자 요청의 경우 비교적 자주 발생하는 요청이 아니라고 생각했기 때문에 주기적인 갱신으로 리소스를 소비할 필요가 없다고 생각했기 때문입니다.   

캐싱되는 데이터는 조건이 없는 Full Scan 요청입니다. 저장되는 KEY 값의 관리는 Enum을 통해 관리하고 TTL은 yml로 관리할 수 있도록 분리했습니다.   
TTL은 @Value로 가져와서 사용하기보다 CacheProperties 객체에 담아 사용할 수 있도록 처리했습니다.

```java
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final RedisTemplate<String, Long> redisTemplate;
    
    private final CacheProperties cacheProperties;
    
    private Map<String, Function<CacheRequest, Long>> KEY_ACTION_MAP;
    
    @PostConstructor
    void init() {
      KEY_ACTION_MAP = Map.of(
              RedisCaching.ADMIN_PRODUCT_QNA_COUNT.getKey(),
              req -> productQnARepository.findAllByAdminProductQnACount(req.getPageDTO()),
              RedisCaching.ADMIN_MEMBER_QNA_COUNT.getKey(),
              req -> memberQnARepository.findAllByAdminMemberQnACount(req.getPageDTO()),
              RedisCaching.ADMIN_ORDER_COUNT.getKey(),
              req -> productOrderRepository.findAllOrderListCount(req.getPageDTO()),
              RedisCaching.ADMIN_REVIEW_COUNT.getKey(),
              req -> productReviewRepository.countByAdminReviewList(req.getPageDTO(), req.getListType())
      );
    }

    @Override
    public PagingListDTO<AdminReviewDTO> getReviewList(AdminOrderPageDTO pageDTO, AdminListType listType) {
      List<AdminReviewDTO> content = productReviewRepository.findAllByAdminReviewList(pageDTO, listType.name());
      Long totalElements = null;
      
      //count 요청
      if(pageDTO.keyword() == null && listType.equals(AdminListType.ALL))
        totalElements = getFullScanCount(RedisCaching.ADMIN_REVIEW_COUNT, new CacheRequest(pageDTO, listType.name()));
      else
        totalElements = productReviewRepository.countByAdminReviewList(pageDTO, listType.name());
  
      PagingMappingDTO pagingMappingDTO = new PagingMappingDTO(totalElements, pageDTO.page(), pageDTO.amount());
  
      return new PagingListDTO<>(content, pagingMappingDTO);
    }

    public long getFullScanCount(RedisCaching cachingKey, CacheRequest request) {
      String key = cachingKey.getKey();
  
      Long result = redisTemplate.opsForValue().get(key);
      if(result == null){
        synchronized (this) {
          result = redisTemplate.opsForValue().get(key);
          if(result == null) {
            Function<CacheRequest, Long> action = KEY_ACTION_MAP.get(key);
  
            if(action == null)
              throw new IllegalArgumentException("caching Key is Abnormal");
  
            result = action.apply(request);
            long ttl = cacheProperties.getCount().get(key).getTtl();
            redisTemplate.opsForValue().set(key, result, Duration.ofMinutes(ttl));
          }
        }
      }
  
      return result;
    }
}
```

여러 Repository에 대한 처리가 필요하기 때문에 캐싱 처리 메소드 내부에서 조건문에 따라 분리하기 보다 Function 인터페이스를 통해 처리할 수 있도록 했습니다.   
호출하는 메소드에서는 count 쿼리의 조건에 대한 매개변수를 받아야 하기 때문에 CacheRequest라는 클래스를 같이 전달하도록 처리했습니다.   
매개변수가 무조건 1개만 존재하는 경우에는 굳이 CacheRequest 클래스를 만들 필요가 없었지만, 매개변수를 2개 이상 받는 요청이 존재했기 때문에 클래스를 만들어 처리했습니다.

캐싱 메소드 내부에서는 최초 Redis 데이터를 확인하고 존재하지 않는다면 재 확인 이후 데이터베이스에 요청을 보내게 됩니다.   
그리고 두번째 체크부터는 synchronized로 감싸 동시성 제어를 할 수 있도록 처리해 여러 요청이 데이터베이스에 요청을 보낼 수 없도록 처리했습니다.   
그래서 synchronized 내부에 Redis 체크를 한번 더 추가해 최초 조회에서 데이터를 찾지 못했더라도 앞선 요청이 캐싱해둔 데이터를 바로 가져갈 수 있도록 처리했습니다.

<br />

### 관리자의 상품 추가 및 수정, 파일관리
<br />

관리자의 상품 및 수정 과정에서는 파일을 저장 및 삭제하는 처리가 포함되어 있습니다.   
데이터베이스 요청은 @Transactional Annotation을 통해 오류 발생 시 롤백이 될 수 있지만, 파일 저장 및 삭제 처리는 그렇지 않다는 점에서 이 처리를 해결해야 한다고 생각했습니다.   
이 문제를 해결하기 위해 대부분의 처리를 try-catch로 감싸주고 저장되는 파일명들은 saveImages 라는 리스트에 담아 따로 관리하도록 했습니다.   
이후 내부에서 예외가 발생하는 경우 saveImages 리스트에 저장된 모든 파일명들을 통해 저장된 파일들을 삭제할 수 있도록 처리했습니다.   

임시 디렉토리를 사용하는 방안도 있었지만, 그런 경우 추가적인 파일 이동 처리가 필요하고 즉시 반영되어야 한다는 기능 특성 상 시간대 별로 파일 이동을 하는 방법을 사용하기에도 적합하지 않다고 생각했습니다.   
즉시 처리되어야 하는 만큼 여러번의 I/O가 발생하는 것 보다는 문제가 발생했을 때 삭제할 수 있도록 처리하는게 더 효율적이라고 생각해 이 방법을 택하게 되었습니다.   

이 상품 관리 기능에서는 또 하나의 개선 사항이 있었습니다.   
JPA를 사용하는 환경이었기에 Product를 참조하는 ProductOption, ProductThumbnail, ProductInfoImage 엔티티들에 대해 양방향 매핑으로 연관관계를 설정하고 CascadeType.ALL을 통한 동시저장을 처리했었습니다.   
테스트 결과 나눠서 저장하는 것 보다 동시저장이 더 빠르게 처리되기도 하고 단순하게 바라보더라도 데이터베이스 연결 횟수도 1번으로 마무리 할 수 있었기 때문에 더 효율적이라는 생각 때문이었습니다.   
하지만 이 설정으로 인해 Product를 단방향으로 참조하는 다른 엔티티에서 save() 요청 시 위 엔티티들을 모두 JOIN해서 조회한다는 것이 문제가 되었습니다.   
간단한 insert 처리임에도 이 JOIN 과정 때문에 큰 지연시간이 발생하는 것을 확인했고, 이 문제를 회피하기 위해 각 엔티티들을 따로 저장하는 방법으로 개선했습니다.   

그리고 양방향 매핑에 있는 엔티티들을 제외한 다른 곳에서 전혀 참조가 발생하지 않는 엔티티에 대해서는 CascadeType.PERSIST, CascadeType.MERGE를 정의해 동시 저장을 처리할 수 있도록 개선했습니다.   
이 문제에 대해서는 블로그에 상세하게 정리해두었고, 아래 링크를 통해 확인하실 수 있습니다.   
<a href="https://myyoun.tistory.com/242">JPA CascadeType 문제 및 해결</a>

```java
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  @Override
  @Transactional(rollbackFor = Exception.class)
  public String patchProduct(String productId, List<Long> deleteOptionList, AdminProductPatchDTO patchDTO, AdminProductImageDTO imageDTO) {
    Product product = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
    product.setPatchData(patchDTO);
    List<String> saveImages = new ArrayList<>();

    try{
      setProductOptionData(product, patchDTO);
      saveImages = saveProductImage(product, imageDTO);
      String firstThumbnail = setProductFirstThumbnail(product, imageDTO.getFirstThumbnail());

      if(firstThumbnail != null)
        saveImages.add(firstThumbnail);

      productRepository.save(product);
      productOptionRepository.saveAll(product.getProductOptions());
      productThumbnailRepository.saveAll(product.getProductThumbnails());
      productInfoImageRepository.saveAll(product.getProductInfoImages());

      if(deleteOptionList != null)
        productOptionRepository.deleteAllById(deleteOptionList);

      deleteProductImage(imageDTO);
    }catch (Exception e) {
      log.warn("Filed admin patchProduct");
      e.printStackTrace();
      saveImages.forEach(this::deleteImage);

      throw new IllegalArgumentException("Failed patchProduct", e);
    }

    return productId;
  }

  public void setProductOptionData(Product product, AdminProductPatchDTO patchDTO) {
    List<PatchOptionDTO> optionDTOList = patchDTO.getOptionList();
    List<ProductOption> optionEntities = product.getProductOptions();

    for(int i = 0; i < optionDTOList.size(); i++) {
      PatchOptionDTO dto = optionDTOList.get(i);
      long dtoOptionId = dto.getOptionId();
      boolean patchStatus = true;

      for(int j = 0; j < optionEntities.size(); j++) {
        ProductOption option = optionEntities.get(j);

        if(dtoOptionId == option.getId()){
          option.patchOptionData(dto);
          patchStatus = false;
          break;
        }
      }

      if(patchStatus)
        product.addProductOption(dto.toEntity());
    }
  }

  public List<String> saveProductImage(Product product, AdminProductImageDTO imageDTO) throws Exception{
    List<String> thumbnails = saveThumbnail(product, imageDTO.getThumbnail());
    List<String> infoImages = saveInfoImage(product, imageDTO.getInfoImage());

    thumbnails.addAll(infoImages);

    return thumbnails;
  }
}
```

개선을 통해 위 코드와 같이 수정되었으며 전체 코드는 아래 링크를 통해 확인하실 수 있습니다.   
<a href="https://github.com/Youndae/mansshop_boot/blob/master/src/main/java/com/example/mansshop_boot/service/AdminServiceImpl.java#L245">patchProduct 코드</a>


<br />

### S3 연결로 인한 이미지 출력 처리

<br />

배포 처리를 진행하며 S3에 이미지 파일을 저장하도록 했습니다.   
S3 연동을 이번에 처음 해봤기 때문에 이미지 파일을 어떻게 불러올지에 대해 알아봤을 때 3가지 방법이 있었습니다.
1. S3 파일의 url을 통한 요청
2. preSignedUrl을 통한 요청
3. 백엔드 서버를 proxy 서버로서 다운로드 받은 뒤 반환하는 요청

여기서 첫번째 방법에 대해서는 S3에 직접 접근하는 형태이기 때문에 해당 방법을 택해서는 안되겠다고 생각했습니다.   
두번째 방법은 개발자가 직접 url의 유효시간을 설정해 처리하는 방법이기 때문에 안전한 방법이라고는 하지만 전달되는 url에 S3 버킷명과 같은 불필요한 정보가 포함된다는 점이 마음에 걸렸습니다.   
이 정보들은 노출되더라도 해당 파일에 접근할 수 없기 때문에 괜찮다는 말이 있었지만 그래도 불필요하게 노출할 필요는 없다고 생각해 다른 방법을 찾게 되었습니다.   

그래서 최종적으로 택한 방법은 백엔드 서버를 proxy 서버로 활용하는 방법입니다.   
이렇게 처리하는 경우 기존 로컬에 저장된 파일을 불러와 반환할때 처럼 다른 정보는 노출하지 않고 요청 url 정도만 노출하는 형태로 처리할 수 있었습니다.
개인적으로 최대한 불필요한 정보는 노출하지 않도록 하자는 생각을 하고 있기 때문에 이 방법이 가장 유용한 방법이라 생각해 이 방법으로 처리했습니다.

```java
//MainServiceImpl
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final AmazonS3 amazonS3;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    
    @Override
    public ResponseEntity<InputStreamResource> getImageFile(String imageName) {
        S3Object s3Object = amazonS3.getObject(bucket, imageName);
        InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());
        
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .body(resource);
    }
}
```

<br />

### 비밀번호 찾기
<br />

로그인 페이지에는 아이디와 비밀번호 찾기 기능이 있습니다.   
아이디는 사용자에게 이름과 연락처 혹은 이메일을 입력받아 일치하는 아이디를 반환해주는 간단한 처리로 구현했습니다.   
비밀번호의 경우 일반적으로 많이 수행되는 방법 중 하나인 이메일을 통한 인증번호 입력으로 처리했습니다.   
사용자에게 아이디, 이름, 이메일을 입력받은 후 해당 정보가 일치한다면 사용자의 이메일로 6자리의 랜덤한 정수를 생성해 전송합니다.   
그리고 인증번호는 Redis에 사용자 아이디를 key로 담고있도록 처리하고 6분의 만료시간을 설정했습니다.   
입력시간인 5분보다 1분의 시간을 더 두었는데 이유는 비밀번호 수정 요청까지 인증번호를 체크하기 위해서 입니다.

사용자가 이메일을 통해 인증번호를 입력하고 나면 서버에 아이디와 입력된 인증번호를 같이 전달해 확인을 요청합니다.   
서버에서는 아이디를 key로 Redis에서 인증번호 데이터를 조회하고 일치한다면 일치 응답을 반환해 비밀번호 변경 페이지로 접근하게 됩니다.   
이때 React에서는 해당 컴포넌트로 state를 통해 아이디와 인증번호를 같이 전달하도록 처리했습니다.

사용자가 수정할 비밀번호를 입력 후 수정 요청을 전달하게 되면 클라이언트에서는 아이디, 비밀번호, 인증번호를 같이 서버에 전달하도록 했습니다.   
서버에서는 아이디와 인증번호를 가장 먼저 체크한 뒤 일치여부와 상관없이 Redis 데이터를 폐기합니다.   
일치하지 않는다면 불일치 응답을 보내게 되며 사용자는 처음부터 다시 인증을 수행하도록 했고, 일치한다면 비밀번호를 BCrypt를 통해 encode한 뒤 수정하도록 처리했습니다.

<br />

## 프론트 엔드 기능

<br />

### Axios Interceptor
<br />

이번 프로젝트에서는 JWT 요청과 오류 응답에 대한 처리를 수월하게 관리할 수 있도록 interceptor를 사용했습니다.   
동일하게 리액트를 사용했던 다른 프로젝트에서는 클라이언트에서 토큰을 모두 쿠키에 저장했기 때문에 따로 인터셉터를 사용하지 않고 오류 핸들링만 모듈화했었는데 이번에는 AccessToken을 LocalStorage에 저장하게 되면서 인터셉터를 활용하게 되었습니다.

```javascript
//customAxios.js

import axios from 'axios';

export const axiosInstance = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

axiosInstance.interceptors.request.use(
        (config) => {
          const accessToken = getToken();

          config.headers['Authorization'] = `${accessToken}`;

          return config;
        },
        (error) => {
          console.log('axios interceptor Error : ', error);
        }
)

axiosInstance.interceptors.response.use(
        (res) => {
          return res;
        },
        async (err) => {
          console.log('axios interceptor response');
          await errorHandling(err);
        }
)

const getToken = () => {

  const token = window.localStorage.getItem('Authorization');
  console.log('getToken : ', token);

  return token;
};


export const checkResponseMessageOk = (res) => {

  if(res.data.message === 'OK')
    return true;
  else{
    alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
    return false;
  }

}

export const errorHandling = (err) => {
  const errorStatus = err.response.status;
  const errorMessage = err.response.data.errorMessage;
  if(errorStatus === 401){
    //토큰 만료 응답
    // err.config._retry = true;
    console.log('axios default response status is 401');

    return axiosInstance.get(`reissue`)
            .then(res => {
              window.localStorage.removeItem('Authorization');

              const authorization = res.headers['authorization'];
              window.localStorage.setItem('Authorization', authorization);

              return axiosInstance(err.config);
            })
  }else if(errorStatus === 800){
    //토큰 탈취 응답
    window.localStorage.removeItem('Authorization');
    alert('로그인 정보에 문제가 발생해 로그아웃됩니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
    window.location.href='/';
  }else {
    window.location.href = '/error';
  }
}
```

인터셉터 Request에서는 AccessToken을 헤더에 담는 처리만 하도록 했고, Response에서는 토큰 만료 응답이 반환된 경우 재발급 요청을 보내도록 처리했습니다.   
이후 재발급이 완료 되었다면 기존 AccessToken 데이터는 삭제하고 새로운 AccessToken 데이터를 담아줍니다.   
남은 오류중 따로 핸들링 해줘야 하는 오류가 탈취 응답 코드로 설정한 800번 오류인데 이 경우 alert 창을 띄워 사용자에게 알린 뒤 LocalStorage에 저장된 토큰 데이터를 삭제하도록 했습니다.   
쿠키는 서버에서 만료된 응답 쿠키를 반환하기 때문에 따로 인터셉터에서 처리하지 않도록 했습니다.   
403 오류를 포함한 다른 모든 오류에 대해서는 오류페이지로 이동하도록 처리했습니다.

customAxios 모듈에는 하나의 axios가 더 존재하는데 이 axios의 인터셉터는 조금의 차이가 있습니다.   
Request의 경우 동일하게 처리하지만 Response에 대해서는 토큰 만료 응답만 처리하도록 했습니다.   
이유는 로그인처럼 응답에 따라 직접 처리해야 하는 요청에서 사용하기 위해서 분리하게 되었습니다.   
로그인의 경우 잘못된 정보를 입력하는 경우 BadCredentialsException이 발생하고 그 오류에 대해 핸들링 하도록 처리해두었는데 그러다보니 오류페이지로 이동하는 것이 아닌 정보가 잘못 입력되었다는 것을 화면에 출력해줄 필요가 있었습니다.   
해당 처리에 대해 기존 Axios로 처리할 수 있는 방법을 찾아보고자 했지만 해결책을 찾지 못해 하나의 인스턴스를 추가하게 되었습니다.

<br />

### 상품 옵션 추가 폼 처리
<br />

상품 추가 폼은 추가, 수정에서 모두 사용되기 대문에 AddProductForm으로 하위 컴포넌트를 생성해 처리했습니다.   
여기서 옵션 추가에 대해서는 버튼 클릭 시 옵션 설정 폼이 추가되도록 처리했습니다.

<img src="src/main/resources/README_image/option_form.jpg">

옵션 추가 버튼 이벤트가 발생하면 optionList라는 useState 배열에 객체를 추가하는 형태로 처리했습니다.   
AddProductForm에서는 이 optionList의 사이즈만큼 폼을 출력하고 데이터를 출력하도록 처리했습니다.

```javascript
//AddProduct Component
function AddProduct() {
    //...
    const [optionList, setOptionList] = useState([]);
    
    //...
    //옵션 추가 버튼 핸들링
    const handleAddOption = () => {
        const optionArr = [...optionList];
        
        optionArr.push({
            optionId: 0,
            size: '',
            color: '',
            optionStock: '',
            optionIsOpen: true
        });
        
        setOptionList(optionArr);
    }
    
    //옵션 폼 입력 onChange 핸들링
    const handleOptionOnChange = (e) => {
      const idx = e.target.parentElement.parentElement.getAttribute('value');
      let value = e.target.value;
  
      if(e.target.name === 'optionStock')
        value = Number(value);
  
      optionList[idx] = {
        ...optionList[idx],
        [e.target.name]: value,
      };
  
      setOptionList([...optionList]);
    }
}


//AddProductForm Component
function AddProductForm(props) {
    //...
    
    return(
        //...
            <div className="option-header">
              <h3>상품 옵션</h3>
              <DefaultBtn
                      btnText={'옵션 추가'}
                      onClick={handleAddOption}
              />
            </div>
            {optionList.map((data, index) => {
              let sizeText = '';
              let colorText = '';
              if(data.size !== null)
                sizeText = data.size;
              if(data.color !== null)
                colorText = data.color;
              return (
                      <div key={index} value={index} className="option-detail">
                        <div className="option-detail-header">
                          <DefaultBtn
                                  btnText={'옵션 삭제'}
                                  onClick={handleRemoveOption}
                                  name={data.optionId}
                                  value={index}
                          />
                        </div>
                        <div className="option-size">
                          <label className="product-label">사이즈</label>
                          <input className="product-input" type={'text'} name={'size'} onChange={handleOptionOnChange} value={sizeText}/>
                        </div>
                        <div className="option-color">
                          <label className="product-label">컬러</label>
                          <input className="product-input" type={'text'} name={'color'} onChange={handleOptionOnChange} value={colorText}/>
                        </div>
                        <div className="option-stock">
                          <label className="product-label">재고</label>
                          <input className="product-input" type={'number'} name={'optionStock'} onChange={handleOptionOnChange} value={data.optionStock}/>
                        </div>
                        <div className="option-isOpen">
                          <label className="product-label">옵션 공개여부</label>
                          <div className="product-isOpen-radio isOpen-radio">
                            <label className="radio-label-label">공개</label>
                            <input className="radio-input" type={'radio'} name={`optionIsOpen/${index}`} onChange={handleOptionRadioOnChange} checked={data.optionIsOpen}/>
                            <label className="radio-label">비공개</label>
                            <input className="radio-input" type={'radio'} name={`optionIsOpen/${index}`} onChange={handleOptionRadioOnChange} checked={!data.optionIsOpen}/>
                          </div>
                        </div>
                      </div>
              )
            })}
        //...
    )
}
```

이렇게 처리하고 입력되는 데이터 onChange 핸들링에 대해서는 상위 div의 value에 index를 갖도록 처리하게 되면서 해당 index의 데이터 값에 대한 핸들링을 처리할 수 있도록 구현했습니다.

<br />

# 느낀점과 고민중인 부분

계속해서 JDK8로만 개발하다가 이번에 처음 17버전을 사용해보게 되었습니다.   
SpringBoot 3.x 부터는 17버전을 사용해야 한다고 해서 이번에 적용해보게 되었는데 record 사용에 있어서 불편하다고 느낀점도 있지만 그래도 처음 record가 무엇인지 알아볼 때 생각했던 것 보다는 원활하게 사용할 수 있었습니다.   
처음에 한가지 걱정했던 점이 record 멤버 변수가 final로 선언되어 setter를 사용할 수 없다는 것이었는데 DTO 위주로 사용하다 보니 따로 setter를 통해 처리해야 하는 경우가 드물고, 있더라도 따로 처리해준 뒤 인스턴스 생성을 하면 되었기 때문에 어렵지 않게 사용했다고 생각합니다.   
오히려 DTO의 목적에 맞게 계층간 데이터 전송 목적으로 사용할 수 있어 어떻게 DTO를 처리하면 좋을지 더 생각해 볼 수 있는 계기가 되었습니다.

그리고 이번에 stream을 적극적으로 사용해보고자 노력했는데 이번 기회로 Collection 타입의 변수를 어떻게 처리할지 경험을 쌓을 수 있는 좋은 기회였습니다.   
Stream을 보통 알고리즘 공부할때만 조금씩 사용해봤기 때문에 class 객체에 대한 filter(), map()의 사용은 처리해본 경험이 굉장히 적었는데 이번 프로젝트를 진행하며 감을 잡을 수 있었습니다.   
Stream으로 처리한 코드들을 볼 때마다 오히려 반복문보다 느리게 처리되는 경우도 있으니 상황에 맞춰 사용해야 한다는 말을 많이 봤습니다.   
그래서 이번 프로젝트를 진행하며 여러 번의 조회 결과에 대한 응답 DTO 매핑에 대해서는 Stream을 사용하지 않고 반복문을 주로 사용했습니다.   
이유로는 반복문으로 처리하면서 한번만 거치고 나면 삭제해도 되는 처리가 많았기 때문에 검색 횟수를 줄이기 위해 기존 리스트에서 remove를 통해 해당 인덱스 데이터를 삭제하도록 했습니다.   
또한 break를 통해 반복문을 제어하도록 했기 때문에 그 부분에서 Stream과 수행시간 차이가 발생하는 것을 확인할 수 있었습니다.   
아직 Stream을 완벽히 사용하지 못하고 모든것을 알지 못하기 때문에 제가 아직 알지 못하는 포인트에서 이 차이를 메꿀 수 있는 방법이 있을 수 있겠지만 아직까지는 가독성이 좋고 좀 더 편하게 사용할 수 있다는 점이 장점이라고 생각합니다.   
Stream은 앞으로 적극적으로 활용해 경험을 쌓고 자유자재로 사용해 반복문과의 선택지에서 더 효율적인 방법을 확실하게 선택할 수 있는 역량을 기르고 싶습니다.


프로젝트를 진행하며 발생했던 오류 중 클라이언트에서 새로고침 또는 url 입력을 통한 페이지 이동 시 토큰 탈취로 판단이 된다는 문제가 있었습니다.   
로그를 통해 확인해보니 다른 토큰은 정상적으로 서버에 넘어오지만 AccessToken이 넘어오지 않아 토큰 구성이 이상해 탈취로 판단한 것이었습니다.   
이 프로젝트에서는 AccessToken, RefreshToken, Ino 이렇게 세가지의 토큰 값을 통해 인증 / 인가를 처리합니다.   
Ino가 존재하더라도 장기간 미접속으로 AccessToken, RefreshToken이 존재하지 않을수는 있지만 AccessToken이나 RefreshToken 둘 중 하나만 존재할수는 없다고 생각했습니다.   
그래서 그런 경우 탈취로 판단하도록 처리했는데 문제는 통합빌드로 처리하게 되면서 새로고침 또는 url 접근으로 인한 최초 접근하는 부분이 React Component가 아닌 Spring Boot 서버라는 점이었습니다.   
그래서 Local Storage에 담아두었던 AccessToken은 서버에 전달되지 않고 쿠키에 저장되어있는 RefreshToken과 Ino만 전달되어 탈취로 판단하는 것이었습니다.   
여러 방향으로 어떻게 수정해야 할지 고민을 많이 해봤는데 결과적으로는 RefreshToken과 ino만 존재하더라도 이후 처리를 진행하도록 하는 방향으로 수정하게 되었습니다.   
이유로는 처음부터 모든 토큰을 검증하는 것이 아닌 있는지만 확인하고 ino와 AccessToken의 Claim을 통해 Redis 데이터와 비교하며 검증을 하도록 설계했습니다.   
그럼 AccessToken이 없으면 권한이 필요한 요청이 허용되지 않는다는 것이므로 문제 발생의 여지가 없었습니다.   
다음으로는 재발급의 경우인데 이 경우에도 탈취에 조금이라도 대응하겠다는 취지로 AccessToken, RefreshToken, Ino가 모두 있어야만 재발급을 처리하도록 설계했습니다.   
그래서 Redis의 AccessToken 데이터 만료시간도 토큰 만료시간보다 길게 잡아두었고 클라이언트에서는 만료시간을 아예 알지 못하고 있어 재발급 시 같이 담아 요청하도록 했기 때문에 재발급에서도 문제가 발생하지 않게 됩니다.   
제 생각으로는 보안에 대해 과하게 집중하면서 여러 상황에 대해 고려하지 못한 것이 가장 문제였다고 생각합니다.   
이번 문제 발생과 해결로 인해 더 신경써야 하는 부분일수록 최대한 모든 상황을 고려해 설계해야 한다는 점을 느낄 수 있었습니다.


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

<br />

### 2024/05/31
> frontend
>> 상세페이지 데이터 전체 출력 구현 및 테스트 완료.    
>> 리뷰, QnA 답변의 경우 안쪽으로 한단 들어가도록 처리.   
>
> backend
>> 리뷰, QnA 답변에 대한 처리 테이블 분리로 수정.   
>> 각 기존 테이블 groupId, Step 필드 제거.   
>> 답변의 양이 문제가 아니라 페이징 처리에 있어서 조회 쿼리 문제 때문에 수정.   
>> 리뷰의 경우 1개 이상의 답변이 달리지 않을 것이므로 left join으로 조회해 처리.   
>> QnA의 경우 한개의 문의에 여러개의 답변이 달릴 수 있으므로 각각 조회한 뒤 반복문을 통해 체크한 뒤 DTO 매핑하도록 처리.

<br />

### 2024/06/01
> frontend
>> 리뷰, QnA 페이징 구현 및 테스트 완료.

<br />

### 2024/06/05
> frontend
>> 상품 상세페이지 바로주문, 관심상품 등록 제외한 나머지 기능 마무리. 테스트 완료.
>> 장바구니 페이지 구현 완료. 테스트 완료.
>> 주문 페이지 완료.
> 
> backend
>> 장바구니 담기, 선택 또는 전체 상품 삭제, 장바구니 내 수량 증감 구현 및 테스트 완료.   
>> 결제 처리 완료. 테스트 완료.

<br />

### 2024/06/07
> frontend
>> 마이페이지 사이드 버튼, 마이페이지 주문 조회, 비회원 주문 조회 전 정보 입력, 비회원 주문 조회 컴포넌트 생성 및 출력 확인.   
>> select box 선택을 통한 기간별 검색, 페이징 추가 필요.
>
> backend
>> 주문 조회 처리 구현. 주문 기준(상품 말고) 10개에 한페이지로 출력하도록 처리.   
>> 회원과 비회원의 요청을 받는 컨트롤러는 분리하되 같은 Service Method에서 처리하도록 구현.   
>> 데이터 정상적으로 반환하는 것 확인 완료.   
>> 현재는 아이디 또는 비회원의 주문자, 연락처를 통한 조회까지만 구현한 상태.   
>> 기간별 검색 추가 필요.
>>
>> 사용자 이름 또는 닉네임을 반환하는 Principal 메소드 분리.   
>> 주문 조회처럼 회원과 비회원이 모두 사용하는 서비스의 경우 굳이 비회원에서 Pincipal을 생성하지 않도록 하기 위해 Principal을 받는 메소드와 userId를 받는 메소드로 분리.   
>> 조회 및 결과 반환 메소드를 Override 하지 않고 ServiceImpl 클래스 내부에 따로 작성해 둘다 해당 메소드를 호출함으로써 결과를 반환하도록 수정.   
>> Principal이 존재하는 경우 조회된 사용자 데이터가 Null이라면 Excpetion을 반환하도록 하고 userId를 받는 메소드에서는 그대로 null을 반환하도록 처리.   
>> 비회원이라서 userId가 null인 경우를 감안해 조회하는 메소드에서는 조회 이전 사용자 아이디가 null인 경우 조회 하지 않고 바로 null을 반환.

<br />

### 2024/06/10
> frontend
>> 마이페이지 주문 내역, 비회원 주문 조회 페이지 구현 완료.   
>> 기간 selectBox onChage 설정, 페이징 설정 완료.   
>> 마이페이지의 주문내역과 비회원의 주문 내역 컴포넌트의 className은 받아서 사용하도록 수정.   
>> 마이페이지의 sideNav 컴포넌트 스크롤 내려도 내려가지 않도록 position fixed로 처리하면서 content 부분이 사이드와 붙는 문제가 있어 css 조정을 위해 className 받는 방법으로 해결.
>> 상품 상세페이지에서 옵션이 하나뿐인 상품에 대해 옵션 select box 출력하지 않도록 수정.   
>> 상품 상세페이지에서 바로구매 버튼 클릭 시 결제 페이지로 데이터 전달하도록 처리 및 테스트 완료.
> 
> backend
>> 주문 조회 쿼리 기간별로 조회하도록 수정.   
>> 특정 데이터를 전달해주는 경우를 제외한 나머지 응답의 body를 모두 "OK"로 처리하기 위해 Result Enum에 OK를 추가.   
>> 전체적으로 아직 수정은 안한 상태이므로 수정 필요.   
>> 관심상품 등록 및 해제 구현 및 테스트 완료.   
>> 관심상품 등록은 Post로 받고 관심상품 해제는 데이터를 삭제할 것이기 때문에 Delete로 받도록 처리.

<br />

### 2024/06/11
> frontend
>> 마이페이지 관심상품 목록 구현 완료.   
>> 주문 목록 페이지에서 상품명 클릭 시 상품 페이지로 이동할 수 있도록 Link 태그를 통해 연결하도록 수정.
>> MyPageSideNav 문의 내역 버튼 클릭 시 상품 문의와 문의 사항 버튼 하단에 출력하도록 수정.   
>> MyPage에 상품 문의, 문의 사항, 리뷰 내역, 정보 수정 컴포넌트 생성.   
>> 추가 생성 컴포넌트에 기본적인 설정만 해둔 상태이고 최초 요청에 대한 axios만 작성해둔 상태. 테스트 필요.
>
> backend
>> 마이페이지 관심상품 리스트 페이징 데이터 반환 처리 구현 및 테스트 완료.   
>> 주문내역에서 주문 상품명 클릭 시 상품 페이지로 이동할 수 있도록 productId 같이 반환하도록 수정.   
>> 문의 내역에 필요한 상품 문의, 상품 문의 상세, 문의 사항, 문의 사항 상세에 대한 Controller Method 생성.   
>> Service Method 및 Repository도 생성.   
>> Repository에 대해서는 테스트 코드로 조회가 정상적으로 처리되는 것을 확인.   
>> front-end로 응답이 정상적으로 반환되는지 확인 필요.

<br />

### 2024/06/12
> frontend
>> 마이페이지 문의 내역 구현 완료.   
>> 상품 문의 목록과 페이징 확인. 문의 상세도 확인.   
>> 문의 사항 목록 출력, 상세 출력, 작성, 수정 페이지 처리 완료.   
>> 문의사항 작성, 수정 및 상품 문의, 문의사항 답글 정상 출력과 작성 확인 완료.   
>> 각 문의 사항 상세 페이지는 겹치기 때문에 컴포넌트를 분리해서 호출해 사용하도록 처리.   
>> 문의 사항 중 작성과 수정 역시 겹치기 때문에 컴포넌트 분리.   
> 
> backend
>> 모든 문의 내역 기능에 대한 처리 구현 완료.   
>> 문의 사항 수정과 삭제를 제외한 모든 기능 테스트 완료.

<br />

### 2024/06/13
> frontend
>> 응답이 데이터가 아닌 메세지로 전달되는 요청에 대한 응답 처리를 모듈로 보내 체크하도록 수정.   
>> Image File 요청을 하나의 컴포넌트에만 작성하기 위해 Image 컴포넌트 생성해서 대부분의 이미지 처리는 해당 컴포넌트를 통해 처리하도록 수정.   
>>> ProductDetail의 Thumbnail 부분은 Image 컴포넌트로 연결하지 않고 별개로 처리.   
>>> Image 컴포넌트는 받는 모든 imageName에 대해 파일 요청을 보내는 반면 ProductDetail의 Thumbnail 부분은 해당 상품의 썸네일 파일들을 모두 담아두었다가   
>>> 마우스 오버 시 해당 파일로 firstThumbnail 값을 수정해야 하기 때문에 별개로 처리하도록 함.   
>> DefaultBtn 컴포넌트 생성해서 버튼에 대한 처리 연결.   
>>> NavBar의 Classification 버튼같은 다른 디자인의 버튼은 연결하지 않고 그 외 동일하게 사용되는 버튼에 대해서만 연결.   
>> ProductDetail 컴포넌트에서 사용하던 dayjs 제거. 서버에서 LocalDate를 사용하게 되면서 dayjs로 처리해야 할 필요가 없어짐.   
>> 가격에 대한 3자리 단위 콤마 처리를 모두 module에 연결해 처리하도록 수정.   
>> 상품 상세페이지내에서 상품 문의 작성 기능 처리 및 테스트 완료.   
> 
> backend
>> 상품 상세페이지의 상품 문의 작성 기능 구현 및 테스트 완료.   
>> 마이페이지 문의 사항 수정과 삭제 모두 테스트 완료.   
>> 응답 메세지를 Result Enum을 통해 반환하도록 수정.   
>> 서비스에서 ResponseEntity를 생성한 뒤 반환하도록 처리했었으나 재사용성이 떨어진다고 생각해 다시 객체 반환으로 수정.   
>> ResponseEntity 생성은 Controller에서 처리.
>> MyPage QnA 관련 DTO가 많아 dto.mypage.qna 패키지를 따로 생성해 분리. 그 안에서도 클라이언트 요청시 RequestBody로 받는 DTO는 req 패키지에 추가 분리.   

<br />

### 2024/06/14
> frontend
>> 회원 정보 수정 추가. 테스트 완료.
>> 모든 마이페이지 컴포넌트, 메인 및 상품 관련 컴포넌트 처리 완료. 테스트도 완료.   
>> Admin 페이지의 sideNav 구현 및 수정 완료.   
>> Admin 페이지에 사용될 컴포넌트 생성.  내부 내용은 아직 없고 주석으로 필요한 데이터만 작성해둔 상태.
> 
> backend
>> 마이페이지 리뷰 작성, 수정, 삭제 처리 구현, 테스트 완료.   
>> 마이페이지 정보 수정 getData, 수정 처리 구현, 테스트 완료.   
>> 마이페이지와 메인에 대한 처리 마무리되면서 해당 controller에 @PreAuthorize로 권한관리 하도록 추가.   
>> 정보 수정 중 이메일 처리에 prefix, suffix, type을 나눠서 응답하기 위해 해당 suffix의 타입을 체크하기 위한 MailSuffix enum 생성.   
>> member, ProductReview에 patch 수행 시 필요한 필드의 set 처리를 위한 setter 또는 메소드 생성.   
>> AdminController, AdminService, impl 생성만 해둔 상태.

<br />

### 2024/06/19
> frontend
>> 관리자 상품 목록, 추가, 수정, 상세 페이지 구현 및 테스트 완료.   
>> 상품 목록은 페이징으로, 상세페이지에서는 상품정보와 옵션, 각 이미지를 확인하도록 처리.   
>> 상세 페이지에서 상품의 공개, 비공개 처리와 옵션의 삭제, 공개, 비공개 처리를 추가할지 고민중.   
>> 추가, 수정 페이지에서는 각 옵션의 추가 및 삭제를 수행할 수 있도록 처리.   
>> 재고 컴포넌트 생성 및 테스트 완료.   
>> 테이블 구조로 상품 출력 후 상품 하단으로 옵션 출력하도록 처리.   
>
> backend
>> 관리자 페이지 중 상품 목록에 출력할 데이터 처리.   
>> 목록에서는 옵션을 출력하지 않기 때문에 전체 옵션에 대한 재고 출력을 groupBy sum()으로 처리.   
>> 옵션의 개수 역시 count()로 처리.   
>> 상품 상세 페이지에서 상품의 모든 정보와 등록된 이미지, 옵션 정보를 반환하도록 처리.   
>> 상품 추가와 삭제에 대해 동일하게 AdminProductPatchDTO를 통해 상품 정보와 옵션 리스트를 받도록 처리.   
>> 이미지 역시 AdminProductImageDTO를 통해 이미지를 받을 수 있도록 처리.   
>> OptionList를 DTO로 바로 매핑하기 위해 여기서는 record가 아닌 class 타입으로 처리.   
>> 다른 데이터는 record로 받는데 문제가 없었지만 List 타입은 문제를 해결하지 못해 class 타입으로 전환.   
>> Long이나 String 타입의 List는 받는데 record로 받는데도 문제가 없었지만 Option 정보를 담고 있는 객체 리스트를 받고자 했더니 매핑이 되지 않음.   
>> 서비스단에서는 상품의 대표 썸네일을 제외한 나머지 처리에 대해 메소드를 분리해 추가와 수정 모두 해당 메소드를 통해 파일을 저장하고 삭제해야 할 파일을 삭제하도록 처리.   
>> 이 메소드에서 처리한 파일에 대한 DB 데이터 역시 담당해 처리.   
>> 재고 목록 요청 처리 완료.   
>> group by 와 sub query를 통해 총 재고 현황 및 옵션 리스트 반환하도록 처리.

<br />

### 2024/06/20
> frontend
>> 관리자 상품 할인 설정 구현.   
>> 할인중인 삼품 목록만 출력하도록 하고 상품명을 통한 검색, 페이징 기능 제공.   
>> 상단 할인 추가 버튼 클릭 시 할인 설정 페이지로 이동하고 해당 페이지에서는 select box로 상품 분류와 상품을 선택. 선택한 상품들을 한번에 동일한 할인율을 적용할 수 있도록 구현.   
> 
> backend
>> 할인 상품 컴포넌트에 대한 처리 완료. 테스트도 완료.   
>> 목록 컴포넌트 접근 시 할인중인 상품만 조회해 반환하도록 처리.   
>> 추가 요청 시 할인을 적용할 상품 id List와 할인율을 받고 update 처리.   
>> 다른 patch 요청과는 다르게 리스트에 대한 엔티티 조회를 한 뒤 JPA의 save를 통한 업데이트를 하지 않고 QueryDSL을 통해 처리.   
>> 다른 patch 요청은 하나의 객체에서 수정해야 할 부분이 여러개였지만 여기에서는 discount 값 하나만 바꿔야 하고 모든 리스트에 동일한 discount 값이 수정 될 것이기 때문에 이게 더 낫다고 판단.

<br />

### 2024/06/21
> frontend
>> 관리자 문의 관리, 회원 관리 구현.   
>> 리스트와 상세 페이지 또는 모달 창 정상 출력만 확인. 기능 테스트 필요.
>
> backend
>> 문의관리, 회원 관리에 대한 처리 구현.   
>> 문의 답변 기능만 테스트 완료.

<br />

### 2024/06/25
> backend
>> 관리자 기간별 매출 처리 구현. 쿼리 테스트만 진행.   
>> 서비스 테스트 이후 프론트 작업 예정.

<br />

### 2024/06/26
> frontend
>> 기간별 매출 관련 컴포넌트 구현. 기능 테스트까지 완료.   
>
> backend
>> 상품별 매출 관련 처리 구현 완료. 서비스 테스트 코드로 체크 완료.

<br />

### 2024/06/29
> frontend
>> 미처리 주문 내역 컴포넌트 구현.   
>> 회원 정보 모달창에서 버튼 연결   
>> 아임포트 결제 API 연결   
>> 아이디, 비밀번호 찾기 컴포넌트 구현.   
>> 관리자 회원 목록 아이디, 이름, 닉네임 가입일 구조로 출력하도록 수정.   
>> 관리자 회원 목록 검색 타입을 아이디, 이름, 닉네임 세가지로 검색할 수 있도록 수정.   
>> 관리자 문의 관련 검색은 모두 닉네임을 기준으로 검색하도록 수정.   
>> 메인 페이지 상품 목록에서 할인 상품의 경우 정가와 할인율, 할인가를 출력하도록 수정.   
>> 모든 옵션이 품절된 상품의 경우 메인 리스트에서 품절을 출력하도록 수정.   
>> 상품 상세 페이지에서 할인중인 상품의 경우 정가, 할인율, 할인가를 출력하도록 수정.   
>> 페이징 기능에 대해 모듈화.   
>> 오류 페이지 컴포넌트 생성 및 잘못된 url에 대해 오류 페이지 컴포넌트로 연결하도록 처리.   
>> axios 에러 핸들링 처리.   
>> 직접 axios 에러에 대해 핸들링 해야 되는 경우(ex. 로그인)를 감안해 axiosDefault를 추가.   
>
> backend
>> 미처리 주문 목록에 대한 쿼리 작성.   
>> 회원 목록 검색 조건 추가로 인한 쿼리 where 문 수정.   
>> 메인 상품 리스트의 할인율 출력과 품절 상태 출력을 위해 쿼리 수정.   
>> 매출 관리 관련해 PeriodSales, ProductSales 테이블을 사용하지 않고 group by로 처리하도록 수정.   
>> 마이페이지 주문 목록 리스트 쿼리 조건을 잘못 설정해 한가지 상품만 출력되던 문제 해결.   
>> 아임포트 결제 API 연결을 위해 properties에 데이터 추가와 PaymentController 추가 및 의존성 추가.   
>> 비밀번호 찾기에 대한 처리 추가.   
>> 비밀번호 찾기 요청 시 메일로 전송할 수 있도록 javaMailSender 추가 및 gmail smtp 설정과 properties에 필요한 데이터 추가.   
>
> All
>> 계획했던 모든 기능 및 페이지 구현 완료.   
>> 전체적으로 중복 코드와 주석에 대한 정리 후 통합 빌드와 기능 테스트 진행 계획.

<br />

---

## 2024/06/30 기준 테스트 목록
#### 테스트가 끝난 항목에 대해서는 취소선 처리.

### 메인 & 상품 페이지
> 메인페이지
>> 1. ~~메인 페이지에서 상품의 정상 출력~~
>> 2. ~~메인 페이지에서 할인중인 상품의 원가와 할인가의 정상 출력~~
>> 3. ~~메인페이지에서 비공개 상품의 미출력~~
>> 4. ~~메인페이지에서 재고가 없는 상품이 품절로 표시~~
>> 5. ~~BEST, NEW를 제외한 카테고리에서의 페이징 동작~~
>> 6. ~~상품 검색 결과의 정상 출력~~
>> 7. ~~상품 검색 결과의 페이징 동작~~
>
> 상품 상세 페이지
>> 1. ~~상품 상세 데이터의 정상 출력~~
>> 2. ~~상품 옵션 Select box의 정상 출력~~
>> 3. ~~상품 옵션 선택 시 옵션 정보와 총 금액의 출력~~
>> 4. ~~품절된 상품 옵션의 (품절) 출력과 disable~~
>> 5. ~~선택한 상품 옵션의 수량 증감~~
>> 6. ~~선택한 상품 옵션의 수량 증감에 따른 금액의 변경~~
>> 7. ~~할인중인 상품의 경우 할인율과 할인가의 출력~~
>> 8. ~~바로구매 버튼의 동작~~
>> 9. ~~장바구니 버튼의 동작~~
>> 10. ~~로그인한 사용자의 관심상품 등록 / 해제 동작~~
>> 11. ~~비로그인한 사용자의 관심상품 등록 버튼 클릭시 로그인 하시겠습니까? 라는 confirm 창 출력 및 확인시 로그인 페이지 이동~~
>> 12. ~~상품 상세 페이지의 네비게이션 바 정상 출력 및 동작~~
>> 13. ~~상품 리뷰의 정상 출력~~
>> 14. ~~상품 리뷰 개수의 정상 출력~~
>> 15. ~~상품 리뷰의 페이징 동작~~
>> 16. ~~상품 문의 개수의 정상 출력~~
>> 17. ~~상품 문의의 정상 출력~~
>> 18. ~~상품 문의 페이징의 동작~~
>> 19. ~~비회원이 상품 문의하기 버튼 클릭 시 로그인 하시겠습니까? 라는 confirm창 출력 및 확인 시 로그인 페이지로 이동~~
>> 20. ~~회원의 상품 문의 등록~~
>
> 비회원
>> 1. ~~상품 상세 페이지에서 바로구매 버튼을 통한 주문 페이지 이동 및 데이터 전달~~
>> 2. ~~상품 상세 페이지에서 장바구니 버튼을 통한 장바구니에 데이터 추가~~
>> 3. ~~비회원의 장바구니 데이터 저장 및 조회를 위한 쿠키 응답~~
>> 4. ~~비회원의 주문 결제~~
>> 5. ~~비회원의 받는사람, 연락처 입력을 통한 주문 조회~~
>
> 주문
>> 1. ~~전달받은 데이터의 정상 출력~~
>> 2. ~~받는사람, 연락처, 주소를 필수로 받아야 결제할 수 있도록 처리~~
>> 3. ~~아임포트 결제 API의 정상 동작 및 결제 완료 처리~~
>> 4. ~~무통장 입금 선택시의 정상 처리~~
>> 5. ~~카카오 우편번호 서비스 API의 정상 동작~~
>
### 가입 또는 로그인
> 로그인
>> 1. ~~로컬 로그인~~
>> 2. ~~모든 OAuth2 로그인~~
>> 3. ~~로그인 처리 이후 로그인 이전 페이지로 이동~~
>> 4. ~~로그인 성공 시 LocalStorage에 Authorizaion과 쿠키에 Authorization_Refresh, Authorization_ino의 저장 여부~~
>
> 가입
>> 1. ~~회원 가입 정상 처리~~
>> 2. ~~회원 가입시 아이디 중복체크 정상 동작~~
>> 3. ~~회원 가입시 닉네임 중복체크 정상 동작~~
>> 4. ~~회원 가입시 필수로 받아야 하는 아이디, 이름, 연락처, 주소의 작성 누락 시 해당 input 아래에 텍스트 출력과 포커스 이동~~
>> 5. ~~회원 가입시 필수로 받아야 하는 데이터를 받아야만 가입 처리~~
>> 6. ~~닉네임 중복체크나 닉네임을 아예 작성하지 않아도 가입 처리~~
>> 7. ~~닉네임을 입력했으나 중복체크를 하지 않았을 때 닉네임 input으로 포커스 이동 및 텍스트 출력~~
>> 8. ~~비밀번호와 비밀번호확인이 일치하는지에 따른 텍스트 출력~~
>> 9. ~~아이디, 비밀번호, 연락처, 이메일의 정규식을 통한 검증~~
>
> 아이디, 비밀번호 찾기
>> 1. ~~아이디 찾기 동작~~
>> 2. ~~비밀번호 찾기 요청 시 이메일 전송 여부~~
>> 3. ~~이메일로 전달받은 인증번호 입력 시 비밀번호 수정 페이지 접근~~
>> 4. ~~비밀번호의 정상 변경~~
>
### 마이페이지
> 접근
>> 1. ~~비회원의 마이페이지 접근 시 오류 페이지로 이동
>
> 주문 내역
>> 1. ~~주문 내역의 정상 출력~~
>> 2. ~~주문 내역의 리뷰 작성여부에 따른 버튼 출력~~
>> 3. ~~주문 상태가 배송 완료인 경우에만 리뷰 버튼 출력~~
>> 4. ~~주문 내역 기간 선택 시 해당 기간의 주문 내역 정상 출력~~
>> 5. ~~주문 내역 페이징 동작~~
>> 6. ~~리뷰 작성 버튼 클릭 시 리뷰 작성 페이지로 연결~~
>> 7. ~~리뷰 작성 정상 처리 및 리뷰 작성 상태 변경~~
>
> 관심 상품
>> 1. ~~등록한 관심상품 목록 정상 출력~~
>> 2. ~~클릭 시 상품 페이지 정상 접근~~
>> 3. ~~관심상품 페이징 동작~~
>
> 문의 내역
>> 1. ~~상품 문의 목록의 정상 출력~~
>> 2. ~~상품 문의 답변 상태 정상 출력~~
>> 3. ~~상품 문의 페이징 동작~~
>> 4. ~~상품 문의 상세 페이지 데이터 정상 출력~~
>> 5. ~~상품 문의 상세 페이지내 답변 작성 textarea 미출력~~
>> 6. ~~상품 문의 상세 답변 정상 출력~~
>> 7. ~~문의 사항 목록 정상 출력~~
>> 8. ~~문의사항 목록 페이징 동작~~
>> 9. ~~문의사항 작성시 문의 분류 목록 정상 출력~~
>> 10. ~~문의 사항 작성 처리~~
>> 11. ~~문의 사항 상세 페이지 데이터 장상 출력~~
>> 12. ~~문의 사항 답변 작성 textarea 출력~~
>> 13. ~~사용자가 문의 사항 답변 작성 시 답변 상태를 미답변으로 재변경~~
>
> 리뷰
>> 1. ~~작성한 리뷰 내역 정상 출력~~
>> 2. ~~리뷰 목록 페이징 동작~~
>> 3. ~~리뷰 내용 정상 출력~~
>> 4. ~~리뷰 수정 정상 동작~~
>> 5. ~~리뷰 삭제시 재작성 불가 안내 및 확인 시 삭제 처리~~
>
> 정보 수정
>> 1. ~~회원 정보의 정상 출력~~
>> 2. ~~닉네임 중복 체크 정상 동작~~
>> 3. ~~닉네임을 입력하지 않더라도 수정 처리~~
>> 4. ~~이메일의 유효성 검증~~
>> 5. ~~정보수정 처리~~
>
### 장바구니
> 1. ~~장바구니 데이터 정상 출력~~
> 2. ~~장바구니 상품 수량 증감~~
> 3. ~~선택 상품 삭제 동작~~
> 4. ~~전체 상품 삭제 동작~~
> 5. ~~선택 상품 결제 동작~~
> 6. ~~전체 상품 결제 동작~~
>
### 관리자
> 상품
>> 1. ~~상품 목록 정상 출력~~
>> 2. ~~상품 목록 페이징 동작~~
>> 3. ~~상품명 검색 정상 수행~~
>> 4. ~~상품명 검색 페이징 정상 동작~~
>> 5. ~~상품 추가 처리~~
>> 6. ~~관리자의 상품 상세 페이지 데이터 정상 출력~~
>> 7. ~~상품 수정 시 상품의 공개 비공개 정상 처리~~
>> 8. ~~상품 수정 시 옵션의 공개 비공개 정상 처리~~
>> 9. ~~상품 수정 시 이미지 파일에 대한 정상 처리~~
>> 10. ~~상품 수정 시 옵션 삭제 및 추가에 대한 정상 처리~~
>
> 상품 재고
>> 1. ~~재고가 부족한 순으로 목록 정상 출력~~
>> 2. ~~상품 재고 페이징~~
>> 3. ~~상품명 검색~~
>> 4. ~~검색 이후 페이징~~
>> 5. ~~클릭시 관리자의 상품 정보 페이지 이동~~
>
> 상품 할인
>> 1. ~~할인중인 상품의 정상 출력~~
>> 2. ~~페이징 동작~~
>> 3. ~~할인 설정 페이지에서 분류 Select box 정상 출력~~
>> 4. ~~할인 설정 페이지에서 분류 select box에서 선택한 카테고리의 상품이 해당 select box에 정상 출력~~
>> 5. ~~상품 select box에서 선택 시 하단에 상품 정보와 할인율에 따른 할인가 출력~~
>> 6. ~~등록 시 선택한 모든 상품 동일한 할인율 적용~~
>
> 주문 내역
>> 1. ~~미처리 주문 목록 정상 출력~~
>> 2. ~~미처리 주문 목록에서 선택 시 모달창에 데이터 정상 출력 및 주문 확인 버튼 출력~~
>> 3. ~~모달창에서 주문확인 버튼 클릭 시 상품 준비중으로 상태 변경~~
>> 4. ~~미처리 주문 목록 페이징 동작~~
>> 5. ~~미처리 주문 목록 상태에서 받는사람, 아이디 조건에 맞는 정상적인 검색 처리~~
>> 6. ~~검색이후 페이징 동작~~
>> 7. ~~전체 주문 목록 정상 출력~~
>> 8. ~~미처리 주문이 아닌 상태의 주문 선택 시 모달창에 주문 확인버튼 미출력~~
>> 9. ~~전체 주문 페이징 동작~~
>> 10. ~~전체 주문 받는 사람, 아이디 조건에 맞는 정상적인 검색 처리~~
>> 11. ~~주문 내역의 상단 select box를 통해 미처리, 전체 주문 내역 선택 시 해당하는 목록 정상 출력~~
>
> 상품 문의
>> 1. ~~상품 문의 내역 상단 select box를 통해 미답변, 전체 내역 선택 시 해당하는 목록 정상 출력~~
>> 2. ~~상품 미답변 목록 정상 출력~~
>> 3. ~~닉네임 기반 검색 정상 처리~~
>> 4. ~~상품 문의 페이징 동작~~
>> 5. ~~검색 이후 상품 문의 페이징 동작~~
>> 6. ~~상품 문의 상세 페이지의 데이터 정상 출력~~
>> 7. ~~상품 문의 상세 페이지에서 관리자가 답변 작성 시 답변 처리 및 답변 완료 처리~~
>> 8. ~~상품 문의 상세 페이지에서 답변을 작성하지 않아도 답변 완료 처리 버튼 클릭 시 답변 완료 처리~~
>
> 회원 문의
>> 1. ~~회원 문의 목록 정상 출력~~
>> 2. ~~상단 select box 선택을 통해 미답변, 전체 목록의 정상 출력~~
>> 3. ~~닉네임 기반 검색 처리~~
>> 4. ~~회원 문의 페이징 동작~~
>> 5. ~~회원 문의 검색 이후 페이징 동작~~
>> 6. ~~회원 문의 상세 페이지의 데이터 정상 출력~~
>> 7. ~~회원 문의 상세 페이지에서 관리자가 답변 작성 시 답변 처리 및 답변 완료 처리~~
>> 8. ~~회원 문의 상세 페이지에서 관리자가 답변을 작성하지 않더라도 답변 완료 처리 버튼 클릭 시 답변 완료 처리~~
>
> 회원 문의 카테고리 설정
>> 1. ~~카테고리의 정상 추가~~
>> 2. ~~카테고리의 정상 삭제~~
>
> 회원 관리
>> 1. ~~회원 목록 정상 출력~~
>> 2. ~~회원 목록 페이징 동작~~
>> 3. ~~아이디, 이름, 닉네임 기반 검색 처리~~
>> 4. ~~검색 이후 페이징 동작~~
>> 5. ~~회원 목록에서 클릭 시 모달창에 데이터 정상 출력~~
>> 6. ~~모달창에서 주문 정보 버튼 클릭 시 해당 사용자의 주문 목록 정상 출력~~
>> 7. ~~모달창에서 상품 문의 내역 버튼 클릭 시 해당 사용자의 상품 문의 내역 목록 정상 출력~~
>> 8. ~~모달창에서 회원 문의 내역 버튼 클릭 시 해당 사용자의 회원 문의 내역 목록 정상 출력~~
>
> 기간별 매출
>> 1. ~~기본으로 최초 접근 시 현재 연도의 매출 데이터 출력~~
>> 2. ~~select box에 현재 연도 포함 3년의 목록 출력~~
>> 3. ~~select box 선택에 따른 연매출 정보 출력~~
>> 4. ~~월 매출 클릭 시 해당 연월의 매출 정보 정상 출력~~
>> 5. ~~월 매출 데이터 중 분류별 매출 데이터에서 상세내역 버튼 클릭 시 해당 분류의 월 매출 정보 모달창으로 출력~~
>> 6. ~~월 매출 데이터 중 일 매출 데이터 클릭 시 해당 일의 주문 내역 출력~~
>
> 상품별 매출
>> 1. ~~분류별로 정렬된 상품별 매출 목록 정상 출력~~
>> 2. ~~상품명 검색 정상 동작~~
>> 3. ~~페이징 동작~~
>> 4. ~~검색 이후 페이징 동작~~
>> 5. ~~상세 내역 데이터 정상 출력~~

<br />

---

## 테스트 더미데이터 현황

1. 사용자
> 2,757. 관리자와 미가입자 처리를 위한 Anonymous 계정 까지 2개 포함.

2. 상품
> 1,016. 기존 상품 16개 포함. 각 상품 분류별로 200개씩 등록.

3. 상품 옵션
> 7,035. 상품당 랜덤으로 4 ~ 12개의 옵션을 갖고 있음. 더미데이터가 아닌 최초 등록 상품 16개의 경우 기존 옵션 수 유지.

4. 장바구니
> 2752. 대부분의 사용자가 장바구니에 물건을 담아두었다고 가정.

5. 장바구니 상세 데이터
> 8,288. 각 사용자는 최대 6개 내의 상품을 장바구니에 담아 두었다고 가정.

6. 회원 문의
> 1,450,304. 2023 / 01 / 01 ~ 2024 / 06 / 10 까지 모든 사용자가 매일 문의를 하나씩 남긴 상태.

7. 회원 문의 답변
> 8,700,618. 회원 문의는 사용자도 재 답변을 작성할 수 있기 때문에 답변에 따라 랜덤으로 관리자만 작성한 답변도 있고 회원과 관리자가 몇번씩 답변을 작성한 경우도 있다고 가정.

8. 상품 문의
> 101,600. 모든 상품에 100개씩 작성

9. 상품 문의 답변
> 101,600. 모든 상품 문의에 답변이 달린 상태

10. 리뷰
> 101,600. 모든 상품에 100개씩 작성

11. 리뷰 답변
> 101,600 모든 리뷰에 답변이 달린 상태

12. 주문
> 2,507,703. 2022 / 01 / 01 ~ 2024 / 06 / 29 까지 모든 사용자가 매일 주문을 했다고 가정.

13. 주문 상세
> 7,518,281. 모든 주문에 대해 최대 5개 이내의 상품을 주문했다고 가정.

<br />

---

### 2024/06/30 ~ 2024/07/03
> 더미데이터 생성   
> 테스트 진행   
> 테스트 진행하면서 수정된 내용
>> 관리자 매출 관련 쿼리 수정.
>>> 이전 프로젝트에서 사용했던 매출 정리하는 테이블을 제거하고 group by를 통해 처리하도록 변경했는데 더미데이터가 들어가면서 엄청난 처리 시간이 발생.   
>>> 최초 서버의 메소드 수행 시간이 2분 30초 가량 소요. 이 중 집계를 하는 group by 처리가 들어간 두 쿼리가 각각 1분씩 소요되는 문제 발생.   
>>> 해당 부분을 처리하기 위해 서브쿼리를 활용하고 인덱스 설정. 각 쿼리에 대해 0.7 ~ 0.9초까지 처리시간 감소.   
>>> 아직 아쉬운 부분으로는 전체 메소드 처리 시간이 2초 조금 안되는 시간이 걸린다는 부분.   
>>> 더 빠르게 처리하기 위해서는 다시 매출 정리 테이블을 생성하거나 파티셔닝을 통한 방법이 있을 것으로 생각하는데   
>>> 매출 정리 테이블 생성은 사용자의 주문 처리 과정이 너무 길어지게 될 것 같고, 그렇다고 배치처리로 돌리자니 실시간 매출 확인이 어려울 것 같아서 고민 중.   
>>> 파티셔닝은 아직 학습이 부족해 추후 테스트 해볼 계획.   
>>
>> 메인 페이지 상품 목록 정렬 수정.
>>> 날짜를 기준으로 역순 정렬이 되도록 하고 있는데 이번에 더미데이터를 추가하면서 날짜는 동일 날짜로 처리하다보니 순서가 계속 섞이는 문제가 발생.   
>>> 날짜를 기준으로 역순 정렬하고 추가적으로 동일 날짜에 대해서는 id를 기준으로 역순 정렬하도록 수정.
>>
>> git Repository Issue에 작성해두었던 간헐적 800(탈취 판단 응답) 문제 해결.
>>> 클라이언트와 서버를 분리해서 각각 3000, 8080에서 동작할때는 문제가 발생하지 않고 통합 빌드 이후에 발생한 문제였는데 문제점을 발견.   
>>> 새로고침 하는 경우 컴포넌트에 먼저 접근하지 않기 때문.   
>>> 8080/~~ 형태로 접근하는데 새로고침을 하다보니 서버에 먼저 접근하게 되고 이 처리를 WebController에서 index.html로 연결해줘야 하는데   
>>> 그전에 JWTAuthorizeFilter에서 먼저 토큰 검증을 수행하다보니 탈취로 판단하고 이후 처리가 진행되지 않은 채 오류만 응답한 것이 문제.   
>>> 포인트는 토큰 탈취에 대응하겠다고 AccessToken이 없지만 RefreshToken, ino가 존재한다면 탈취로 판단하도록 처리한 부분인데   
>>> RefreshToken과 ino는 쿠키에 저장되어있었으니 알아서 넘어가 서버에서 받았지만 LocalStorage에 저장한 AccessToken은 받을 수 없어 여기에서 걸린 것.   
>>> 이 문제에 대해 AccessToken이 존재하지만 RefreshToken과 ino가 존재하지 않거나 둘중 하나만 존재하는 경우를 탈취로 판단하도록 수정.   
>>> RefreshToken과 ino가 존재하더라도 AccessToken이 존재하지 않는 경우 검증을 진행하지 않고 넘겨 컴포넌트에서 요청 시 재 검증하도록 수정.   
>>> 토큰 재발급 시에도 AccessToken이 같이 넘어오도록 설계했기 때문에 RefreshToken과 ino만으로는 어차피 할 수 있는게 없다고 생각해 이렇게 처리.   
>>
>> 주문 내역에서 리뷰 작성 여부에 따른 버튼 출력 부분 수정.   
>>> 기존 int로 처리되어있던 상태값을 TINYINT와 boolean으로 수정하면서 누락된 부분으로 수정.   
>>
>> OAuth2 최초 로그인 사용자 추가 정보 받는 컴포넌트 제거.   
>>> 실제 운영되는 서비스라면 사용자 정보를 서버로부터 받을 수 있기 때문에 해당 부분 처리 제거.   
>>> 백엔드에서도 최초 로그인 시 해당 컴포넌트로 redirect 하는 코드를 제거.   
>> 
>> 장바구니 추가 처리 수정.   
>>> 장바구니에 이미 존재하는 상품을 다시 장바구니에 담는 경우 수량만 증가시켜주도록 처리해야 하는데 해당 부분이 누락.   
>>> 데이터 추가 등록이 아닌 수량 증감 처리를 하도록 수정.

<br />

### 2024/07/04 ~ 2024/07/05
> 공통
>> 메모 형태의 주석 제거 및 불필요한 log 제거.
>
> front-end
>> axios 모듈의 axiosDefault에 대해 interceptor를 추가. Request는 axiosInstance와 동일하나 Response에 대해서는 토큰 재발급 요청만 처리.   
>> axiosDefault의 목적은 오류에 대해 직접 핸들링 하는 것이 목적이기 때문에 재발급만 interceptor를 통해 처리.   
>
> back-end
>> 서비스에서 반환되는 데이터를 응답 DTO가 아닌 결과에 대한 DTO를 반환하도록 수정.   
>> 그리고 ResponseMappingService를 새로 만들어 해당 서비스를 통해 응답 매핑을 처리하도록 추가.   
>> Stream으로 처리할 수 있는 Collection 타입 매핑들을 수정.   
> 
> 테스트
>> 수정 내역에 대한 테스트 완료.

<br />

### 2024/07/18
> S3 연동 추가.
> 파일 조회에 대해서는 백엔드 서버를 프록시 서버로 사용하는 방법으로 처리.
> AWS 배포 처리 후 코드 완벽하게 수정 예정.

<br />

### 2024/08/24
> 코드 개선
>> record에서는 기본 생성자를 제공하고 불변성의 특징이 있기 때문에 Builder Pattern을 사용하는 것 보다 생성자를 통해 처리하는 것이 좋다. 라는 말에 따라 해당 부분 수정.   
>> 그렇다고 record에서 Builder Pattern을 사용하지 말아야한다! 는 아니지만 간결성을 위해 지양하는 편이라고 한다.   
>> 단, 필드 수가 많거나 생성자에서 모든 필드를 한번에 초기화 하는 경우가 아니라면 사용하기도 하며, 간결성의 문제는 lombok의 @Builder Annotation으로 간결해지기도 한다.   
>> JwtAuthorizationFilter에서 토큰 검증 후 정상 토큰이라면 OAuth2인지 local인지에 따라 CustomUser, CustomOAuth2User를 통해 처리하는데 이때 중복된 코드를 제거하기 위해 CustomUserDetails라는 인터페이스를 생성.   
>> 해당 인터페이스를 통해 getUserId()와 getAuthorities()를 재정의 하도록 해 필요한 정보를 조건문 밖에서 꺼낼 수 있도록 수정.   
>> CustomOAuth2Service에서 OAuth2Response를 Member Entity로 매핑해 save 처리하는 과정이 있는데 이 부분에 대해 OAuth2ResponseEntityConverter 클래스를 추가 생성해 처리하도록 개선.   
>> 실제 운영 서비스 시 OAuth2 서버에서 추가적인 데이터를 받아야 한다면 converter 코드를 수정하는 것으로 유지보수성을 높이기 위함.
>> AdminService에서 파일 저장에 대한 처리 메소드 개선.   
>> 기존 저장, 삭제를 모두 처리하던 메소드를 저장과 삭제를 기준으로 1차 분리. 그리고 각 엔티티에 대한 처리로 2차 분리.
>> OrderService의 payment 메소드 분리.   
>> PaymentDTO 값을 통한 데이터 처리 메소드, 장바구니 처리 관련 메소드, 옵션 재고 수정 및 상품 판매량 수정 메소드로 각각 분리하고 payment에서는 호출하도록 처리.
> 
> domain.dto 패키지 구조 개선.
>> 각 기능별 in, out, business 로 나눠서 분리.
> OAuth2와 Security 처리에 대한 USER, Service, SuccessHandler auth 패키지 생성해 하위로 이동.

<br />

### 2024/10/29
> 리팩토링
>> CartDetail 테이블에서 detailPrice 컬럼 제거.   
>>> 제거 이유로는 할인율이 적용되는 이상 고정적인 값을 갖고 있는게 의미가 없기 때문.   
>>> 연관 부분 수정 및 확인 완료.
>>
>> 주문 페이지 이동 시 React에서 바로 넘겨주던 처리를 서버에 요청 이후 해당 컴포넌트로 넘겨주도록 수정.   
>>> 이 부분 수정 역시 할인율이 적용되는 경우와 데이터 무결성을 위해 서버 요청 방식으로 수정.   
>>> 만약 서버에 요청하지 않고 처리할 경우, 할인이 종료되었는데도 불구하고 기존 페이지 로딩 시점의 가격으로 처리될 우려가 있기 때문에 서버에서 다시 데이터를 받아오도록 해야 한다.
>>
>> 관리자 리뷰 관리 기능 추가.
>>> 누락 기능으로 이번에 추가.   
>>> 여기에는 ReviewStatus도 없었기 때문에 status라는 이름의 컬럼을 ProductReview 테이블에 추가.   

<br/>

### 2025/01/10
> 문제 해결
>> Admin의 상품 수정 과정에서 발생한 Multiple representations of the same entity 오류 문제 해결.
>>> 멀티 모듈 환경 프로젝트 진행하면서 테스트 코드 작성 중 비슷한 오류가 발생하는 상황이 발생 해 원인을 파악할 수 있었고 문제를 해결.   
>>> 문제 원인은 ProductOption Entity의 중복 문제.   
>>> 이미 존재하는 id와 같은 ProductOption을 생성해 담아주었으니 당연히 중복 오류가 발생한 것.
>>> 문제 해결로 인해 상품 대표 썸네일과 ProductOption 리스트를 생성해 반환하던 메소드는 분리해서 대표 썸네일을 처리하는 메소드와 ProductOption 데이터를 수정하는 메소드로 분리.   
>>> 수정될 옵션 데이터를 담고 있는 PatchOptionDTO에 ProductOption 타입의 toEntity 메소드를 아무런 매개변수도 받지 않도록 하나 더 추가.   
>>> ProductOption Entity에는 수정 데이터를 적용하는 patchOptionData() 메소드를 추가.   
>>> Product에서는 연관관계에 있는 Entity 리스트의 타입을 Set이 아닌 List로 수정.   
>>> List로 수정 이유는 순서가 보장되어야 하기 때문.
>>> 정상적으로 옵션이 수정되는지 확인 완료.

<br/>

### 2025/01/16
> 처리 개선
>> 관리자의 상품 추가 및 수정 관련해서 예외 발생 시 처리 개선
>>> 상품 수정 기준 기존에는 처리에 따라 try-catch로 감싸서 처리한 반면   
>>> 이번에는 하나의 try-catch로 묶어서 처리하되 각 이미지 파일 저장 처리 이후 저장된 파일명을 리스트 또는 문자열로 반환하도록 처리.   
>>> 호출하는 메소드에서는 해당 리스트와 문자열 객체를 별도의 리스트에 담아두었다가 예외가 발생하면 해당 리스트를 기반으로 파일 삭제 요청을 처리하도록 개선.


<br />

### 2025/01/29
> Entity 수정
>> 모든 Entity에 대해 @Table, @Column Annotation 추가.   
>> Ubuntu 환경에서 테스트하려고 하다보니 Docker 환경의 MySQL을 사용하게 되었는데 그 과정에서 테이블 매핑이 정상적으로 처리되지 않는 문제가 발생.   
>> lower_case_table_names=1을 통해 문제를 해결할 수 있는 여지가 있었지만 RDS, Local MySQL 에서는 해당 문제가 발생하지 않는만큼 Docker 설정을 바꾸는 것 보다는 @Table로 처리하는게 더 낫다고 판단.

<br />

### 2025/02/14
> JMeter를 통한 부하테스트 중.
>> ProductQnAReply에 대한 N + 1 문제를 해결하기 위해 Entity를 통째로 조회하는 것이 아닌 필요한 데이터만 DTO에 매핑하도록 수정.

<br />

### 2025/02/19 ~
> ver 1.4 리팩토링
> 목표
>> 1. Swagger 적용.   
>> 2. JMeter 부하 테스트 결과를 통한 코드레벨 또는 데이터베이스 최적화 수행 및 재 테스트
>> 3. Redis를 통한 캐싱 처리로 조회 성능 개선
>> 4. 테스트 코드 재작성
>> 5. React Redux 개선
> 
> 리팩토링 처리 내역
>> 1. Swagger 적용. ( 2025/02/19 ~ 2025/02/21 )
>>> springdoc-openapi 를 통한 Swagger 의존성 추가 및 설정, 각 Controller에 대한 문서화 완료.   
>>> 중복되는 Response에 대한 @ApiResponse는 @DefaultApiResponse라는 Custom Annotation을 생성해 간결하게 처리.   
>>> JWT에 대한 @Parameter, @SecurityRequirement에 대해서는 @SwaggerAuthentication 이라는 Custom Annotation을 통해 처리.   
>>> 관리자 기능 중 상품 추가, 수정 기능은 여러 필드로 이미지를 받고 있으므로 DTO로 받도록 처리하고 있는데 Swagger에서는 MultipartFile이 내부 필드에 존재하는 경우 처리하지 못한다는 점 때문에 테스트 불가로 description 작성.
> 
>> 2 - 1. 쿼리 개선 및 인덱스 개선 ( 2025/02/23 ~ 2025/02/27 )
>>> 대부분 기능이 비즈니스 로직 보다도 쿼리 성능에서 문제가 발생.   
>>> 서브 쿼리 및 JOIN 개선으로 개선 및 인덱스 개선으로 최적화 수행.   
>>> 몇몇 쿼리에서 발생하고 있던 N + 1 문제와 하위 엔티티 조회에서 발생하는 역 N + 1 문제를 해결.   
>>> TimeCheckAOP의 표현식 수정. 간간히 표시되지 않던 로그가 있었는데 이 문제를 표현식 수정으로 해결.   
>>> 주문 데이터 처리와 관리자의 매출 기능은 아직 개선 이전.   
>>> JMeter와 유사한 테스트를 위해 테스트 클래스에서 QueryTest라는 클래스 생성 후 CompletableFuture를 통해 간이 테스트 수행. 최적화 이후 제거 예정.   
>>> Controller에서 Map으로 받던 데이터들 모두 수정. React Axios에서 보내는 방식을 수정하는 것으로 해결.   
>>> page, keyword, searchType, listType과 같은 QueryParam도 공통적으로 처리되는 부분이 많기 때문에 requestUrlModule.js 라는 모듈을 추가해서 사용하도록 개선.   
>>> pagingModule은 동적으로 사용될 수 있도록 개선.
>
>> 2 - 2. 데이터베이스 테이블 개선 및 엔티티 수정, 관리자 매출 기능 수정( 2025/03/01 ~ 2025/03/06 )
>>> 매출 집계 테이블 재생성. 기간별 매출을 의미하는 PeriodSalesSummary, 상품별 매출을 의미하는 ProductSalesSummary 테이블 추가 생성.   
>>> 기간별 테이블의 경우 auto_increment로 처리되는 id를 두고 period 컬럼에 DATE 타입으로 처리. 그날의 총 매출과 카드 매출, 현금 매출, 주문량, 상품 판매량, 배송비로 설계.   
>>> 상품별 테이블의 경우 동일하게 auto_increment로 처리되는 id, 날짜 처리를 위한 DATE 타입 컬럼, 주문량, 상품 분류 아이디, 상품 아이디, 옵션 아이디, 매출, 판매량으로 설계.   
>>> 두 테이블 모두 일별 기준으로 데이터를 처리. 상품별 테이블의 경우 특정 날짜에 대해 옵션 아이디는 unique를 유지해야 함.   
>>> 매출 집계 테이블을 제외한 나머지 테이블에 대해서는 대부분 DATETIME(3)으로 createdAt, updatedAt 타입을 개선.   
>
>> 2 - 3. 주문 처리 시 비동기 처리를 위한 RabbitMQ 학습 및 적용 ( 2025/03/07 ~ 2025/03/16 )
>>> RabbitMQ에 대한 기본적인 개념, 실패시 재처리 시도에 대한 개념 학습 이후 적용.   
>>> 최종적으로 적용한 설계로는 Exchange, Queue, DLQExchange, DLQ 구조로 설계.   
>>> DLQ의 경우 관리자 기능에 추가로 DLQ에 담긴 메시지 수량 조회 및 재시도 처리를 구현할 계획. 조회 및 처리 테스트까지 마무리.   
>>> 기존의 주문 처리에서 주문 데이터 처리를 제외한 장바구니, 상품 판매량 수정, 옵션 재고 수정, 집계 테이블 수정 처리는 모두 RabbitMQ로 처리.   
>>> 이로 인해 주문 처리의 응답 반환속도가 많이 감소 됨.   
>>> 그리고 관리자의 집계 처리 역시 새로 생성한 테이블로 인해 빠른 처리를 보일 것으로 예측.