# Django API gateway

### 명령어

```shell
# debug 서버 실행
python manage.py runserver --settings=config.settings.debug

# deploy 서버 실행
python manage.py runserver --settings=config.settings.deploy
```



### API 문서

* 여러 라이브러리를 사용하다보니 response의 양식이 일치하지 않음 -> 추후 통일 예정

| URL     | Method | URL Params | body                                                         | success response                                             | error response                                               | 비고                                                         |
| ------- | ------ | ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /       | GET    | -          | -                                                            | {<br />   'message': '로그인 성공'<br />}                    | {     <br />    "detail": "Authentication credentials were not provided."<br /> } | 로그인이 잘 되었는지 test 용 endpoint                        |
| login/  | POST   | -          | {<br/>    "username": "아이디",<br/> "password": "비밀번호"<br/>} | {     <br />"token": "토큰값"<br /> }                        | {    <br /> "non_field_errors": [         "Unable to log in with provided credentials."     ]<br /> } | token 값으로 어떻게 로그인 하는지 밑에서 부가 설명           |
| signup/ | GET    | -          | -                                                            | [     <br />{        <br />"username": "root",         "email": "rkdalstjd0@namver.com",         "nickname": "",         "address": "",         "detail_address": ""     },<br />{        <br /> "username": "rkdalstjd9",         "email": "rkdalstjd9@naver.com",         "nickname": "arkss",         "address": "",         "detail_address": ""     <br />},<br />] | -                                                            | 개발 할 때 유저 목록 확인하기 편하기 위해서 생성<br />추후 삭제 예정 |
| signup/ | POST   | -          | {<br /><br />"profile":{<br />"username": "아이디" "password": "비밀번호", "email": "이메일","nickname": "닉네임"}<br />} | {<br />"response": "success", "message": "profile이 성공적으로 생성되었습니다."<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러 메세지"<br />} |                                                              |
|         |        |            |                                                              |                                                              |                                                              |                                                              |



### 로그인 하는 법

위에서 설명한대로 login/ 로 요청 시 token 값을 받음.

이를 request header에 넣어줌.

토큰값에는 "" 나 '' 없이 토큰값 자체만 넣어줌.

| KEY           | VALUE      |
| ------------- | ---------- |
| Authorization | JWT 토큰값 |



일단 이걸로 통신 테스트하고 이해했다싶으면 refresh token과 access token 설명하겠음.

두 토큰은 테스트 할 때는 계속 귀찮게 해서 일단 빼놓음.

