import time
import requests

URL = 'http://ec2-52-78-218-95.ap-northeast-2.compute.amazonaws.com/'

# 토큰값을 위한 로그인
data = {
    'username': 'rkdalstjd9',
    'password': 'qwe123'
}
response = requests.post(
    f'{URL}login/',
    data=data
)

try:
    token = response.json()['token']
except:
    raise Exception('로그인에 실패하였습니다.')

headers = {
    'Authorization': f'JWT {token}'
}

total_time = 0
request_cnt = 100

bikestops_URL = f'{URL}api/bikestops/'
bikestop_parked_counts_URL = f'{URL}api/bikestop_parked_counts/'
routes_URL = f'{URL}api/routes/'
routes_params = {
    'lat_start': 37.586759,
    'lon_start': 127.053907,
    'lat_end': 37.582931,
    'lon_end': 127.060980
}

for i in range(request_cnt):
    start = time.time()
    params = {
        'lat': 37.58396981971035,
        'lon': 127.05907344818158,
        'n': 5
    }
    response = requests.get(
        routes_URL,
        headers=headers,
        params=routes_params
    )
    end = time.time()
    total_time += end - start

print(total_time/request_cnt)
breakpoint()
