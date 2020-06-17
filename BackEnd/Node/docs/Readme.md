# GoHome Server(Node.js)
## 개요
본 서버는 GoHome API Gateway Server와 통신하며 다음 기능을 RESTful API 형태로 제공한다.
- 위치 기반 자전거 대여소 검색
- 도보/공공자전거/N버스 중 일부를 포함하는 경로 탐색



## 제공하는 OAPI

### 목록

#### 테스트

테스트를 위해 제공됨

- path: /

- method: GET

- request parameter

  - 없음

- response field
  
  - | Name    | Type   | Mandatory | Default | Example | Description |
    | ------- | ------ | --------- | ------- | ------- | ----------- |
    | message | String | Y         | hi      | hi      | Test        |



#### 자전거 대여소 목록 조회

주변의 따릉이 대여소 정보를 반환

- path: /api/bikestops

- method: GET

- request parameter

  - | Name | Type   | Mandatory | Default | Example            | Description             |
    | ---- | ------ | --------- | ------- | ------------------ | ----------------------- |
    | lat  | Number |           | 0       | 37.58396981971035  | 위도                    |
    | lon  | Number |           | 0       | 127.05907344818158 | 경도                    |
    | n    | Number |           | MAX     | 5                  | 검색할 대여소의 최대 수 |

- response field
  
  - | Name                               | Type   | Mandatory | Default   | Example    | Description                  |
    | ---------------------------------- | ------ | --------- | --------- | ---------- | ---------------------------- |
    | result                             | Number | Y         | 1         | 1          | 1: 정상 처리됨<br />-1: 오류 |
    | message                            | String | Y         | 'success' | '13 found' | 요청 처리 결과               |
    | data                               | Object | Y         |           |            |                              |
    | data.n                             | Number |           |           | 1          | 검색된 대여소 수             |
    | data.bikestops                     | Array  |           |           |            | 검색된 대여소 목록           |
    | data.bikestops[].rackTotCnt        | Number |           |           |            | 거치대 개수                  |
    | data.bikestops[].stationName       | String |           |           |            | 대여소 이름                  |
    | data.bikestops[].parkingBikeTotCnt | Number |           |           |            | 자전거 수                    |
    | data.bikestops[].shared            | Number |           |           |            | 거치율                       |
    | data.bikestops[].stationLatitude   | Number |           |           |            | 위도                         |
    | data.bikestops[].stationLongitude  | Number |           |           |            | 경도                         |
    | data.bikestops[].stationId         | String |           |           |            | 대여소 ID                    |
    | data.bikestops[].distance          | Number |           |           |            | 대여소까지의 직선 거리       |



#### 자전거 거치 수 조회

- path: /api/bikestop_parked_counts

- method: GET

- request parameter

  - 없음
- response field
  
  - | Name                               | Type   | Mandatory | Default   | Example    | Description                  |
    | ---------------------------------- | ------ | --------- | --------- | ---------- | ---------------------------- |
    | result                             | Number | Y         | 1         | 1          | 1: 정상 처리됨<br />-1: 오류 |
    | message                            | String | Y         | 'success' | '13 found' | 요청 처리 결과               |
    | data                               | Object | Y         |           |            |                              |
    | data.n                             | Number |           |           | 1          | 검색된 대여소 수             |
    | data.bikestops                     | Array  |           |           |            | 검색된 대여소 목록           |
    | data.bikestops[].stationId         | String |           |           |            | 대여소 ID                    |
    | data.bikestops[].parkingBikeTotCnt | Number |           |           |            | 자전거 수                    |





#### 경로탐색

- path: /routes

- method: GET

- request parameter

  - | Name         | Type   | Mandatory | Default | Example    | Description      |
    | ------------ | ------ | --------- | ------- | ---------- | ---------------- |
    | lat_start    | Number | Y         |         | 37.586759  | 출발지 위도      |
    | lon_start    | Number | Y         |         | 127.053907 | 출발지 경도      |
    | lat_end      | Number | Y         |         | 37.582931  | 도착지 위도      |
    | lon_end      | Number | Y         |         | 127.060980 | 도착지 경도      |
    | include_bike | String |           | Y       | N          | 저전거 포함 여부 |
    | include_bus  | String |           | Y       | N          | 버스 포함 여부   |


- response field
  
