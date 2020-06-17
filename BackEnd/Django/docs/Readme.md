# Django API gateway

### 명령어

```shell
# debug 서버 실행
python manage.py runserver --settings=config.settings.debug

# deploy 서버 실행
python manage.py runserver --settings=config.settings.deploy
```



### API 문서

<<<<<<< HEAD
#### 로그인 여부 테스트

* path : /

* method : GET

* request parameter

  | Name | Type | Mandatory | Default | Example | Description |
  | ---- | ---- | --------- | ------- | ------- | ----------- |
  | -    | -    | -         | -       | -       | -           |

* response field

   | Name    | Type   | Mandatory | Default     | Example     | Description |
   | ------- | ------ | --------- | ----------- | ----------- | ----------- |
   | message | String | Y         | 로그인 성공 | 로그인 성공 | 테스트      |



#### 로그인

* path : login/

* method : POST

* request parameter

  | Name     | Type   | Mandatory | Default | Example    | Description |
  | -------- | ------ | --------- | ------- | ---------- | ----------- |
  | username | String | Y         | -       | rkdalstjd9 | 아이디      |
  | password | String | Y         | -       | qwe123     | 비밀번호    |

* response field

  | Name  | Type   | Mandatory | Default | Example | Description  |
  | ----- | ------ | --------- | ------- | ------- | ------------ |
  | token | String | Y         | -       | 토큰값  | access token |



#### 회원가입

* path : signup/

* method : POST

* request parameter

  | Name                   | Type   | Mandatory | Default | Example                           | Description            |
  | ---------------------- | ------ | --------- | ------- | --------------------------------- | ---------------------- |
  | profile                | Object | Y         | -       | -                                 | 아이디                 |
  | profile.username       | String | Y         | -       | qwe123                            | 비밀번호               |
  | profile.password       | String | Y         | -       | qwe123                            | 비밀번호               |
  | profile.email          | String | Y         | -       | rkdalstjd9@naver.com              | 이메일                 |
  | profile.nickname       | String | Y         | -       | arkss                             | 닉네임                 |
  | profile.address        | String | N         | ""      | 서울특별시 동대문구 전농동 103-45 | API에 검색 가능한 주소 |
  | profile.detail_address | String | N         | ""      | 주영리빙텔 108호                  | 상세 주소              |
  | profile.address_lat    | Float  | N         | 0.0     | 37.5642135                        | 위도                   |
  | profile.address_log    | Float  | N         | 0.0     | 127.0016985                       | 경도                   |

* response field

  | Name     | Type   | Mandatory | Default                              | Example                              | Description |
  | -------- | ------ | --------- | ------------------------------------ | ------------------------------------ | ----------- |
  | response | String | Y         | success                              | success                              | 성공        |
  | message  | String | Y         | profile이 성공적으로 생성되었습니다. | profile이 성공적으로 생성되었습니다. | 상세 메세지 |



#### 유저 상세 정보

* path : profile/

* method : GET

* request parameter

  | Name | Type | Mandatory | Default | Example | Description |
  | ---- | ---- | --------- | ------- | ------- | ----------- |
  | -    | -    | -         | -       | -       | -           |

* response field

| Name                | Type   | Mandatory | Default | Example                           | Description               |
| ------------------- | ------ | --------- | ------- | --------------------------------- | ------------------------- |
| response            | String | Y         | success | success                           | 성공                      |
| data                | Object | Y         | -       | -                                 | data를 감싸는 object 이름 |
| data.username       | String | Y         | -       | rkdalstjd9                        | 아이디                    |
| data.email          | String | Y         | -       | rkdalstjd9@naver.com              | 이메일                    |
| data.nickname       | String | Y         | -       | arkss                             | 닉네임                    |
| data.address        | String | Y         | -       | 서울특별시 동대문구 전농동 103-45 | API에 검색 가능한 주소    |
| data.detail_address | String | Y         | -       | 주영리빙텔 108호                  | 상세주소                  |
| data.address_lat    | Float  | Y         | -       | 37.5642135                        | 위도                      |
| data.address_log    | Float  | Y         | -       | 127.0016985                       | 경도                      |



