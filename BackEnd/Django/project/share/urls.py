from django.urls import path
from . import views

urlpatterns = [
    path('route/', views.ShareRouteView.as_view()),
    path('<int:route_id>/position/', views.PositionView.as_view()),
]
