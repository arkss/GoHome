# Django API gateway

### 명령어

```shell
# debug 서버 실행
python manage.py runserver --settings=config.settings.debug

# deploy 서버 실행
python manage.py runserver --settings=config.settings.deploy
```



### API 문서

| URL                             | Method | URL Params        | body                                                         | success response                                             | error response                                               |
| ------------------------------- | ------ | ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /                               | GET    | -                 | -                                                            | {<br />   'message': '로그인 성공'<br />}                    | {     <br />    "detail": "Authentication credentials were not provided."<br /> } |
| login/                          | POST   | -                 | {<br/>    "username": "아이디",<br/> "password": "비밀번호"<br/>} | {     <br />"token": "토큰값"<br /> }                        | {    <br /> "non_field_errors": [         "Unable to log in with provided credentials."     ]<br /> } |
| signup/                         | GET    | -                 | -                                                            | [     <br />{        <br />"username": "root",         "email": "rkdalstjd0@namver.com",         "nickname": "",         "address": "",         "detail_address": ""     },<br />{        <br /> "username": "rkdalstjd9",         "email": "rkdalstjd9@naver.com",         "nickname": "arkss",         "address": "",         "detail_address": ""     <br />},<br />] | -                                                            |
| signup/                         | POST   | -                 | {<br/>	"profile":{<br/>		"username": "rkdalstjd9",<br/>		"password": "qwe123",<br/>		"email": "rkdalstjd9@naver.com",<br/>		"nickname": "arkss9",<br/>        "address": "서울특별시 동대문구 전농동 103-45",<br/>        "detail_address": "주영리빙텔 108호"<br/>	}<br/>} | {<br />"response": "success", "message": "profile이 성공적으로 생성되었습니다."<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러 메세지"<br />} |
| share/route/                    | POST   | -                 | -                                                            | {<br />"response": "success",<br />message: "route가 성공적으로 생성되었습니다.",<br />"data":{<br />"profile": 5,<br />"route_id": 2<br />}<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러메세지"<br />} |
| share/\<int:route_id>/position/ | POST   | route_id: integer | {<br />"lat": 37.56442135,<br />"log": 127.0016985<br />}    | -                                                            |                                                              |
| share/\<int:route_id>/position/ | GET    | route_id: integer | -                                                            | {<br />"response": "success",<br />"data": [<br />{<br />"route": 3,<br />"lat": 37.5642135,<br />"log": 127.0016985<br />},<br />{<br />"route": 3,<br />"lat": 37.5642136,<br />"log": 127.0016986<br />}<br />]<br />} |                                                              |



### 로그인 하는 법

위에서 설명한대로 login/ 로 요청 시 token 값을 받음.

이를 request header에 넣어줌.

토큰값에는 "" 나 '' 없이 토큰값 자체만 넣어줌.

| KEY           | VALUE      |
| ------------- | ---------- |
| Authorization | JWT 토큰값 |





