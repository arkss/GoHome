# GoHome Server(Node.js)
## 개요
본 서버는 GoHome API Gateway Server와 통신하며 다음 기능을 RESTful API 형태로 제공한다.
- 위치 기반 자전거 대여소 검색
- 도보/공공자전거/N버스 중 일부를 포함하는 경로 탐색

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
- https://www.data.go.kr/useCase/1001467/exam.do