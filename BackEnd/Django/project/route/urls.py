from django.urls import path
from . import views

urlpatterns = [
    path('', views.route_test),
    path('bikestops/', views.bike_stops),
    path('bikestop_parked_counts/', views.bikestop_parked_counts),
    path('routes/', views.routes),
]