#### 공유 경로 생성

* path : share/route/

* method : POST

* request parameter

  | Name | Type | Mandatory | Default | Example | Description |
  | ---- | ---- | --------- | ------- | ------- | ----------- |
  | -    | -    | -         | -       | -       | -           |

* response field

  | Name          | Type    | Mandatory | Default                            | Example                            | Description               |
  | ------------- | ------- | --------- | ---------------------------------- | ---------------------------------- | ------------------------- |
  | response      | String  | Y         | success                            | success                            | 성공                      |
  | message       | String  | Y         | route가 성공적으로 생성되었습니다. | route가 성공적으로 생성되었습니다. | 상세 메세지               |
  | data          | Object  | Y         | -                                  | -                                  | data를 감싸는 object 이름 |
  | data.profile  | Integer | Y         | -                                  | 5                                  | 유저의 아이디             |
  | data.route_id | Integer | Y         | -                                  | 2                                  | 생성한 경로의 아이디      |



#### 공유 경로에 위치 좌표 생성

* path : share/\<int:route_id>/position/

* method : POST

* request parameter

  | Name | Type  | Mandatory | Default | Example     | Description |
  | ---- | ----- | --------- | ------- | ----------- | ----------- |
  | lat  | Float | Y         | -       | 37.56442135 | 위도        |
  | log  | Float | Y         | -       | 127.0016985 | 경도        |

* response field

  | Name       | Type    | Mandatory | Default                               | Example                               | Description               |
  | ---------- | ------- | --------- | ------------------------------------- | ------------------------------------- | ------------------------- |
  | response   | String  | Y         | success                               | success                               | 성공                      |
  | message    | String  | Y         | position이 성공적으로 생성되었습니다. | position이 성공적으로 생성되었습니다. | 상세 메세지               |
  | data       | Object  | Y         | -                                     | -                                     | data를 감싸는 object 이름 |
  | data.route | Integer | Y         | -                                     | 2                                     | 경로의 아이디             |
  | data.lat   | Float   | Y         | -                                     | 37.56442135                           | 생성한 경로의 아이디      |
  | data.log   | Float   | Y         | -                                     | 127.0016985                           | 경도                      |



#### 공유 경로에 위치 좌표 조회

* path : share/\<int:route_id>/position/

* method : GET

* request parameter

  | Name | Type | Mandatory | Default | Example | Description |
  | ---- | ---- | --------- | ------- | ------- | ----------- |
  | -    | -    | -         | -       | -       | -           |

* response field

  | Name         | Type   | Mandatory | Default | Example     | Description      |
  | ------------ | ------ | --------- | ------- | ----------- | ---------------- |
  | response     | String | Y         | success | success     | 성공             |
  | data         | List   | Y         | -       | -           | data를 담는 배열 |
  | data[].route | Object | Y         | -       | 3           | 경로의 아이디    |
  | data[].lat   | Float  | Y         | -       | 37.56442135 | 위도             |
  | data[].log   | Float  | Y         | -       | 127.0016985 | 경도             |







### API 문서 (구) - 추후 삭제 예정

