from django.urls import path
from . import  views
from rest_framework_jwt.views import obtain_jwt_token

urlpatterns = [
    path('', views.is_login),
    path('login/', obtain_jwt_token),
    path('signup/', views.CreateProfileView.as_view()),
]
