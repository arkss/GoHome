from django.shortcuts import render
from rest_framework.permissions import AllowAny
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
import requests


@api_view(['GET'])
@permission_classes([AllowAny, ])
def route_test(request):
    r = requests.get('http://15.164.105.118/')
    # r = requests.get('https://api.github.com/events')
    print(r.json())
    print(r)
    breakpoint()
    print(r)
    return Response({
        'test': 'success'
    })
