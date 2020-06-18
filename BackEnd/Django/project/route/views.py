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


@api_view(['GET'])
def bike_stops(request):

    try:
        lat = request.GET['lat']
        lon = request.GET['lon']
        n = request.GET['n']
    except:
        return Response({
            'response': 'error',
            'message': 'request parameter가 잘못되었습니다.'
        })

    params = {
        'lat': lat,
        'lon': lon,
        'n': n
    }

    res = requests.get(
        f'{route_server}/bikestops',
        params=params
    )

    if res.status_code != 200:
        return Resonse({
            'response': 'error',
            'message': res.status_code
        })

    return Response(res.json())


@api_view(['GET'])
def bikestop_parked_counts(request):
    res = requests.get(
        f'{route_server}/bikestop_parked_counts',
    )

    if res.status_code != 200:
        return Response({
            'response': 'error',
            'message': res.status_code
        })

    return Response(res.json())


@api_view(['GET'])
def routes(request):
    params = request.GET
    # params = {
    #     'lat_start': lat_start,
    #     'lon_start': lon_start,
    #     'lat_end': lat_end,
    #     'lon_end': lon_end,
    #     'include_bike': include_bike,
    #     'include_bus': include_bus
    # }

    res = requests.get(
        f'{route_server}/routes',
        params=params
    )

    if res.status_code != 200:
        return Response({
            'response': 'error',
            'message': res.status_code
        })

    return Response(res.json())
