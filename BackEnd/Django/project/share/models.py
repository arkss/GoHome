from django.db import models
from core.models import Profile


class Route(models.Model):
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f'{self.profile}의 {self.created_at}에 생성된 경로'


class Position(models.Model):
    route = models.ForeignKey(Route, on_delete=models.CASCADE)
    lat = models.FloatField()
    log = models.FloatField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f'{self.route}의 ({self.lat}, {self.log})'
