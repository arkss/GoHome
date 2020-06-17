from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from .serializers import RouteSerializer, PositionSerializer
from .models import Position, Route


class ShareRouteView(APIView):
    permission_classes = [AllowAny]

    def post(self, request, *args, **kwargs):
        profile = request.user
        data = {
            'profile': profile.id
        }
        serializer = RouteSerializer(data=data)
        if serializer.is_valid():
            route = serializer.save()
        else:
            return Response({
                'response': 'error',
                'message': serializer.errors
            })

        return Response({
            'response': 'success',
            'message': 'route가 성공적으로 생성되었습니다.',
            'data': serializer.data
        })


class PositionView(APIView):
    permission_classes = [AllowAny]

    # 가장 작은 걸 어떻게 쿼리 날리지?
    def get(self, request, route_id, *args, **kwargs):

        queryset = Position.objects.filter(
            route=route_id).order_by('created_at')
        serializer = PositionSerializer(queryset, many=True)

        return Response({
            'response': 'success',
            'data': serializer.data
        })

    def post(self, request, route_id, *args, **kwargs):
        """
        {
            "lat": 37.5642135,
            "log": 127.0016985
        }
        """
        try:
            lat = request.data["lat"]
            log = request.data["log"]
        except KeyError:
            return Response({
                'response': 'error',
                'message': '입력 데이터가 잘못되었습니다.'
            })
        try:
            route = Route.objects.get(id=route_id)
        except:
            return Response({
                'response': 'error',
                'message': f'{route_id}에 해당하는 route가 존재하지 않습니다.'
            })
        data = {
            "route": route.id,
            "lat": lat,
            "log": log
        }
        serializer = PositionSerializer(data=data)
        if serializer.is_valid():
            position = serializer.save()
        else:
            return Response({
                'response': 'error',
                'message': serializer.errors
            })

        return Response({
            'response': 'success',
            'message': 'position이 성공적으로 생성되었습니다.',
            'data': serializer.data
        })
