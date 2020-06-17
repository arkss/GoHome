from rest_framework import serializers as sz
from django.contrib.auth import get_user_model
from .models import Route, Position


class RouteSerializer(sz.ModelSerializer):
    route_id = sz.SerializerMethodField()

    def get_route_id(self, obj):
        return obj.id

    class Meta:
        model = Route
        fields = ['profile', 'route_id']


class PositionSerializer(sz.ModelSerializer):

    class Meta:
        model = Position
        fields = ['route', 'lat', 'log']


{
    "response": "success",
    "message": "route가 성공적으로 생성되었습니다.",
    "data": {
        "profile": 5,
        "route_id": 2
    }
}