<<<<<<< HEAD
  - | Name                              | Type   | Mandatory | Default   | Example                                         | Description                                               |
    | --------------------------------- | ------ | --------- | --------- | ----------------------------------------------- | --------------------------------------------------------- |
    | result                            | Number | Y         | 1         | 1                                               | 1: 정상 처리됨<br />-1: 오류                              |
    | message                           | String | Y         | 'success' | '13 found'                                      | 요청 처리 결과                                            |
    | data                              | Object | Y         |           |                                                 |                                                           |
    | data.n                            | Number |           |           | 1                                               | 검색된 경로 수                                            |
    | data.routes                       | Array  |           |           |                                                 | 검색된 경로 목록                                          |
    | data.routes[].time                | Number |           |           | 2428                                            | 소요시간                                                  |
    | data.routes[].distance            | Number |           |           | 9900                                            | 이동 거리                                                 |
    | data.routes[].brief_list          | Array  |           |           | [1]                                             | 간단한 경로 내용<br />1: 도보<br />2: 자전거<br />3: 버스 |
    | data.routes[].sections            | Array  |           |           |                                                 | 경로의 구간 목록                                          |
    | data.routes[].sections[].time     | Array  |           |           | [641, 1341, 446]                                | 구간 별 소요시간                                          |
    | data.routes[].sections[].distance | Array  |           |           | [857, 8441, 602]                                | 구간 별 이동 거리                                         |
    | data.routes[].sections[].points   | Array  |           |           | [[126.99430937255515, 37.534636452132624], ...] | 경로 체크포인트(경도, 위도 순)                            |
    | data.routes[].sections[].type     | Number |           |           | 1                                               | 구간 타입<br />1: 도보<br />2: 자전거<br />3: 버스        |
    |                                   |        |           |           |                                                 |                                                           |
=======
  - | Name                           | Type   | Mandatory | Default   | Example                                         | Description                    |
    | ------------------------------ | ------ | --------- | --------- | ----------------------------------------------- | ------------------------------ |
    | result                         | Number | Y         | 1         | 1                                               | 1: 정상 처리됨<br />-1: 오류   |
    | message                        | String | Y         | 'success' | '13 found'                                      | 요청 처리 결과                 |
    | data                           | Object | Y         |           |                                                 |                                |
    | data.n                         | Number |           |           | 1                                               | 검색된 경로 수                 |
    | data.routes                    | Array  |           |           |                                                 | 검색된 경로 목록               |
    | data.routes[].time             | Number |           |           | 2428                                            | 소요시간                       |
    | data.routes[].distance         | Number |           |           | 9900                                            | 이동 거리                      |
    | data.routes[].brief_list       | Array  |           |           | ['도보']                                        | 간단한 경로 내용               |
    | data.routes[].section_time     | Array  |           |           | [641, 1341, 446]                                | 구간 별 소요시간               |
    | data.routes[].section_distance | Array  |           |           | [857, 8441, 602]                                | 구간 별 이동 거리              |
    | data.routes[].points           | Array  |           |           | [[126.99430937255515, 37.534636452132624], ...] | 경로 체크포인트(경도, 위도 순) |
>>>>>>> client/merge-AR




## 사용하는 OAPI
본 서버는 기능 제공을 위해 외부 기관에서 제공하는 Open API를 사용한다.
- API 인증키는 서버의 별도 파일(keys.json)에 저장되며, 이를 외부에 공유하지 않는다.
- 각 API는 무료로 제공되는 것이며, 일 최대 1,000건 요청할 수 있다. 세부목록이 있는 경우, 요청 횟수 제한은 각 항목별로 적용된다.

