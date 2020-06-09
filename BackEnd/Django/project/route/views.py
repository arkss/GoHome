from django.shortcuts import render
from rest_framework.permissions import AllowAny
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
import requests
from .servers import route_server


@api_view(['GET'])
@permission_classes([AllowAny, ])
def route_test(request):
    res = requests.get(route_server)

    response = res.json()

    return Response(response)

# TODO: 이 아래 부터 permission_classes 전체적으로 변경


@api_view(['GET'])
@permission_classes([AllowAny, ])
def bike_stops(request, lat, lon, n):
    params = {
        'lat': lat,
        'lon': lon,
        'n': n
    }
    res = requests.get(
        f'{route_server}api/bikestops',
        params=params
    )

    return Response(res.json())


@api_view(['GET'])
@permission_classes([AllowAny, ])
def bikestop_parked_counts(request):
    res = requests.get(
        f'{route_server}api/bikestop_parked_counts',
    )

    return Response(res.json())


@api_view(['GET'])
@permission_classes([AllowAny, ])
def routes(request, lat_start, lon_start, lat_end, lon_end, include_bike, include_bus):
    params = {
        'lat_start': lat_start,
        'lon_start': lon_start,
        'lat_end': lat_end,
        'lon_end': lon_end,
        'include_bike': include_bike,
        'include_bus': include_bus
    }

    res = requests.get(
        f'{route_server}routes',
        params=params
    )

    return Response(res.json())
