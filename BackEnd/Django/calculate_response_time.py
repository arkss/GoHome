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
request_cnt = 1000

for i in range(request_cnt):
    start = time.time()
    response = requests.get(f'{URL}share/1/position/', headers=headers)
    end = time.time()
    total_time += end - start

print(total_time/request_cnt)