### 목록
- [공공데이터포털](https://www.data.go.kr/)
	- [노선정보조회 서비스](https://www.data.go.kr/dataset/15000193/openapi.do?mypageFlag=Y)
		- 문서
			- [서울특별시_노선정보조회_서비스_활용가이드_20190110.docx](./서울특별시_노선정보조회_서비스_활용가이드_20190110.docx)
		- 세부목록
			- **getBusRouteList**: 노선번호에 해당하는 노선 목록 조회
			- getRoutePathList: 노선의 지도상 경로를 리턴한다.
			- getRouteInfoItem: 노선 기본정보 조회
			- **getStaionsByRouteList**: 노선별 경유 정류소 조회 서비스
	- [버스위치정보조회 서비스](https://www.data.go.kr/dataset/15000332/openapi.do?mypageFlag=Y)
		- 문서
			- [서울특별시_버스위치정보조회_서비스_활용가이드_20190110.docx](./서울특별시_버스위치정보조회_서비스_활용가이드_20190110.docx)
		- 세부목록
			- ~~getPosImgByRouteStItem: 노선ID와 구간정보로 차량들의 위치정보 이미지를 요청한다.~~
			- ~~getPosImgByRtidItem: 노선ID로 차량들의 위치정보 이미지를 요청한다.~~
			- getLowBusPosByRouteStList: 노선ID와 구간정보로 저상차량들의 위치정보를 조회한다.
			- ~~getLowPosImgByRouteStItem: 노선ID와 구간정보로 저상버스들의 위치정보 이미지를 요청한다.~~
			- ~~getLowPosImgByRtidItem: 노선ID로 저상버스들의 위치정보 이미지를 요청한다.~~
			- getBusPosByRtidList: 노선ID로 차량들의 위치정보를 조회한다.
			- getBusPosByVehIdItem: 차량ID로 위치정보를 조회한다.
			- getLowBusPosByRtidList: 노선ID로 저상버스들의 위치정보를 조회한다.
			- getBusPosByRouteStList: 노선ID와 구간정보로 차량들의 위치정보를 조회한다.
	- [버스도착정보조회 서비스](https://www.data.go.kr/dataset/15000314/openapi.do?mypageFlag=Y)
		- 문서
			- [서울특별시_버스도착정보조회_서비스_활용가이드_20191011.docx](./서울특별시_버스도착정보조회_서비스_활용가이드_20191011.docx)
		- 세부목록
			- getLowArrInfoByRouteList: 한 정류소의 특정노선의 저상버스 도착예정정보를 조회한다.
			- getLowArrInfoByStIdList: 정류소ID로 저상버스 도착예정정보를 조회한다.
			- getArrInfoByRouteList: 한 정류소의 특정노선의 도착예정정보를 조회한다.
			- **getArrInfoByRouteAllList**: 경유노선 전체 정류소 도착예정정보를 조회한다.
	- [대중교통환승경로 조회 서비스](https://www.data.go.kr/dataset/15000414/openapi.do?mypageFlag=Y)
		- 문서
			- [서울특별시_대중교통환승경로_조회_서비스_활용가이드_20171208.docx](./서울특별시_대중교통환승경로_조회_서비스_활용가이드_20171208.docx)
		- 세부목록
			- getLocationInfoList: 출발지/목적지 명칭으로 검색
			- getPathInfoBySubwayList: 지하철이용 경로 조회
			- getPathInfoByBusNSubList: 버스 지하철 환승 경로 조회
			- getPathInfoByBusList: 버스이용 경로 조회
	- [정류소정보조회 서비스](https://www.data.go.kr/dataset/15000303/openapi.do?mypageFlag=Y)
		- 문서
			- [서울특별시_정류소정보조회_서비스_활용가이드_20190524.docx](./서울특별시_정류소정보조회_서비스_활용가이드_20190524.docx)
		- 세부목록
			- getStaionsByPosList: 좌표기반 근접 정류소 조회
			- getLowStaionByUidList: 고유번호를 입력받은 정류소의 저상버스 도착정보 제공
			- getLowStationByNameList: 저상버스가 운행되는 정류소 명칭 검색
			- getBustimeByStationList: 정류소고유번호와 노선id를 입력받아 첫차/막차예정시간을 조회한다.
			- getRouteByStationList: 정류소고유번호를 입력받아 경유하는 노선목록을 조회한다.
			- getStationByUidItem: 정류소고유번호를 입력받아 버스도착정보목록을 조회한다.
			- getStationByNameList: 정류소 명칭 검색
- [서울 열린데이터 광장](https://data.seoul.go.kr/)
	- [서울특별시 공공자전거 실시간 대여정보](https://data.seoul.go.kr/dataList/OA-15493/A/1/datasetView.do)
- [T map API](http://tmapapi.sktelecom.com/index.html)
	- [보행자 경로안내](http://tmapapi.sktelecom.com/main.html#webservice/docs/tmapRoutePedestrianDoc)



## 참고

- 위치정보
	- 모든 위치정보는 GPS(WGS84) 좌표계를 기준으로 한다.
- 대중교통 길찾기
	- [ODSay LAB](https://lab.odsay.com/)에서 대중교통 길찾기 API를 무료 제공하지만 일 최대 1,000건까지만 요청 가능하다. 유료 요금제 유형은 일 최대 100,000건 요청할 수 있지만 가격이 월 300만원이다. 따라서 ODSay LAB 대중교통 길찾기 API는 실질적 이용이 어렵다고 판단해 사용하지 않는다.
<<<<<<< HEAD
- https://www.data.go.kr/useCase/1001467/exam.dow
=======
- https://www.data.go.kr/useCase/1001467/exam.do
>>>>>>> client/merge-AR