| URL                             | Method | URL Params        | body                                                         | success response                                             | error response                                               |
| ------------------------------- | ------ | ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /                               | GET    | -                 | -                                                            | {<br />   'message': '로그인 성공'<br />}                    | {     <br />    "detail": "Authentication credentials were not provided."<br /> } |
| login/                          | POST   | -                 | {<br/>    "username": "아이디",<br/> "password": "비밀번호"<br/>} | {     <br />"token": "토큰값"<br /> }                        | {    <br /> "non_field_errors": [         "Unable to log in with provided credentials."     ]<br /> } |
| signup/                         | GET    | -                 | -                                                            | [     <br />{        <br />"username": "root",         "email": "rkdalstjd0@namver.com",         "nickname": "",         "address": "",         "detail_address": ""     },<br />{        <br /> "username": "rkdalstjd9",         "email": "rkdalstjd9@naver.com",         "nickname": "arkss",         "address": "",         "detail_address": ""     <br />},<br />] | -                                                            |
| signup/                         | POST   | -                 | {<br/>	"profile":{<br/>		"username": "rkdalstjd9",<br/>		"password": "qwe123",<br/>		"email": "rkdalstjd9@naver.com",<br/>		"nickname": "arkss9",<br/>        "address": "서울특별시 동대문구 전농동 103-45",<br/>        "detail_address": "주영리빙텔 108호"<br/>	}<br/>} | {<br />"response": "success", "message": "profile이 성공적으로 생성되었습니다."<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러 메세지"<br />} |
| share/route/                    | POST   | -                 | -                                                            | {<br />"response": "success",<br />message: "route가 성공적으로 생성되었습니다.",<br />"data":{<br />"profile": 5,<br />"route_id": 2<br />}<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러메세지"<br />} |
| share/\<int:route_id>/position/ | POST   | route_id: integer | {<br />"lat": 37.56442135,<br />"log": 127.0016985<br />}    | -                                                            |                                                              |
| share/\<int:route_id>/position/ | GET    | route_id: integer | -                                                            | {<br />"response": "success",<br />"data": [<br />{<br />"route": 3,<br />"lat": 37.5642135,<br />"log": 127.0016985<br />},<br />{<br />"route": 3,<br />"lat": 37.5642136,<br />"log": 127.0016986<br />}<br />]<br />} |                                                              |
=======
* 여러 라이브러리를 사용하다보니 response의 양식이 일치하지 않음 -> 추후 통일 예정

| URL     | Method | URL Params | body                                                         | success response                                             | error response                                               | 비고                                                         |
| ------- | ------ | ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /       | GET    | -          | -                                                            | {<br />   'message': '로그인 성공'<br />}                    | {     <br />    "detail": "Authentication credentials were not provided."<br /> } | 로그인이 잘 되었는지 test 용 endpoint                        |
| login/  | POST   | -          | {<br/>    "username": "아이디",<br/> "password": "비밀번호"<br/>} | {     <br />"token": "토큰값"<br /> }                        | {    <br /> "non_field_errors": [         "Unable to log in with provided credentials."     ]<br /> } | token 값으로 어떻게 로그인 하는지 밑에서 부가 설명           |
| signup/ | GET    | -          | -                                                            | [     <br />{        <br />"username": "root",         "email": "rkdalstjd0@namver.com",         "nickname": "",         "address": "",         "detail_address": ""     },<br />{        <br /> "username": "rkdalstjd9",         "email": "rkdalstjd9@naver.com",         "nickname": "arkss",         "address": "",         "detail_address": ""     <br />},<br />] | -                                                            | 개발 할 때 유저 목록 확인하기 편하기 위해서 생성<br />추후 삭제 예정 |
| signup/ | POST   | -          | {<br /><br />"profile":{<br />"username": "아이디" "password": "비밀번호", "email": "이메일","nickname": "닉네임"}<br />} | {<br />"response": "success", "message": "profile이 성공적으로 생성되었습니다."<br />} | {<br />"response": "error",<br />"message": "상황에 맞는 에러 메세지"<br />} |                                                              |
|         |        |            |                                                              |                                                              |                                                              |                                                              |
>>>>>>> client/merge-AR



### 로그인 하는 법

위에서 설명한대로 login/ 로 요청 시 token 값을 받음.

이를 request header에 넣어줌.

토큰값에는 "" 나 '' 없이 토큰값 자체만 넣어줌.

| KEY           | VALUE      |
| ------------- | ---------- |
| Authorization | JWT 토큰값 |



<<<<<<< HEAD

=======
일단 이걸로 통신 테스트하고 이해했다싶으면 refresh token과 access token 설명하겠음.

두 토큰은 테스트 할 때는 계속 귀찮게 해서 일단 빼놓음.
>>>>>>> client/merge-AR

