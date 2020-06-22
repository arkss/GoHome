# Django API gateway

### 명령어

```shell
# debug 서버 실행
python manage.py runserver --settings=config.settings.debug

# deploy 서버 실행
python manage.py runserver --settings=config.settings.deploy
```

> 현재는 uWSGI와 Nginx로 배포



### API 문서

#### 로그인 여부 테스트

* path : /

* method : GET

* request body

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

* request body

  | Name     | Type   | Mandatory | Default | Example    | Description |
  | -------- | ------ | --------- | ------- | ---------- | ----------- |
  | username | String | Y         | -       | rkdalstjd9 | 아이디      |
  | password | String | Y         | -       | qwe123     | 비밀번호    |

* response field

  | Name  | Type   | Mandatory | Default | Example | Description  |
  | ----- | ------ | --------- | ------- | ------- | ------------ |
  | token | String | Y         | -       | 토큰값  | access token |



#### Refresh Token

* path : refresh/

* method : POST

* request body

  | Name  | Type   | Mandatory | Default | Example | Description         |
  | ----- | ------ | --------- | ------- | ------- | ------------------- |
  | token | String | Y         | -       | 토큰값  | 만료된 access token |

* response field

  | Name  | Type   | Mandatory | Default | Example | Description  |
  | ----- | ------ | --------- | ------- | ------- | ------------ |
  | token | String | Y         | -       | 토큰값  | access token |



#### 회원가입

* path : signup/

* method : POST

* request body

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

* request body

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

* request body

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





### 로그인 하는 법

위에서 설명한대로 login/ 로 요청 시 token 값을 받음.

이를 request header에 넣어줌.

토큰값에는 "" 나 '' 없이 토큰값 자체만 넣어줌.

| KEY           | VALUE      |
| ------------- | ---------- |
| Authorization | JWT 토큰값 |





### 시간 측정

`calculate_response_time.py` 를 통해서 각 API의 시간을 측정하였다.

각 요청은 1000회씩 수행하였으며 평균을 측정하였다.

값은 소수점 둘째자리까지 구한다.

* 로그인 여부 테스트
  * path : /
  * 0.41
* 로그인
  * path : login/
  * 0.64
* 회원가입
  * path : signup/
  * 0.67
* 유저 상세 정보
  * path : profile/
  * 0.55
* 공유 경로 생성
  * path : share/route/
  * 0.43
* 공유 경로에 위치 좌표 생성
  * path : share/\<int:route_id>/position/
  * 0.56
* 공유 경로에 위치 좌표 조회
  * path : share/\<int:route_id>/position/
  * 0.69