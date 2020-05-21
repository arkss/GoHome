from django.shortcuts import render
from django.contrib.auth import get_user_model
from django.http import JsonResponse
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework.decorators import api_view
from .serializers import ProfileSeriallizer

from config.permissions import CustomIsAuthenticated


class CreateProfileView(APIView):
    permission_classes = [AllowAny]

    def get(self, request, *args, **kwargs):
        queryset = get_user_model().objects.all()
        serializer = ProfileSeriallizer(queryset, many=True)

        return Response(serializer.data)

    def post(self, request, *args, **kwargs):
        """
        {
            "profile": {
                "username": "rkdalstjd9",
                "password": "qwe123",
                "email": "rkdalstjd9@naver.com",
                "nickname": "arkss"
            }
        }
        """
        data = request.data.get('profile')
        if not data:
            return Response({
                'response': 'error',
                'message': 'profile 파라미터가 없습니다.'
            })
        serializer = ProfileSeriallizer(data=data)
        if serializer.is_valid():
            profile = serializer.save()
        else:
            return Response({
                'response': 'error',
                'message': serializer.errors
            })

        return Response({
            'response': 'success',
            'message': 'profile이 성공적으로 생성되었습니다.'
        })


@api_view(['GET'])
def is_login(request):
    return Response({'message': '로그인 성공'})
