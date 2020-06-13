from django.urls import path
from . import views
from rest_framework_jwt.views import obtain_jwt_token, refresh_jwt_token

urlpatterns = [
    path('', views.login_test),
    path('login/', obtain_jwt_token),
    path('refresh/', refresh_jwt_token),
    path('signup/', views.CreateProfileView.as_view()),
    path('user_active/<str:uid64>/<str:token>/',
         views.user_active, name='user_active'),
]
